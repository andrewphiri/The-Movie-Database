package com.drew.themoviedatabase.Navigation

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.drew.themoviedatabase.screens.Details.DetailsScreen
import com.drew.themoviedatabase.screens.Details.MovieDetailsScreen
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
                navigateToDetails = { movieId, ageRating, title ->
                    navController.navigate(
                        DetailsScreen(
                            movieId = movieId,
                            ageRating = ageRating,
                            title = title
                        )
                    )
                }
            )
        }
        composable<DetailsScreen> {
            val args = it.toRoute<DetailsScreen>()
            MovieDetailsScreen(
                movieId = args.movieId,
                ageRating = args.ageRating,
                title = args.title,
                navigateUp = { navController.navigateUp() }
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
