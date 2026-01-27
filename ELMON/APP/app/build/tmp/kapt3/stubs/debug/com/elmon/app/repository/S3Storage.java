package com.elmon.app.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0010\u0012\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0004\u0018\u00002\u00020\u0001:\u0002&\'B5\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\u0010\u0010\u0013\u001a\u00020\u00032\u0006\u0010\u0014\u001a\u00020\u0003H\u0002J\u0010\u0010\u0015\u001a\u00020\u00032\u0006\u0010\u0016\u001a\u00020\u0003H\u0002J\u0016\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0014\u001a\u00020\u00032\u0006\u0010\u0019\u001a\u00020\u0003J\u000e\u0010\u001a\u001a\u00020\u00182\u0006\u0010\u001b\u001a\u00020\u0003J\u000e\u0010\u001c\u001a\u00020\u00032\u0006\u0010\u001d\u001a\u00020\u0003J\u000e\u0010\u001e\u001a\u00020\u001f2\u0006\u0010\u001b\u001a\u00020\u0003J\u001e\u0010 \u001a\u00020\u00182\u0006\u0010!\u001a\u00020\u00032\u0006\u0010\"\u001a\u00020\u00032\u0006\u0010#\u001a\u00020$J\u0010\u0010%\u001a\u00020\f2\u0006\u0010\u001b\u001a\u00020\u0003H\u0002R\u000e\u0010\u0004\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006("}, d2 = {"Lcom/elmon/app/repository/S3Storage;", "", "endpoint", "", "bucket", "region", "accessKey", "secretKey", "httpClient", "Lokhttp3/OkHttpClient;", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lokhttp3/OkHttpClient;)V", "bucketBaseUrl", "Lokhttp3/HttpUrl;", "bucketBaseUrlString", "emptyPayloadHash", "json", "Lkotlinx/serialization/json/Json;", "signer", "Lcom/elmon/app/repository/S3Storage$AwsRequestSigner;", "buildCopySourceHeader", "sourceKey", "canonicalize", "value", "copyObject", "", "destinationKey", "deleteObject", "key", "keyFromUrl", "url", "readObject", "", "uploadFeedback", "videoId", "comment", "timestamp", "", "urlForKey", "AwsRequestSigner", "FeedbackPayload", "app_debug"})
public final class S3Storage {
    @org.jetbrains.annotations.NotNull
    private final java.lang.String bucket = null;
    @org.jetbrains.annotations.NotNull
    private final okhttp3.OkHttpClient httpClient = null;
    @org.jetbrains.annotations.NotNull
    private final okhttp3.HttpUrl bucketBaseUrl = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String bucketBaseUrlString = null;
    @org.jetbrains.annotations.NotNull
    private final com.elmon.app.repository.S3Storage.AwsRequestSigner signer = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.serialization.json.Json json = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String emptyPayloadHash = null;
    
    public S3Storage(@org.jetbrains.annotations.NotNull
    java.lang.String endpoint, @org.jetbrains.annotations.NotNull
    java.lang.String bucket, @org.jetbrains.annotations.NotNull
    java.lang.String region, @org.jetbrains.annotations.NotNull
    java.lang.String accessKey, @org.jetbrains.annotations.NotNull
    java.lang.String secretKey, @org.jetbrains.annotations.NotNull
    okhttp3.OkHttpClient httpClient) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final byte[] readObject(@org.jetbrains.annotations.NotNull
    java.lang.String key) {
        return null;
    }
    
    public final void copyObject(@org.jetbrains.annotations.NotNull
    java.lang.String sourceKey, @org.jetbrains.annotations.NotNull
    java.lang.String destinationKey) {
    }
    
    public final void deleteObject(@org.jetbrains.annotations.NotNull
    java.lang.String key) {
    }
    
    public final void uploadFeedback(@org.jetbrains.annotations.NotNull
    java.lang.String videoId, @org.jetbrains.annotations.NotNull
    java.lang.String comment, long timestamp) {
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String keyFromUrl(@org.jetbrains.annotations.NotNull
    java.lang.String url) {
        return null;
    }
    
    private final okhttp3.HttpUrl urlForKey(java.lang.String key) {
        return null;
    }
    
    private final java.lang.String buildCopySourceHeader(java.lang.String sourceKey) {
        return null;
    }
    
    private final java.lang.String canonicalize(java.lang.String value) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\b\u0004\b\u0002\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0007J\u0010\u0010\f\u001a\u00020\u00032\u0006\u0010\r\u001a\u00020\u000eH\u0002J>\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030\u00102\u0006\u0010\u0011\u001a\u00020\u00032\u0006\u0010\r\u001a\u00020\u000e2\u0012\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030\u00102\u0006\u0010\u0013\u001a\u00020\u0003R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\b\u001a\n \n*\u0004\u0018\u00010\t0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000b\u001a\n \n*\u0004\u0018\u00010\t0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lcom/elmon/app/repository/S3Storage$AwsRequestSigner;", "", "accessKey", "", "secretKey", "region", "service", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "dateFormatter", "Ljava/time/format/DateTimeFormatter;", "kotlin.jvm.PlatformType", "isoFormatter", "hostHeader", "url", "Lokhttp3/HttpUrl;", "sign", "", "method", "additionalHeaders", "payloadHash", "app_debug"})
    static final class AwsRequestSigner {
        @org.jetbrains.annotations.NotNull
        private final java.lang.String accessKey = null;
        @org.jetbrains.annotations.NotNull
        private final java.lang.String secretKey = null;
        @org.jetbrains.annotations.NotNull
        private final java.lang.String region = null;
        @org.jetbrains.annotations.NotNull
        private final java.lang.String service = null;
        private final java.time.format.DateTimeFormatter isoFormatter = null;
        private final java.time.format.DateTimeFormatter dateFormatter = null;
        
        public AwsRequestSigner(@org.jetbrains.annotations.NotNull
        java.lang.String accessKey, @org.jetbrains.annotations.NotNull
        java.lang.String secretKey, @org.jetbrains.annotations.NotNull
        java.lang.String region, @org.jetbrains.annotations.NotNull
        java.lang.String service) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.util.Map<java.lang.String, java.lang.String> sign(@org.jetbrains.annotations.NotNull
        java.lang.String method, @org.jetbrains.annotations.NotNull
        okhttp3.HttpUrl url, @org.jetbrains.annotations.NotNull
        java.util.Map<java.lang.String, java.lang.String> additionalHeaders, @org.jetbrains.annotations.NotNull
        java.lang.String payloadHash) {
            return null;
        }
        
        private final java.lang.String hostHeader(okhttp3.HttpUrl url) {
            return null;
        }
    }
    
    @kotlinx.serialization.Serializable
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0083\b\u0018\u0000 #2\u00020\u0001:\u0002\"#B5\b\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\b\u0010\t\u001a\u0004\u0018\u00010\n\u00a2\u0006\u0002\u0010\u000bB\u001d\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u00a2\u0006\u0002\u0010\fJ\t\u0010\u0012\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\bH\u00c6\u0003J\'\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\bH\u00c6\u0001J\u0013\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0019\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u001a\u001a\u00020\u0005H\u00d6\u0001J!\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u00002\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!H\u00c7\u0001R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000e\u00a8\u0006$"}, d2 = {"Lcom/elmon/app/repository/S3Storage$FeedbackPayload;", "", "seen1", "", "videoId", "", "comment", "timestamp", "", "serializationConstructorMarker", "Lkotlinx/serialization/internal/SerializationConstructorMarker;", "(ILjava/lang/String;Ljava/lang/String;JLkotlinx/serialization/internal/SerializationConstructorMarker;)V", "(Ljava/lang/String;Ljava/lang/String;J)V", "getComment", "()Ljava/lang/String;", "getTimestamp", "()J", "getVideoId", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "toString", "write$Self", "", "self", "output", "Lkotlinx/serialization/encoding/CompositeEncoder;", "serialDesc", "Lkotlinx/serialization/descriptors/SerialDescriptor;", "$serializer", "Companion", "app_debug"})
    static final class FeedbackPayload {
        @org.jetbrains.annotations.NotNull
        private final java.lang.String videoId = null;
        @org.jetbrains.annotations.NotNull
        private final java.lang.String comment = null;
        private final long timestamp = 0L;
        @org.jetbrains.annotations.NotNull
        public static final com.elmon.app.repository.S3Storage.FeedbackPayload.Companion Companion = null;
        
        public FeedbackPayload(@org.jetbrains.annotations.NotNull
        java.lang.String videoId, @org.jetbrains.annotations.NotNull
        java.lang.String comment, long timestamp) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String getVideoId() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String getComment() {
            return null;
        }
        
        public final long getTimestamp() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String component2() {
            return null;
        }
        
        public final long component3() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull
        public final com.elmon.app.repository.S3Storage.FeedbackPayload copy(@org.jetbrains.annotations.NotNull
        java.lang.String videoId, @org.jetbrains.annotations.NotNull
        java.lang.String comment, long timestamp) {
            return null;
        }
        
        @java.lang.Override
        public boolean equals(@org.jetbrains.annotations.Nullable
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override
        @org.jetbrains.annotations.NotNull
        public java.lang.String toString() {
            return null;
        }
        
        @kotlin.jvm.JvmStatic
        public static final void write$Self(@org.jetbrains.annotations.NotNull
        com.elmon.app.repository.S3Storage.FeedbackPayload self, @org.jetbrains.annotations.NotNull
        kotlinx.serialization.encoding.CompositeEncoder output, @org.jetbrains.annotations.NotNull
        kotlinx.serialization.descriptors.SerialDescriptor serialDesc) {
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0003J\u0018\u0010\b\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\n0\tH\u00d6\u0001\u00a2\u0006\u0002\u0010\u000bJ\u0011\u0010\f\u001a\u00020\u00022\u0006\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\u0019\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0002H\u00d6\u0001R\u0014\u0010\u0004\u001a\u00020\u00058VX\u00d6\u0005\u00a2\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0014"}, d2 = {"com/elmon/app/repository/S3Storage.FeedbackPayload.$serializer", "Lkotlinx/serialization/internal/GeneratedSerializer;", "Lcom/elmon/app/repository/S3Storage$FeedbackPayload;", "()V", "descriptor", "Lkotlinx/serialization/descriptors/SerialDescriptor;", "getDescriptor", "()Lkotlinx/serialization/descriptors/SerialDescriptor;", "childSerializers", "", "Lkotlinx/serialization/KSerializer;", "()[Lkotlinx/serialization/KSerializer;", "deserialize", "decoder", "Lkotlinx/serialization/encoding/Decoder;", "serialize", "", "encoder", "Lkotlinx/serialization/encoding/Encoder;", "value", "app_debug"})
        @java.lang.Deprecated
        public static final class $serializer implements kotlinx.serialization.internal.GeneratedSerializer<com.elmon.app.repository.S3Storage.FeedbackPayload> {
            @org.jetbrains.annotations.NotNull
            public static final com.elmon.app.repository.S3Storage.FeedbackPayload.$serializer INSTANCE = null;
            
            private $serializer() {
                super();
            }
            
            @java.lang.Override
            @org.jetbrains.annotations.NotNull
            public kotlinx.serialization.KSerializer<?>[] childSerializers() {
                return null;
            }
            
            @java.lang.Override
            @org.jetbrains.annotations.NotNull
            public com.elmon.app.repository.S3Storage.FeedbackPayload deserialize(@org.jetbrains.annotations.NotNull
            kotlinx.serialization.encoding.Decoder decoder) {
                return null;
            }
            
            @java.lang.Override
            @org.jetbrains.annotations.NotNull
            public kotlinx.serialization.descriptors.SerialDescriptor getDescriptor() {
                return null;
            }
            
            @java.lang.Override
            public void serialize(@org.jetbrains.annotations.NotNull
            kotlinx.serialization.encoding.Encoder encoder, @org.jetbrains.annotations.NotNull
            com.elmon.app.repository.S3Storage.FeedbackPayload value) {
            }
            
            @java.lang.Override
            @org.jetbrains.annotations.NotNull
            public kotlinx.serialization.KSerializer<?>[] typeParametersSerializers() {
                return null;
            }
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u00c6\u0001\u00a8\u0006\u0006"}, d2 = {"Lcom/elmon/app/repository/S3Storage$FeedbackPayload$Companion;", "", "()V", "serializer", "Lkotlinx/serialization/KSerializer;", "Lcom/elmon/app/repository/S3Storage$FeedbackPayload;", "app_debug"})
        public static final class Companion {
            
            private Companion() {
                super();
            }
            
            @org.jetbrains.annotations.NotNull
            public final kotlinx.serialization.KSerializer<com.elmon.app.repository.S3Storage.FeedbackPayload> serializer() {
                return null;
            }
        }
    }
}