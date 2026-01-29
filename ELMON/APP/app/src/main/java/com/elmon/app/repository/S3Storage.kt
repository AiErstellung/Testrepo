package com.elmon.app.repository

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TreeMap
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class S3Storage(
    endpoint: String,
    private val bucket: String,
    region: String,
    accessKey: String,
    secretKey: String,
    sessionToken: String?,
    private val httpClient: OkHttpClient
) {
    private val bucketBaseUrl: HttpUrl
    private val bucketBaseUrlString: String
    private val signer = AwsRequestSigner(accessKey, secretKey, region, "s3", sessionToken)
    private val emptyPayloadHash = sha256Hex(ByteArray(0))

    init {
        val parsedEndpoint = endpoint.toHttpUrl()
        bucketBaseUrl = parsedEndpoint.newBuilder()
            .addPathSegment(bucket)
            .build()
        bucketBaseUrlString = bucketBaseUrl.toString().trimEnd('/')
    }

    fun presignGetObject(key: String, expiresInSeconds: Long = 300): String {
        val url = urlForKey(key)
        return signer.presign("GET", url, expiresInSeconds).toString()
    }

    fun readObject(key: String): ByteArray {
        val url = urlForKey(key)
        val headers = signer.sign("GET", url, emptyMap(), emptyPayloadHash)
        val request = Request.Builder().url(url).get().apply {
            headers.forEach { (name, value) -> addHeader(name, value) }
        }.build()
        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Failed to download $key (${response.code})")
            }
            return response.body?.bytes()
                ?: throw IOException("Empty response body for $key")
        }
    }

    private fun urlForKey(key: String): HttpUrl {
        val sanitizedKey = key.trim().trimStart('/')
        if (sanitizedKey.isBlank()) {
            throw IllegalArgumentException("Object key must not be empty")
        }
        return bucketBaseUrl.newBuilder()
            .addPathSegments(sanitizedKey)
            .build()
    }

    fun writeObject(key: String, bytes: ByteArray, contentType: String = "application/octet-stream") {
        val url = urlForKey(key)
        val headers = signer.sign(
            "PUT",
            url,
            mapOf("content-type" to contentType),
            sha256Hex(bytes)
        )
        val request = Request.Builder()
            .url(url)
            .put(bytes.toRequestBody(contentType.toMediaType()))
            .apply { headers.forEach { (name, value) -> addHeader(name, value) } }
            .build()
        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Failed to write $key (${response.code})")
            }
        }
    }

    private class AwsRequestSigner(
        private val accessKey: String,
        private val secretKey: String,
        private val region: String,
        private val service: String,
        private val sessionToken: String?
    ) {
        private val isoFormatter =
            DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").withZone(ZoneOffset.UTC)
        private val dateFormatter = DateTimeFormatter.BASIC_ISO_DATE.withZone(ZoneOffset.UTC)

        fun sign(
            method: String,
            url: HttpUrl,
            additionalHeaders: Map<String, String>,
            payloadHash: String
        ): Map<String, String> {
            val headers = TreeMap<String, String>()
            additionalHeaders.forEach { (key, value) ->
                headers[key.lowercase(Locale.US)] = value.trim()
            }
            headers["host"] = hostHeader(url)
            val now = Instant.now()
            val amzDate = isoFormatter.format(now)
            val dateStamp = dateFormatter.format(now)
            headers["x-amz-date"] = amzDate
            headers["x-amz-content-sha256"] = payloadHash
            sessionToken?.takeIf { it.isNotBlank() }?.let {
                headers["x-amz-security-token"] = it.trim()
            }

            val signedHeaders = headers.keys.joinToString(";")
            val canonicalHeaders = headers.entries.joinToString("\n") { "${it.key}:${it.value}" } + "\n"
            val canonicalRequest = listOf(
                method,
                url.encodedPath,
                url.encodedQuery ?: "",
                canonicalHeaders,
                signedHeaders,
                payloadHash
            ).joinToString("\n")
            val credentialScope = "$dateStamp/$region/$service/aws4_request"
            val stringToSign = "AWS4-HMAC-SHA256\n$amzDate\n$credentialScope\n" +
                sha256Hex(canonicalRequest.toByteArray(StandardCharsets.UTF_8))
            val signingKey = getSigningKey(secretKey, dateStamp, region, service)
            val signature = bytesToHex(
                hmacSha256(signingKey, stringToSign.toByteArray(StandardCharsets.UTF_8))
            )
            val authorization =
                "AWS4-HMAC-SHA256 Credential=$accessKey/$credentialScope, SignedHeaders=$signedHeaders, Signature=$signature"
            headers["authorization"] = authorization
            return headers
        }

        fun presign(
            method: String,
            url: HttpUrl,
            expiresInSeconds: Long,
            additionalQueryParameters: Map<String, String> = emptyMap()
        ): HttpUrl {
            val now = Instant.now()
            val amzDate = isoFormatter.format(now)
            val dateStamp = dateFormatter.format(now)
            val credentialScope = "$dateStamp/$region/$service/aws4_request"

            val queryParameters = TreeMap<String, String>()
            addQueryParam(queryParameters, "x-amz-algorithm", "AWS4-HMAC-SHA256")
            addQueryParam(queryParameters, "x-amz-credential", "$accessKey/$credentialScope")
            addQueryParam(queryParameters, "x-amz-date", amzDate)
            addQueryParam(queryParameters, "x-amz-expires", expiresInSeconds.toString())
            addQueryParam(queryParameters, "x-amz-signedheaders", "host")
            sessionToken?.takeIf { it.isNotBlank() }?.let { addQueryParam(queryParameters, "x-amz-security-token", it) }
            additionalQueryParameters.forEach { addQueryParam(queryParameters, it.key, it.value) }

            val canonicalQuery = canonicalQueryString(queryParameters)
            val canonicalHeaders = "host:${hostHeader(url)}\n"
            val canonicalRequest = listOf(
                method,
                url.encodedPath,
                canonicalQuery,
                canonicalHeaders,
                "host",
                UNSIGNED_PAYLOAD
            ).joinToString("\n")
            val stringToSign =
                "AWS4-HMAC-SHA256\n$amzDate\n$credentialScope\n${sha256Hex(canonicalRequest.toByteArray(StandardCharsets.UTF_8))}"
            val signingKey = getSigningKey(secretKey, dateStamp, region, service)
            val signature = bytesToHex(
                hmacSha256(signingKey, stringToSign.toByteArray(StandardCharsets.UTF_8))
            )
            addQueryParam(queryParameters, "x-amz-signature", signature)
            val finalQuery = canonicalQueryString(queryParameters)
            return url.newBuilder().encodedQuery(finalQuery).build()
        }

        private fun addQueryParam(map: MutableMap<String, String>, key: String, value: String) {
            map[awsEncode(key)] = awsEncode(value)
        }

        private fun canonicalQueryString(map: Map<String, String>): String =
            map.entries.joinToString("&") { "${it.key}=${it.value}" }

        private fun awsEncode(value: String): String =
            URLEncoder.encode(value, StandardCharsets.UTF_8)
                .replace("+", "%20")
                .replace("%7E", "~")

        private fun hostHeader(url: HttpUrl): String {
            val defaultPort = if (url.scheme == "https") 443 else 80
            return if (url.port != defaultPort) "${url.host}:${url.port}" else url.host
        }
    }
}

private const val UNSIGNED_PAYLOAD = "UNSIGNED-PAYLOAD"

private fun getSigningKey(secretKey: String, dateStamp: String, region: String, service: String): ByteArray {
    val kSecret = ("AWS4$secretKey").toByteArray(StandardCharsets.UTF_8)
    val kDate = hmacSha256(kSecret, dateStamp.toByteArray(StandardCharsets.UTF_8))
    val kRegion = hmacSha256(kDate, region.toByteArray(StandardCharsets.UTF_8))
    val kService = hmacSha256(kRegion, service.toByteArray(StandardCharsets.UTF_8))
    return hmacSha256(kService, "aws4_request".toByteArray(StandardCharsets.UTF_8))
}

private fun sha256Hex(bytes: ByteArray): String {
    val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
    return bytesToHex(digest)
}

private fun hmacSha256(key: ByteArray, data: ByteArray): ByteArray {
    val algorithm = "HmacSHA256"
    val mac = Mac.getInstance(algorithm)
    mac.init(SecretKeySpec(key, algorithm))
    return mac.doFinal(data)
}

private fun bytesToHex(bytes: ByteArray): String {
    val builder = StringBuilder(bytes.size * 2)
    for (byte in bytes) {
        builder.append(String.format("%02x", byte))
    }
    return builder.toString()
}
