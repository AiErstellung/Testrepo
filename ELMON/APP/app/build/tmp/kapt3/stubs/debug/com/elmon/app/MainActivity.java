package com.elmon.app;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u0018\u001a\u00020\u00192\b\u0010\u001a\u001a\u0004\u0018\u00010\u001bH\u0014R\u001b\u0010\u0003\u001a\u00020\u00048BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006R\u001b\u0010\t\u001a\u00020\n8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\r\u0010\b\u001a\u0004\b\u000b\u0010\fR\u001b\u0010\u000e\u001a\u00020\u000f8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0012\u0010\b\u001a\u0004\b\u0010\u0010\u0011R\u001b\u0010\u0013\u001a\u00020\u00148BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0017\u0010\b\u001a\u0004\b\u0015\u0010\u0016\u00a8\u0006\u001c"}, d2 = {"Lcom/elmon/app/MainActivity;", "Landroidx/activity/ComponentActivity;", "()V", "httpClient", "Lokhttp3/OkHttpClient;", "getHttpClient", "()Lokhttp3/OkHttpClient;", "httpClient$delegate", "Lkotlin/Lazy;", "repository", "Lcom/elmon/app/repository/VideoRepository;", "getRepository", "()Lcom/elmon/app/repository/VideoRepository;", "repository$delegate", "storage", "Lcom/elmon/app/repository/S3Storage;", "getStorage", "()Lcom/elmon/app/repository/S3Storage;", "storage$delegate", "viewModel", "Lcom/elmon/app/ui/VideoReviewViewModel;", "getViewModel", "()Lcom/elmon/app/ui/VideoReviewViewModel;", "viewModel$delegate", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "app_debug"})
public final class MainActivity extends androidx.activity.ComponentActivity {
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy httpClient$delegate = null;
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy storage$delegate = null;
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy repository$delegate = null;
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy viewModel$delegate = null;
    
    public MainActivity() {
        super(0);
    }
    
    private final okhttp3.OkHttpClient getHttpClient() {
        return null;
    }
    
    private final com.elmon.app.repository.S3Storage getStorage() {
        return null;
    }
    
    private final com.elmon.app.repository.VideoRepository getRepository() {
        return null;
    }
    
    private final com.elmon.app.ui.VideoReviewViewModel getViewModel() {
        return null;
    }
    
    @java.lang.Override
    protected void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
}