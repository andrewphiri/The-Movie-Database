package com.drew.themoviedatabase.screens.Home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.drew.themoviedatabase.ComposeUtils.PullToRefresh
import com.drew.themoviedatabase.POJO.MovieDetailsReleaseData
import com.drew.themoviedatabase.POJO.TVShowDetails
import com.drew.themoviedatabase.composeUI.MovieList
import com.drew.themoviedatabase.composeUI.TVShowList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

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
    var isLoading by rememberSaveable { mutableStateOf(true) }
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
    val moviesPopular  = moviesViewModel.moviesPopular.collectAsLazyPagingItems()
    val moviesTopRated = moviesViewModel.moviesTopRated.collectAsLazyPagingItems()
    val moviesNowPlaying = moviesViewModel.moviesNowPlaying.collectAsLazyPagingItems()
    val moviesUpcoming = moviesViewModel.moviesUpcoming.collectAsLazyPagingItems()
    val moviesTrending = moviesViewModel.moviesTrending.collectAsLazyPagingItems()

    val tvShowsPopular = tvShowsViewModel.tvShowsPopular.collectAsLazyPagingItems()
    val tvShowsTopRated = tvShowsViewModel.tvShowsTopRated.collectAsLazyPagingItems()
    val tvShowsAiringTodayTVShows = tvShowsViewModel.tvShowsAiringToday.collectAsLazyPagingItems()
    val tvShowsOnTheAirTVShows = tvShowsViewModel.tvShowsOnTheAir.collectAsLazyPagingItems()



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

//        if (popularMovies?.isNotEmpty() == true
//            && topRatedMovies?.isNotEmpty() == true
//            && nowPlayingMovies?.isNotEmpty() == true
//            && trendingMovies?.isNotEmpty() == true
//            && upcomingMovies?.isNotEmpty() == true
//        ) {
//        isLoading = false
//    }

    if(moviesPopular.loadState.refresh is LoadState.NotLoading
        && moviesTopRated.loadState.refresh is LoadState.NotLoading
        && moviesNowPlaying.loadState.refresh is LoadState.NotLoading
        && moviesUpcoming.loadState.refresh is LoadState.NotLoading
        && moviesTrending.loadState.refresh is LoadState.NotLoading
        && tvShowsPopular.loadState.refresh is LoadState.NotLoading
        && tvShowsTopRated.loadState.refresh is LoadState.NotLoading
        && tvShowsAiringTodayTVShows.loadState.refresh is LoadState.NotLoading
        && tvShowsOnTheAirTVShows.loadState.refresh is LoadState.NotLoading) {
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
//                            async { moviesViewModel.fetchPopularMovies(3) }.await()
//                            async { moviesViewModel.fetchTopRatedMovies(3) }.await()
//                            async { moviesViewModel.fetchNowPlayingMovies(3) }.await()
//                            async { moviesViewModel.fetchUpcomingMovies() }.await()
//                            async { moviesViewModel.fetchTrendingMovies() }.await()
                            moviesTrending.refresh()
                            moviesPopular.refresh()
                            moviesTopRated.refresh()
                            moviesNowPlaying.refresh()
                            moviesUpcoming.refresh()
                            tvShowsPopular.refresh()
                            tvShowsTopRated.refresh()
                            tvShowsAiringTodayTVShows.refresh()
                            tvShowsOnTheAirTVShows.refresh()
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


                        if (moviesUpcoming.loadState.refresh is LoadState.NotLoading) {
                            // Upcoming Movies
                            item {
                                MovieList(
                                    movies = moviesUpcoming,
                                    categoryTitle = "Upcoming Movies",
                                    color = Color.Red,
                                    onItemClick = navigateToMovieDetails
                                )
                                }
                        }

                        if (tvShowsPopular.loadState.refresh is LoadState.NotLoading) {
                            item {
                                TVShowList(
                                    tvShows = tvShowsPopular,
                                    categoryTitle = "Popular TV Shows",
                                    color = Color.Red,
                                    onItemClick = navigateToTVShowDetails
                                )
                            }
                        }


                        if (moviesTrending.loadState.refresh is LoadState.NotLoading) {
                            // Trending Movies
                            item {

                                MovieList(
                                    movies = moviesTrending,
                                    categoryTitle = "Trending Movies",
                                    color = Color.Green,
                                    onItemClick = navigateToMovieDetails
                                )

                            }
                        }


                        if (tvShowsTopRated.loadState.refresh is LoadState.NotLoading) {
                            item {
                                TVShowList(
                                    tvShows = tvShowsTopRated,
                                    categoryTitle = "Top Rated TV Shows",
                                    color = Color.Red,
                                    onItemClick = navigateToTVShowDetails)
                            }
                        }


                        if (moviesPopular.loadState.refresh is LoadState.NotLoading) {
                            // Popular Movies
                            item {
                                MovieList(
                                    movies = moviesPopular,
                                    categoryTitle = "Popular Movies",
                                    color = Color.Blue,
                                    onItemClick = navigateToMovieDetails
                                )
                            }
                        }

                        if (tvShowsOnTheAirTVShows.loadState.refresh is LoadState.NotLoading) {
                            item {
                                TVShowList(
                                    tvShows = tvShowsOnTheAirTVShows,
                                    categoryTitle = "On The Air TV Shows",
                                    color = Color.Red,
                                    onItemClick = navigateToTVShowDetails
                                )
                            }
                        }

                        if (moviesNowPlaying.loadState.refresh is LoadState.NotLoading) {
                            // Now Playing Movies
                            item {

                                MovieList(
                                    movies = moviesNowPlaying,
                                    categoryTitle = "Now Playing Movies",
                                    color = Color.Yellow,
                                    onItemClick = navigateToMovieDetails
                                )

                            }
                        }

                        if (moviesTopRated.loadState.refresh is LoadState.NotLoading) {
                            // Top Rated Movies
                            item {
                                MovieList(
                                    movies = moviesTopRated,
                                    categoryTitle = "Top Rated Movies",
                                    color = Color.Magenta,
                                    onItemClick = navigateToMovieDetails
                                )

                            }
                        }

                        if (tvShowsAiringTodayTVShows.loadState.refresh is LoadState.NotLoading) {
                            item {
                                TVShowList(
                                    tvShows = tvShowsAiringTodayTVShows,
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






