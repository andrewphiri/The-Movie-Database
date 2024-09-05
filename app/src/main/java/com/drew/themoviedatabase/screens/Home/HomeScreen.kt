package com.drew.themoviedatabase.screens.Home

import android.content.res.Resources
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.drew.themoviedatabase.ComposeUtils.PullToRefresh
import com.drew.themoviedatabase.POJO.MovieDetailsReleaseData
import com.drew.themoviedatabase.POJO.TVShowDetails
import com.drew.themoviedatabase.composeUI.MovieItem
import com.drew.themoviedatabase.composeUI.MovieList
import com.drew.themoviedatabase.composeUI.TVShowItem
import com.drew.themoviedatabase.composeUI.TVShowList
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable
object HomeScreen

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    moviesViewModel: MoviesViewModel = hiltViewModel(),
    tvShowsViewModel: TVShowsViewModel = hiltViewModel(),
    navigateToMovieDetails: (Int) -> Unit,
    navigateToTVShowDetails: (Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val lazyListState = rememberLazyListState()
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    // Loading state
    var isLoading by remember { mutableStateOf(true) }
    val isRefreshing by moviesViewModel.isRefreshing.observeAsState(false)

    var popularMovies by remember { mutableStateOf<List<MovieDetailsReleaseData?>?>(null) }
    var topRatedMovies by remember { mutableStateOf<List<MovieDetailsReleaseData?>?>(null) }
    var nowPlayingMovies by remember { mutableStateOf<List<MovieDetailsReleaseData?>?>(null) }
    var upcomingMovies by remember { mutableStateOf<List<MovieDetailsReleaseData?>?>(null) }
    var trendingMovies by remember { mutableStateOf<List<MovieDetailsReleaseData?>?>(null) }

    var popularTVShows by remember { mutableStateOf<List<TVShowDetails?>?>(null) }
    var topRatedTVShows by remember { mutableStateOf<List<TVShowDetails?>?>(null) }
    var onTheAirTVShows by remember { mutableStateOf<List<TVShowDetails?>?>(null) }
    var airingTodayTVShows by remember { mutableStateOf<List<TVShowDetails?>?>(null) }



    // Observing movie lists
    moviesViewModel.popularMovies.observe(lifecycleOwner) {
        popularMovies = it?.sortedByDescending { it?.popularity }
    }
    moviesViewModel.topRatedMovies.observe(lifecycleOwner){
        topRatedMovies = it
    }
    moviesViewModel.nowPlayingMovies.observe(lifecycleOwner) {
        nowPlayingMovies = it
    }
    moviesViewModel.upcomingMovies.observe(lifecycleOwner) {
        upcomingMovies = it?.filter { it?.status != "Released" }
    }

    moviesViewModel.trendingMovies.observe(lifecycleOwner) {
        trendingMovies = it
    }

    // Observing TV show lists
    tvShowsViewModel.popularTVShows.observe(lifecycleOwner) {
        popularTVShows = it
    }
    tvShowsViewModel.topRatedTVShows.observe(lifecycleOwner){
        topRatedTVShows = it
    }
    tvShowsViewModel.onTheAirTVShows.observe(lifecycleOwner) {
        onTheAirTVShows = it
    }
    tvShowsViewModel.airingTodayTVShows.observe(lifecycleOwner) {
        airingTodayTVShows = it
    }

        if (popularMovies?.isNotEmpty() == true
            && topRatedMovies?.isNotEmpty() == true
            && nowPlayingMovies?.isNotEmpty() == true
            && trendingMovies?.isNotEmpty() == true
            && upcomingMovies?.isNotEmpty() == true
        ) {
        isLoading = false
    }

    // UI
    Scaffold(
        modifier = modifier,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            PullToRefresh(
                onRefresh = {
                    coroutineScope.launch {
                        try {
                            moviesViewModel.setRefreshing(true)
                            delay(2000)
                            async { moviesViewModel.fetchPopularMovies(3) }.await()
                            async { moviesViewModel.fetchTopRatedMovies(3) }.await()
                            async { moviesViewModel.fetchNowPlayingMovies(3) }.await()
                            async { moviesViewModel.fetchUpcomingMovies() }.await()
                            async { moviesViewModel.fetchTrendingMovies() }.await()
                            moviesViewModel.setRefreshing(false)
                        } catch (e : Exception) {
                            e.printStackTrace()
                        }
                    }
                },
                isRefreshing = isRefreshing,
            ) {
                if (isLoading) {
                    // Display loading spinner
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(50.dp)
                    )
                } else {
                    // Display movie lists
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = lazyListState,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {


                        if (upcomingMovies?.isNotEmpty() == true) {
                            // Upcoming Movies
                            item {
                                upcomingMovies?.let { upcoming ->
                                    MovieList(
                                        movies = upcoming.sortedBy { it?.releaseDate },
                                        categoryTitle = "Upcoming Movies",
                                        color = Color.Red,
                                        onItemClick = navigateToMovieDetails
                                    )
                                }
                            }
                        }

                        if (popularTVShows?.isNotEmpty() == true) {
                            item {
                                TVShowList(
                                    tvShows = popularTVShows?.sortedByDescending { it?.popularity },
                                    categoryTitle = "Popular TV Shows",
                                    color = Color.Red,
                                    onItemClick = navigateToTVShowDetails
                                )
                            }
                        }


                        if (trendingMovies?.isNotEmpty() == true) {
                            // Trending Movies
                            item {
                                trendingMovies?.let { trending ->
                                    MovieList(
                                        movies = trending,
                                        categoryTitle = "Trending Movies",
                                        color = Color.Green,
                                        onItemClick = navigateToMovieDetails
                                    )
                                }
                            }
                        }


                        if (topRatedTVShows?.isNotEmpty() == true) {
                            item {
                                TVShowList(
                                    tvShows = topRatedTVShows?.sortedByDescending { it?.voteAverage },
                                    categoryTitle = "Top Rated TV Shows",
                                    color = Color.Red,
                                    onItemClick = navigateToTVShowDetails)
                            }
                        }


                        if (popularMovies?.isNotEmpty() == true) {
                            // Popular Movies
                            item {
                                popularMovies?.let { popular ->
                                    MovieList(
                                        movies = popular,
                                        categoryTitle = "Popular Movies",
                                        color = Color.Blue,
                                        onItemClick = navigateToMovieDetails
                                    )
                                }
                            }
                        }

                        if (onTheAirTVShows?.isNotEmpty() == true) {
                            item {
                                TVShowList(
                                    tvShows = onTheAirTVShows,
                                    categoryTitle = "On The Air TV Shows",
                                    color = Color.Red,
                                    onItemClick = navigateToTVShowDetails
                                )
                            }
                        }

                        if (nowPlayingMovies?.isNotEmpty() == true) {
                            // Now Playing Movies
                            item {
                                nowPlayingMovies?.let { nowPlaying ->
                                    MovieList(
                                        movies = nowPlaying,
                                        categoryTitle = "Now Playing Movies",
                                        color = Color.Yellow,
                                        onItemClick = navigateToMovieDetails
                                    )
                                }
                            }
                        }

                        if (topRatedMovies?.isNotEmpty() == true) {
                            // Top Rated Movies
                            item {
                                topRatedMovies?.let { topRated ->
                                    MovieList(
                                        movies = topRated.sortedByDescending { it?.voteAverage },
                                        categoryTitle = "Top Rated Movies",
                                        color = Color.Magenta,
                                        onItemClick = navigateToMovieDetails
                                    )
                                }
                            }
                        }

                        if (airingTodayTVShows?.isNotEmpty() == true) {
                            item {
                                TVShowList(
                                    tvShows = airingTodayTVShows,
                                    categoryTitle = "Airing Today TV Shows",
                                    color = Color.Red,
                                    onItemClick = navigateToTVShowDetails
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SmoothScrolling(
    listState: LazyListState
) {
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect{ visibleItems ->
                val totalItems = listState.layoutInfo.totalItemsCount
                val lastVisibleItem = visibleItems.lastOrNull()?.index ?: 0
                if (lastVisibleItem >= totalItems - 1) {
//                    listState.scroll()
                }
            }
    }
}






