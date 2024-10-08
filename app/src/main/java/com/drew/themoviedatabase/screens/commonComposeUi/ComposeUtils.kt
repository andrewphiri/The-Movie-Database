package com.drew.themoviedatabase.screens.commonComposeUi

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.drew.themoviedatabase.ui.theme.DarkOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefresh(
    modifier: Modifier = Modifier,
    onRefresh: () -> Unit,
    isRefreshing: Boolean,
    content: @Composable BoxScope.() -> Unit
) {
    val pullToRefreshState = rememberPullToRefreshState()
//
//    //
//    val alpha = remember { mutableStateOf(0f) }
//
//    if (pullToRefreshState) {
//        LaunchedEffect(true) {
//            onRefresh()
//        }
//    }
//
//    LaunchedEffect(isRefreshing) {
//        if (isRefreshing) {
//            pullToRefreshState.startRefresh()
//        } else {
//            pullToRefreshState.endRefresh()
//        }
//    }
//
//    LaunchedEffect(pullToRefreshState) {
//        alpha.value = pullToRefreshState.progress
//    }

    Box(
      modifier = modifier
          .fillMaxSize()
    ) {
        PullToRefreshBox(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxSize(),
            state = pullToRefreshState,
            content = content,
            indicator = {
                Indicator(
                    modifier = Modifier. align(Alignment. TopCenter),
                    isRefreshing = isRefreshing,
                    color = DarkOrange,
                    state = pullToRefreshState
                )
            },
            isRefreshing = isRefreshing,
            onRefresh = onRefresh
        )
    }
}