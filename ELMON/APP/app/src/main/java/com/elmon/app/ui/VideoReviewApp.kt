package com.elmon.app.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.elmon.app.R
import com.elmon.app.data.model.VideoItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VideoReviewApp(viewModel: VideoReviewViewModel) {
    val uiState by viewModel.state.collectAsState()
    val pagerState = rememberPagerState(pageCount = { uiState.videos.size })
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var pendingFeedbackVideo by remember { mutableStateOf<VideoItem?>(null) }
    var pendingFeedbackPage by remember { mutableStateOf(0) }

    val handleRating: (VideoItem, Boolean, Int, String?) -> Unit = { video, liked, page, feedback ->
        scope.launch {
            val maxPage = (pagerState.pageCount - 1).coerceAtLeast(0)
            val nextPage = (page + 1).coerceAtMost(maxPage)
            if (pagerState.pageCount > 1) {
                pagerState.animateScrollToPage(nextPage)
            }
        }
        viewModel.rateVideo(video, liked, feedback)
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    LaunchedEffect(uiState.videos.size) {
        if (uiState.videos.isNotEmpty() && pagerState.currentPage >= uiState.videos.size) {
            pagerState.scrollToPage(uiState.videos.lastIndex)
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(title = { Text(stringResource(R.string.app_name)) })
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.videos.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.no_videos),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Pull to refresh",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                else -> {
                    VideoFeed(
                        videos = uiState.videos,
                        pagerState = pagerState,
                        onLike = { video, page ->
                            handleRating(video, true, page, null)
                        },
                        onDislike = { video, page ->
                            pendingFeedbackVideo = video
                            pendingFeedbackPage = page
                        }
                    )
                }
            }
            pendingFeedbackVideo?.let { video ->
                BadFeedbackDialog(
                    video = video,
                    onDismiss = { pendingFeedbackVideo = null },
                    onSubmit = { comment ->
                        handleRating(video, false, pendingFeedbackPage, comment)
                        pendingFeedbackVideo = null
                    }
                )
            }
        }
    }
}

@Composable
private fun BadFeedbackDialog(
    video: VideoItem,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var comment by rememberSaveable { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Was gefällt dir an \"${video.title}\" nicht?")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Beschreibe kurz, was dich an dem Clip gestört hat.")
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Feedback") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSubmit(comment.trim()) }) {
                Text("Speichern")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )
}
