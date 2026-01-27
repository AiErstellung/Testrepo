package com.elmon.app.repository

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
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
    private val httpClient: OkHttpClient
) {
    private val bucketBaseUrl: HttpUrl
    private val bucketBaseUrlString: String
    private val signer = AwsRequestSigner(accessKey, secretKey, region, "s3")
    private val json = Json { encodeDefaults = true }
    private val emptyPayloadHash = sha256Hex(ByteArray(0))

    init {
        val parsedEndpoint = endpoint.toHttpUrl()
        bucketBaseUrl = parsedEndpoint.newBuilder()
            .addPathSegment(bucket)
            .build()
        bucketBaseUrlString = bucketBaseUrl.toString().trimEnd('/')
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

    fun copyObject(sourceKey: String, destinationKey: String) {
        val url = urlForKey(destinationKey)
        val extraHeaders = mapOf(
            "x-amz-copy-source" to buildCopySourceHeader(sourceKey),
            "x-amz-metadata-directive" to "COPY"
        )
        val headers = signer.sign("PUT", url, extraHeaders, emptyPayloadHash)
        val request = Request.Builder().url(url)
            .put(ByteArray(0).toRequestBody(null))
            .apply { headers.forEach { (name, value) -> addHeader(name, value) } }
            .build()
        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Failed to copy $sourceKey to $destinationKey (${response.code})")
            }
        }
    }

    fun deleteObject(key: String) {
        val url = urlForKey(key)
        val headers = signer.sign("DELETE", url, emptyMap(), emptyPayloadHash)
        val request = Request.Builder().url(url).delete()
            .apply { headers.forEach { (name, value) -> addHeader(name, value) } }
            .build()
        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Failed to delete $key (${response.code})")
            }
        }
    }

    fun uploadFeedback(videoId: String, comment: String, timestamp: Long) {
        val payloadKey = "bad/feedback/${videoId}.json"
        val payload = FeedbackPayload(videoId, comment, timestamp)
        val bodyBytes = json.encodeToString(payload).toByteArray(StandardCharsets.UTF_8)
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val url = urlForKey(payloadKey)
        val headers = signer.sign(
            "PUT",
            url,
            mapOf("content-type" to "application/json; charset=utf-8"),
            sha256Hex(bodyBytes)
        )
        val request = Request.Builder()
            .url(url)
            .put(bodyBytes.toRequestBody(mediaType))
            .apply { headers.forEach { (name, value) -> addHeader(name, value) } }
            .build()
        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Failed to upload feedback for $videoId (${response.code})")
            }
        }
    }

    fun keyFromUrl(url: String): String {
        val normalized = url.toHttpUrl().toString().trimEnd('/')
        val prefix = "$bucketBaseUrlString/"
        if (!normalized.startsWith(prefix)) {
            throw IllegalArgumentException(
                "Cannot derive S3 key from $url. Ensure it uses $bucketBaseUrlString/ or provide s3Key."
            )
        }
        return normalized.removePrefix(prefix).trimStart('/')
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

    private fun buildCopySourceHeader(sourceKey: String): String {
        val sanitizedKey = sourceKey.trim().trimStart('/')
        val encodedSegments = sanitizedKey.split('/').joinToString("/") { canonicalize(it) }
        return "/$bucket/$encodedSegments"
    }

    private fun canonicalize(value: String): String {
        if (value.isEmpty()) {
            return ""
        }
        val allowed = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_.~"
        val builder = StringBuilder()
        for (ch in value) {
            builder.append(
                if (allowed.contains(ch)) {
                    ch
                } else {
                    String.format(Locale.US, "%%%02X", ch.code)
                }
            )
        }
        return builder.toString()
    }

    @Serializable
    private data class FeedbackPayload(
        val videoId: String,
        val comment: String,
        val timestamp: Long
    )

    private class AwsRequestSigner(
        private val accessKey: String,
        private val secretKey: String,
        private val region: String,
        private val service: String
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

        private fun hostHeader(url: HttpUrl): String {
            val defaultPort = if (url.scheme == "https") 443 else 80
            return if (url.port != defaultPort) "${url.host}:${url.port}" else url.host
        }
    }
}

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
