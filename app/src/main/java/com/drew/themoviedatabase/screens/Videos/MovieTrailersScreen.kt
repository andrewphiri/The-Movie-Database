package com.drew.themoviedatabase.screens.Videos

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drew.themoviedatabase.MovieTopAppBar
import com.drew.themoviedatabase.screens.commonComposeUi.VideosPager
import com.drew.themoviedatabase.screens.Details.MoviesViewModel
import kotlinx.serialization.Serializable

@Serializable
data class MovieTrailersNavScreen(
    val movieID: Int
)

@Composable
fun MovieTrailersScreen(
    modifier: Modifier = Modifier,
    movieID: Int,
    moviesViewModel: MoviesViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    canNavigateBack: Boolean = true
) {
    val trailers by moviesViewModel.trailers.observeAsState()

    LaunchedEffect(Unit) {
        moviesViewModel.fetchTrailer(movieID)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(top = 0.dp, bottom = 0.dp),
       topBar = {
           MovieTopAppBar(
               canNavigateBack = canNavigateBack,
               title = "Videos",
               navigateUp = onNavigateBack
           )
       }
    ) { innerPadding ->
//        VideosList(
//            modifier = modifier.padding(innerPadding),
//            trailers = trailers,
//            listState = listState
//        )
        trailers?.map { it?.key }?.let {
            VideosPager(
                modifier = modifier.padding(innerPadding),
                trailers = it,
                videoIdsString = it.joinToString(separator = ",")
            )
        }
    }
}

