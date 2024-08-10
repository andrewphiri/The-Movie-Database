package com.drew.themoviedatabase.ComposeUtils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefresh(
    modifier: Modifier = Modifier,
    onRefresh: () -> Unit,
    isRefreshing: Boolean,
    content: @Composable () -> Unit
) {
    val pullToRefreshState = rememberPullToRefreshState()

    //
    val alpha = remember { mutableStateOf(0f) }

    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            onRefresh()
        }
    }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            pullToRefreshState.startRefresh()
        } else {
            pullToRefreshState.endRefresh()
        }
    }

    LaunchedEffect(pullToRefreshState.progress) {
        alpha.value = pullToRefreshState.progress
    }

    Box(
      modifier = modifier
          .fillMaxSize()
          .nestedScroll(pullToRefreshState.nestedScrollConnection)
    ) {
        content()
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.TopCenter),
            visible = alpha.value > 0f || isRefreshing
        ) {
            PullToRefreshContainer(
                modifier = Modifier
                    .align(Alignment.TopCenter),
                state = pullToRefreshState
            )
        }

    }
}