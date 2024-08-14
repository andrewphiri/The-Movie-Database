package com.drew.themoviedatabase.Navigation

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.drew.themoviedatabase.screens.Details.DetailsMovieScreen
import com.drew.themoviedatabase.screens.Details.DetailsTVScreen
import com.drew.themoviedatabase.screens.Details.MovieDetailsScreen
import com.drew.themoviedatabase.screens.Details.TVDetailsScreen
import com.drew.themoviedatabase.screens.Home.HomeScreen

@Composable
fun MovieNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = HomeScreen,
        ) {
        composable<HomeScreen> {
            HomeScreen(
                navigateToMovieDetails = { movieId ->
                    navController.navigate(
                        DetailsMovieScreen(
                            movieId = movieId
                        )
                    )
                },
                navigateToTVShowDetails = { seriesId ->
                    navController.navigate(
                        DetailsTVScreen(
                            seriesId = seriesId,
                        )
                    )
                }
            )
        }
        composable<DetailsMovieScreen> {
            val args = it.toRoute<DetailsMovieScreen>()
            MovieDetailsScreen(
                movieId = args.movieId,
                navigateUp = { navController.navigateUp() },
                navigateToDetails = { movieId ->
                    navController.navigate(
                        DetailsMovieScreen(
                            movieId = movieId,
                        )
                    )
                }
            )
        }
        composable<DetailsTVScreen> {
            val args = it.toRoute<DetailsTVScreen>()
            TVDetailsScreen(
                seriesId = args.seriesId,
                navigateUp = { navController.navigateUp() },
                navigateToTVShowDetails = { movieId ->
                    navController.navigate(
                        DetailsTVScreen(
                            seriesId = movieId,
                        )
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieTopAppBar(
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {},
    title: String,
    canNavigateBack: Boolean = false
) {
    if (canNavigateBack) {
        TopAppBar(
            modifier =
            modifier.height(56.dp),
            title = {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(),
            navigationIcon = {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Navigate Back"
                    )
                }
            }
        )
    } else {
        TopAppBar(title = { Text(title) }, modifier = modifier,)
    }

}
