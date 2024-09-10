package com.drew.themoviedatabase.Navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.drew.themoviedatabase.screens.Cast.PersonDetailsScreen
import com.drew.themoviedatabase.screens.Cast.CastDetailsScreen
import com.drew.themoviedatabase.screens.Details.DetailsMovieScreen
import com.drew.themoviedatabase.screens.Details.DetailsTVScreen
import com.drew.themoviedatabase.screens.Details.MovieDetailsScreen
import com.drew.themoviedatabase.screens.Details.TVDetailsScreen
import com.drew.themoviedatabase.screens.Home.HomeScreen
import com.drew.themoviedatabase.screens.Profile.ProfileScreen
import com.drew.themoviedatabase.screens.Reviews.MoviesReviewsScreen
import com.drew.themoviedatabase.screens.Reviews.MovieUserReviewsScreen
import com.drew.themoviedatabase.screens.Reviews.TVReviewsScreen
import com.drew.themoviedatabase.screens.Reviews.TVUserReviewsScreen
import com.drew.themoviedatabase.screens.Search.SearchScreen
import com.drew.themoviedatabase.screens.Videos.VideosScreen

@Composable
fun MovieNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = HomeNavGraph,
        route = RootNavGraph::class
        ) {
        homeNavGraph(navController)
        detailsNavGraph(navController)
        searchNavGraph(navController)
        profileNavGraph(navController)
        videoNavGraph(navController)
    }
}

fun NavGraphBuilder.homeNavGraph(
    navController: NavHostController
) {
    navigation<HomeNavGraph>(
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
    }
}

fun NavGraphBuilder.detailsNavGraph(
    navController: NavHostController,
) {
    navigation<DetailsGraph>(
        startDestination = DetailsMovieScreen(0),
    ) {
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
                        },
                        navigateToCastDetails = {personId ->

                            navController.navigate(
                                PersonDetailsScreen(
                                    personId = personId
                                )
                            )
                        },
                        navigateToReviews = {id ->
                            navController.navigate(
                                MoviesReviewsScreen(
                                    id = id
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
                        },
                        navigateToCastDetails = {personId ->
                            navController.navigate(
                                PersonDetailsScreen(
                                    personId = personId
                                )
                            )
                        },
                        navigateToReviews = {id ->
                            navController.navigate(
                                TVReviewsScreen(
                                    id = id
                                )
                            )
                        }
                    )
                }
                composable<PersonDetailsScreen> {
                    val args = it.toRoute<PersonDetailsScreen>()
                    CastDetailsScreen(
                        personId = args.personId,
                        navigateUp = { navController.navigateUp() },
                        navigateToMovieDetails = { movieId ->
                            navController.navigate(
                                DetailsMovieScreen(
                                    movieId = movieId,
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
                composable<MoviesReviewsScreen>{
                    val args = it.toRoute<MoviesReviewsScreen>()
                    MovieUserReviewsScreen(
                        id = args.id,
                        onNavigateBack = { navController.navigateUp()}
                    )
                }
                composable<TVReviewsScreen> {
                    val args = it.toRoute<TVReviewsScreen>()
                    TVUserReviewsScreen(
                        id = args.id,
                        onNavigateBack = { navController.navigateUp()}
                    )
                }

        }

}

fun NavGraphBuilder.searchNavGraph(
    navController: NavHostController
) {
    navigation<SearchNavGraph>(
        startDestination = SearchScreen,
    ) {
        composable<SearchScreen> {
            SearchScreen(
                navigateToMovieDetailsScreen = { movieId ->
                    navController.navigate(
                        DetailsMovieScreen(
                            movieId = movieId,
                        )
                    )
                },
                navigateToCastDetailsScreen = { personId ->
                    navController.navigate(
                        PersonDetailsScreen(
                            personId = personId
                        )
                    )
                },
                navigateToTVShowDetailsScreen = { seriesId ->
                    navController.navigate(
                        DetailsTVScreen(
                            seriesId = seriesId,
                        )
                    )
                }
            )

        }
    }
}

fun  NavGraphBuilder.profileNavGraph(
    navController: NavHostController
) {
    navigation<ProfileNavGraph>(
        startDestination = ProfileScreen,
    ) {
        composable<ProfileScreen> {
            ProfileScreen()
        }
    }
}

fun NavGraphBuilder.videoNavGraph(
    navController: NavHostController
) {
    navigation<VideosNavGraph>(
        startDestination = VideosScreen,
    ) {
        composable<VideosScreen> {
            VideosScreen()
        }
    }
}


