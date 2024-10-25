package com.drew.themoviedatabase.screens.Genre

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.drew.themoviedatabase.MovieTopAppBar
import com.drew.themoviedatabase.Utilities.getWatchRegion
import com.drew.themoviedatabase.screens.commonComposeUi.LoadingSpinner
import com.drew.themoviedatabase.screens.commonComposeUi.MovieItem
import com.drew.themoviedatabase.screens.commonComposeUi.MovieItemByGenre
import com.drew.themoviedatabase.screens.commonComposeUi.TVShowItem
import kotlinx.serialization.Serializable

@Serializable
data class MoviesByGenreNavScreen(
    val genreID: Int,
    val genreName: String
)

@Composable
fun MoviesByGenreScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    navigateUp: () -> Unit = {},
    genreID: Int,
    genreName: String,
    navigateToMoviesDetailsScreen: (Int) -> Unit,
    genreViewModel: GenreViewModel = hiltViewModel()
) {
    val moviesByGenre = genreViewModel.getMoviesByGenre(genreID).collectAsLazyPagingItems()
    var isloading by rememberSaveable  { mutableStateOf(true) }

    if (moviesByGenre.itemCount > 0 && moviesByGenre.loadState.refresh is LoadState.NotLoading) {
        isloading = false
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(top = 20.dp),
        topBar = {
            MovieTopAppBar(
                canNavigateBack = canNavigateBack,
                title = "$genreName Movies",
                navigateUp = navigateUp
            )
        }
    ) { innerPadding ->

        Box(modifier = Modifier.padding(innerPadding)) {

            if (isloading) {
                LoadingSpinner()
            } else {
                LazyVerticalStaggeredGrid (
                    columns = StaggeredGridCells.Adaptive(120.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalItemSpacing = 16.dp,
                ) {
                    items(moviesByGenre.itemCount) { index ->
                        moviesByGenre[index]?.let {
                            MovieItemByGenre(
                                movie = it,
                                onItemClick = navigateToMoviesDetailsScreen,
                                watchRegion = getWatchRegion()
                            )
                        }
                    }

                }
            }

        }

    }
}