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
import com.drew.themoviedatabase.screens.Home.MoviesViewModel
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
        VideosList(
            modifier = modifier.padding(innerPadding),
            trailers = trailers,
        )
    }
}