package com.elmon.app.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.elmon.app.data.model.VideoItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoFeed(
    videos: List<VideoItem>,
    pagerState: PagerState,
    onRate: (VideoItem, Boolean, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    VerticalPager(
        modifier = modifier,
        state = pagerState,
        beyondBoundsPageCount = 1
    ) { page ->
        val video = videos[page]
        val isActive = pagerState.currentPage == page

        Box(modifier = Modifier.fillMaxSize()) {
            VideoPlayer(
                url = video.url,
                isActive = isActive,
                modifier = Modifier.fillMaxSize()
            )

            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                color = Color.Transparent
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = video.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            ) {
                RatingControls(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onLike = { onRate(video, true, page) },
                    onDislike = { onRate(video, false, page) }
                )
            }
        }
    }
}
