package com.elmon.app.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010#\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\n2\u0006\u0010\u0015\u001a\u00020\u0016J\u0006\u0010\u0017\u001a\u00020\u0013J\b\u0010\u0018\u001a\u00020\u0013H\u0002R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00070\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011\u00a8\u0006\u0019"}, d2 = {"Lcom/elmon/app/ui/VideoReviewViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/elmon/app/repository/VideoRepository;", "(Lcom/elmon/app/repository/VideoRepository;)V", "_state", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/elmon/app/ui/VideoFeedState;", "allVideos", "", "Lcom/elmon/app/data/model/VideoItem;", "ratedIds", "", "", "state", "Lkotlinx/coroutines/flow/StateFlow;", "getState", "()Lkotlinx/coroutines/flow/StateFlow;", "rateVideo", "", "video", "liked", "", "refresh", "updateState", "app_debug"})
public final class VideoReviewViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull
    private final com.elmon.app.repository.VideoRepository repository = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<com.elmon.app.ui.VideoFeedState> _state = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<com.elmon.app.ui.VideoFeedState> state = null;
    @org.jetbrains.annotations.NotNull
    private java.util.List<com.elmon.app.data.model.VideoItem> allVideos;
    @org.jetbrains.annotations.NotNull
    private final java.util.Set<java.lang.String> ratedIds = null;
    
    public VideoReviewViewModel(@org.jetbrains.annotations.NotNull
    com.elmon.app.repository.VideoRepository repository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<com.elmon.app.ui.VideoFeedState> getState() {
        return null;
    }
    
    public final void refresh() {
    }
    
    public final void rateVideo(@org.jetbrains.annotations.NotNull
    com.elmon.app.data.model.VideoItem video, boolean liked) {
    }
    
    private final void updateState() {
    }
}