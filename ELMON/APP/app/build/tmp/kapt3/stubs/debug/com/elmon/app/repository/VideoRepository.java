package com.elmon.app.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0018\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\nH\u0002J\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0010J\u0017\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\u000eH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0010J\u0017\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\n0\u0014H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0010J+\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u000f2\u0006\u0010\u0018\u001a\u00020\u00192\b\u0010\u001a\u001a\u0004\u0018\u00010\nH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u001bJ\u0010\u0010\u001c\u001a\u00020\n2\u0006\u0010\u0017\u001a\u00020\u000fH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u001d"}, d2 = {"Lcom/elmon/app/repository/VideoRepository;", "", "storage", "Lcom/elmon/app/repository/S3Storage;", "dao", "Lcom/elmon/app/data/db/VideoRatingDao;", "(Lcom/elmon/app/repository/S3Storage;Lcom/elmon/app/data/db/VideoRatingDao;)V", "json", "Lkotlinx/serialization/json/Json;", "destinationKey", "", "sourceKey", "targetFolder", "fetchPendingVideos", "", "Lcom/elmon/app/data/model/VideoItem;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllRatings", "Lcom/elmon/app/data/model/VideoRating;", "getRatedIds", "", "rateVideo", "", "video", "liked", "", "feedback", "(Lcom/elmon/app/data/model/VideoItem;ZLjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "resolveKey", "app_debug"})
public final class VideoRepository {
    @org.jetbrains.annotations.NotNull
    private final com.elmon.app.repository.S3Storage storage = null;
    @org.jetbrains.annotations.NotNull
    private final com.elmon.app.data.db.VideoRatingDao dao = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.serialization.json.Json json = null;
    
    public VideoRepository(@org.jetbrains.annotations.NotNull
    com.elmon.app.repository.S3Storage storage, @org.jetbrains.annotations.NotNull
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
    com.elmon.app.data.model.VideoItem video, boolean liked, @org.jetbrains.annotations.Nullable
    java.lang.String feedback, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object getAllRatings(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.util.List<com.elmon.app.data.model.VideoRating>> $completion) {
        return null;
    }
    
    private final java.lang.String resolveKey(com.elmon.app.data.model.VideoItem video) {
        return null;
    }
    
    private final java.lang.String destinationKey(java.lang.String sourceKey, java.lang.String targetFolder) {
        return null;
    }
}