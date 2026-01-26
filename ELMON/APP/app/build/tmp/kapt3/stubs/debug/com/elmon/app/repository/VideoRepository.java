package com.elmon.app.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u000eJ\u0017\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00100\fH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u000eJ\u0017\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00030\u0012H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u000eJ!\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00032\u0006\u0010\u0016\u001a\u00020\u0017H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0018R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u0019"}, d2 = {"Lcom/elmon/app/repository/VideoRepository;", "", "videosJsonUrl", "", "httpClient", "Lokhttp3/OkHttpClient;", "dao", "Lcom/elmon/app/data/db/VideoRatingDao;", "(Ljava/lang/String;Lokhttp3/OkHttpClient;Lcom/elmon/app/data/db/VideoRatingDao;)V", "json", "Lkotlinx/serialization/json/Json;", "fetchPendingVideos", "", "Lcom/elmon/app/data/model/VideoItem;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllRatings", "Lcom/elmon/app/data/model/VideoRating;", "getRatedIds", "", "rateVideo", "", "videoId", "liked", "", "(Ljava/lang/String;ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class VideoRepository {
    @org.jetbrains.annotations.NotNull
    private final java.lang.String videosJsonUrl = null;
    @org.jetbrains.annotations.NotNull
    private final okhttp3.OkHttpClient httpClient = null;
    @org.jetbrains.annotations.NotNull
    private final com.elmon.app.data.db.VideoRatingDao dao = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.serialization.json.Json json = null;
    
    public VideoRepository(@org.jetbrains.annotations.NotNull
    java.lang.String videosJsonUrl, @org.jetbrains.annotations.NotNull
    okhttp3.OkHttpClient httpClient, @org.jetbrains.annotations.NotNull
    com.elmon.app.data.db.VideoRatingDao dao) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object fetchPendingVideos(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.util.List<com.elmon.app.data.model.VideoItem>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object getRatedIds(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.util.Set<java.lang.String>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object rateVideo(@org.jetbrains.annotations.NotNull
    java.lang.String videoId, boolean liked, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object getAllRatings(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.util.List<com.elmon.app.data.model.VideoRating>> $completion) {
        return null;
    }
}