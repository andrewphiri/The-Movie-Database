package com.drew.themoviedatabase.screens.Videos

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drew.themoviedatabase.MovieTopAppBar
import com.drew.themoviedatabase.composeUI.VideosList
import com.drew.themoviedatabase.screens.Home.TVShowsViewModel
import kotlinx.serialization.Serializable

@Serializable
data class TvTrailersNavScreen(
    val seriesID: Int
)

@Composable
fun TvTrailersScreen(
    modifier: Modifier = Modifier,
    seriesID: Int,
    tvShowsViewModel: TVShowsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    canNavigateBack: Boolean = true
) {
    val trailers by tvShowsViewModel.trailers.observeAsState()

    LaunchedEffect(Unit) {
        tvShowsViewModel.fetchTrailer(seriesID)
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
        VideosList(
            modifier = modifier.padding(innerPadding),
            trailers = trailers,
        )
    }
}