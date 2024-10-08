package com.drew.themoviedatabase.screens.Home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.drew.themoviedatabase.screens.commonComposeUi.PullToRefresh
import com.drew.themoviedatabase.screens.commonComposeUi.LoadingSpinner
import com.drew.themoviedatabase.screens.commonComposeUi.PopularPeopleList
import com.drew.themoviedatabase.screens.commonComposeUi.TVShowList
import com.drew.themoviedatabase.data.remote.MultiSearchResult
import com.drew.themoviedatabase.screens.Cast.CastViewModel
import com.drew.themoviedatabase.screens.Search.CastCardSearchItem
import com.drew.themoviedatabase.screens.Search.MovieItemSearch
import com.drew.themoviedatabase.screens.Search.TVShowItemsSearch
import com.drew.themoviedatabase.screens.commonComposeUi.MovieList
import com.drew.themoviedatabase.ui.theme.DarkOrange
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object HomeScreen

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    moviesViewModel: HomeMoviesViewModel = hiltViewModel(),
    tvShowsViewModel: HomeTVViewModel = hiltViewModel(),
    castViewModel: CastViewModel = hiltViewModel(),
    navigateToMovieDetails: (Int) -> Unit,
    navigateToTVShowDetails: (Int) -> Unit,
    navigateToCastDetailsScreen: (Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    // Loading state
    var isLoading by rememberSaveable { mutableStateOf(true) }
    val isRefreshing by moviesViewModel.isRefreshing.observeAsState(false)


    val moviesPopular  = moviesViewModel.moviesPopular.collectAsLazyPagingItems()
    val moviesTopRated = moviesViewModel.moviesTopRated.collectAsLazyPagingItems()
    val moviesNowPlaying = moviesViewModel.moviesNowPlaying.collectAsLazyPagingItems()
    val moviesUpcoming = moviesViewModel.moviesUpcoming.collectAsLazyPagingItems()
    val moviesTrending = moviesViewModel.moviesTrending.collectAsLazyPagingItems()
    val trendingMedia = moviesViewModel.trendingMedia.collectAsLazyPagingItems()

    val tvShowsPopular = tvShowsViewModel.tvShowsPopular.collectAsLazyPagingItems()
    val tvShowsTopRated = tvShowsViewModel.tvShowsTopRated.collectAsLazyPagingItems()
    val tvShowsAiringTodayTVShows = tvShowsViewModel.tvShowsAiringToday.collectAsLazyPagingItems()
    val tvShowsOnTheAirTVShows = tvShowsViewModel.tvShowsOnTheAir.collectAsLazyPagingItems()

    val popularPeople = castViewModel.popularPeople.collectAsLazyPagingItems()

    if(
        moviesPopular.loadState.refresh is LoadState.NotLoading
        && moviesTopRated.loadState.refresh is LoadState.NotLoading
        && moviesNowPlaying.loadState.refresh is LoadState.NotLoading
        && moviesUpcoming.loadState.refresh is LoadState.NotLoading
        && moviesTrending.loadState.refresh is LoadState.NotLoading
        && tvShowsPopular.loadState.refresh is LoadState.NotLoading
        && tvShowsTopRated.loadState.refresh is LoadState.NotLoading
        && tvShowsAiringTodayTVShows.loadState.refresh is LoadState.NotLoading
        && tvShowsOnTheAirTVShows.loadState.refresh is LoadState.NotLoading
        && popularPeople.loadState.refresh is LoadState.NotLoading &&
        trendingMedia.loadState.refresh is LoadState.NotLoading
        ) {
        isLoading = false
    }

    // UI
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(top = 20.dp)
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
                            moviesTrending.refresh()
                            moviesPopular.refresh()
                            moviesTopRated.refresh()
                            moviesNowPlaying.refresh()
                            moviesUpcoming.refresh()
                            popularPeople.refresh()
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
                    LoadingSpinner(modifier = Modifier.align(Alignment.Center))
                } else {
                    // Display movie lists
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        if (moviesNowPlaying.itemCount > 0 && moviesNowPlaying.loadState.refresh is LoadState.NotLoading) {
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

                        if (trendingMedia.itemCount > 0 && trendingMedia.loadState.refresh is LoadState.NotLoading) {
                            item {
                                TrendingMediaCard(
                                    items = trendingMedia,
                                    navigateToMovieDetailsScreen = navigateToMovieDetails,
                                    navigateToCastDetailsScreen = navigateToCastDetailsScreen,
                                    navigateToTVShowDetailsScreen = navigateToTVShowDetails,
                                )
                            }
                        }

                        if (tvShowsPopular.itemCount > 0 && tvShowsPopular.loadState.refresh is LoadState.NotLoading) {
                            item {
                                TVShowList(
                                    tvShows = tvShowsPopular,
                                    categoryTitle = "Popular TV Shows",
                                    color = Color.Red,
                                    onItemClick = navigateToTVShowDetails
                                )
                            }
                        }

                        if (popularPeople.itemCount > 0 && popularPeople.loadState.refresh is LoadState.NotLoading) {
                            item {
                                PopularPeopleList(
                                    peopleList = popularPeople,
                                    categoryTitle = "Popular People",
                                    color = DarkOrange,
                                    onItemClick = navigateToCastDetailsScreen
                                )
                            }
                        }

                        if (moviesUpcoming.itemCount > 0 && moviesUpcoming.loadState.refresh is LoadState.NotLoading) {
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

                        if (moviesTrending.itemCount > 0 && moviesTrending.loadState.refresh is LoadState.NotLoading) {
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


                        if (tvShowsTopRated.itemCount > 0 && tvShowsTopRated.loadState.refresh is LoadState.NotLoading) {
                            item {
                                TVShowList(
                                    tvShows = tvShowsTopRated,
                                    categoryTitle = "Top Rated TV Shows",
                                    color = Color.Red,
                                    onItemClick = navigateToTVShowDetails)
                            }
                        }


                        if (moviesPopular.itemCount > 0 && moviesPopular.loadState.refresh is LoadState.NotLoading) {
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

                        if (tvShowsOnTheAirTVShows.itemCount > 0 && tvShowsOnTheAirTVShows.loadState.refresh is LoadState.NotLoading) {
                            item {
                                TVShowList(
                                    tvShows = tvShowsOnTheAirTVShows,
                                    categoryTitle = "On The Air TV Shows",
                                    color = Color.Red,
                                    onItemClick = navigateToTVShowDetails
                                )
                            }
                        }



                        if (moviesTopRated.itemCount > 0 && moviesTopRated.loadState.refresh is LoadState.NotLoading) {
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

                        if ( tvShowsAiringTodayTVShows.itemCount > 0 && tvShowsAiringTodayTVShows.loadState.refresh is LoadState.NotLoading) {
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
fun TrendingMediaCard(
    modifier: Modifier = Modifier,
    items: LazyPagingItems<MultiSearchResult>,
    navigateToMovieDetailsScreen: (Int) -> Unit,
    navigateToCastDetailsScreen: (Int) -> Unit,
    navigateToTVShowDetailsScreen: (Int) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            VerticalDivider(
                modifier = Modifier
                    .width(6.dp),
                color = Color.Green
            )
            Text(
                text = "Trending",
                style = MaterialTheme.typography.titleLarge
            )
        }

        LazyRow (
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items.itemCount) { index ->
                when(val item = items[index]) {
                    is MultiSearchResult.Movie -> {
                        MovieItemSearch(
                            movie = item,
                            onItemClick = navigateToMovieDetailsScreen,
                            width = 150.dp,
                        )
                    }

                    is MultiSearchResult.Person -> {
                        CastCardSearchItem(
                            person = item,
                            navigateToCastDetailsScreen = navigateToCastDetailsScreen,
                            width = 150.dp,
                        )
                    }

                    is MultiSearchResult.TV -> {
                        TVShowItemsSearch(
                            tvShow = item,
                            onItemClick = navigateToTVShowDetailsScreen,
                            width = 150.dp,
                        )
                    }

                    null -> return@items
                }
            }
        }
    }
}







