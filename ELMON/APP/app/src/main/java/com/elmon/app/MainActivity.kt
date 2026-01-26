package com.elmon.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.elmon.app.data.db.AppDatabase
import com.elmon.app.repository.VideoRepository
import com.elmon.app.ui.theme.ElmonVideoReviewerTheme
import com.elmon.app.ui.VideoReviewApp
import com.elmon.app.ui.VideoReviewViewModel
import com.elmon.app.ui.VideoReviewViewModelFactory
import okhttp3.OkHttpClient

class MainActivity : ComponentActivity() {
    private val repository by lazy {
        VideoRepository(
            BuildConfig.VIDEOS_JSON_URL,
            OkHttpClient(),
            AppDatabase.getInstance(this).videoRatingDao()
        )
    }

    private val viewModel: VideoReviewViewModel by viewModels {
        VideoReviewViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ElmonVideoReviewerTheme {
                VideoReviewApp(viewModel)
            }
        }
    }
}
