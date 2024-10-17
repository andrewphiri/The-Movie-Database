package com.drew.themoviedatabase.screens.Home

import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.drew.themoviedatabase.data.model.MyFavoriteMovies
import com.drew.themoviedatabase.data.model.MyFavoriteTVShows
import com.drew.themoviedatabase.data.model.MyRatedMovies
import com.drew.themoviedatabase.data.model.MyRatedTVShows
import com.drew.themoviedatabase.data.model.MyWatchlistMovies
import com.drew.themoviedatabase.data.model.MyWatchlistTVShows
import com.drew.themoviedatabase.screens.commonComposeUi.PullToRefresh
import com.drew.themoviedatabase.screens.commonComposeUi.LoadingSpinner
import com.drew.themoviedatabase.screens.commonComposeUi.PopularPeopleList
import com.drew.themoviedatabase.screens.commonComposeUi.TVShowList
import com.drew.themoviedatabase.data.remote.MultiSearchResult
import com.drew.themoviedatabase.screens.Cast.CastViewModel
import com.drew.themoviedatabase.screens.Details.MoviesShowsViewModel
import com.drew.themoviedatabase.screens.Profile.MyMoviesTVsViewModel
import com.drew.themoviedatabase.screens.Profile.UserViewModel
import com.drew.themoviedatabase.screens.Search.CastCardSearchItem
import com.drew.themoviedatabase.screens.Search.MovieItemSearch
import com.drew.themoviedatabase.screens.Search.TVShowItemsSearch
import com.drew.themoviedatabase.screens.commonComposeUi.MovieList
import com.drew.themoviedatabase.ui.theme.DarkOrange
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object HomeScreen

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = hiltViewModel(),
    moviesShowsViewModel: MoviesShowsViewModel = hiltViewModel(),
    moviesViewModel: HomeMoviesViewModel = hiltViewModel(),
    moviesTVsViewModel: MyMoviesTVsViewModel = hiltViewModel(),
    tvShowsViewModel: HomeTVViewModel = hiltViewModel(),
    castViewModel: CastViewModel = hiltViewModel(),
    navigateToMovieDetails: (Int) -> Unit,
    navigateToTVShowDetails: (Int) -> Unit,
    navigateToCastDetailsScreen: (Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val user by userViewModel.getUser.collectAsState(initial = null)
    // Loading state
    var isLoading by rememberSaveable { mutableStateOf(true) }
    val isRefreshing by moviesViewModel.isRefreshing.observeAsState(false)
    var sessionID by rememberSaveable { mutableStateOf<String?>(null) }
    var accountID by rememberSaveable { mutableStateOf<Int>(21411766) }

    val moviesPopular = moviesViewModel.moviesPopular.collectAsLazyPagingItems()
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

    //My movies and TV Shows
    val favoriteMovies by moviesTVsViewModel.favMovies.observeAsState()
    val favoriteTVShows by moviesTVsViewModel.favTVShows.observeAsState()
    val ratedMovies by moviesTVsViewModel.ratedMovies.observeAsState()
    val ratedTVShows by moviesTVsViewModel.ratedTVShows.observeAsState()
    val watchlistMovies by moviesTVsViewModel.watchlistMovies.observeAsState()
    val watchlistTVShows by moviesTVsViewModel.watchlistTVShows.observeAsState()

    //My movies and TV shows in room
    val myFavMovies = moviesShowsViewModel.getFavoriteMovies.collectAsState(initial = emptyList())
    val myFavTVShows = moviesShowsViewModel.getFavoriteTVShows.collectAsState(initial = emptyList())
    val myRatedRoomMovies =
        moviesShowsViewModel.getRatedMovies.collectAsState(initial = emptyList())
    val myRatedRoomTVShows =
        moviesShowsViewModel.getRatedTVShows.collectAsState(initial = emptyList())
    val myWatchlistRoomMovies =
        moviesShowsViewModel.getWatchlistMovies.collectAsState(initial = emptyList())
    val myWatchlistRoomTVShows =
        moviesShowsViewModel.getWatchlistTVShows.collectAsState(initial = emptyList())

    LaunchedEffect(user) {
        if (user != null) {
            accountID = user?.accountId ?: 21411766
            sessionID = user?.sessionId
//            Log.d("PROFILE_SCREEN", "Account ID: $accountID")
//            Log.d("PROFILE_SCREEN", "Session ID: $sessionID")
            coroutineScope.launch {
                val deferreds = listOf(
                    async { moviesTVsViewModel.fetchMyFavoriteMovies(accountID, sessionID) },
                    async { moviesTVsViewModel.fetchMyFavoriteShows(accountID, sessionID) },
                    async { moviesTVsViewModel.fetchMyRatedMovies(accountID, sessionID) },
                    async { moviesTVsViewModel.fetchMyRatedTVShows(accountID, sessionID) },
                    async { moviesTVsViewModel.fetchMyWatchlistMovies(accountID, sessionID) },
                    async { moviesTVsViewModel.fetchMyWatchlistTVShows(accountID, sessionID) }
                )

                try {
                    deferreds.awaitAll()
                    //Log.d("favoriteMovies", "HomeScreen: $favoriteMovies")
                } catch (e: Exception) {
                    // Handle error, e.g., log, show error message
                    e.printStackTrace()
                }
            }
        }
    }

        if (user != null) {
            try {
            favoriteMovies?.let { movies ->
                val newMovies = movies.filterNot { movie ->
                    myFavMovies.value.any { it.movieId == movie?.id }
                }
                if (newMovies.isNotEmpty()) {
                    moviesShowsViewModel.insertFavoriteMovies(
                        newMovies.mapNotNull { movie ->
                            MyFavoriteMovies(
                                movieId = movie?.id,
                                movieTitle = movie?.title,
                                moviePoster = movie?.posterPath
                            )
                        }
                    )
                }
            }

            favoriteTVShows?.let { shows ->
                val newShows = shows.filterNot { show ->
                    myFavTVShows.value.any { it.seriesId == show?.id }
                }
                if (newShows.isNotEmpty()) {
                    moviesShowsViewModel.insertFavoriteTVShows(
                        newShows.mapNotNull { show ->
                            MyFavoriteTVShows(
                                seriesId = show?.id,
                                seriesTitle = show?.name,
                                seriesPoster = show?.posterPath
                            )
                        }
                    )
                }
            }

            watchlistMovies?.let { movies ->
                val newMovies = movies.filterNot { movie ->
                    myWatchlistRoomMovies.value.any { it.movieId == movie?.id }
                }
                if (newMovies.isNotEmpty()) {
                    moviesShowsViewModel.insertWatchlistMovies(
                        newMovies.mapNotNull { movie ->
                            MyWatchlistMovies(
                                movieId = movie?.id,
                                movieTitle = movie?.title,
                                moviePoster = movie?.posterPath
                            )
                        }
                    )
                }
            }

            watchlistTVShows?.let { shows ->
                val newShows = shows.filterNot { show ->
                    myWatchlistRoomTVShows.value.any { it.seriesId == show?.id }
                }
                if (newShows.isNotEmpty()) {
                    moviesShowsViewModel.insertWatchlistTVShows(
                        newShows.mapNotNull { show ->
                            MyWatchlistTVShows(
                                seriesId = show?.id,
                                seriesTitle = show?.name,
                                seriesPoster = show?.posterPath
                            )
                        }
                    )
                }
            }

            ratedMovies?.let { movies ->
                val newMovies = movies.filterNot { movie ->
                    myRatedRoomMovies.value.any { it.movieId == movie?.id }
                }
                if (newMovies.isNotEmpty()) {
                    moviesShowsViewModel.insertRatedMovies(
                        newMovies.mapNotNull { movie ->
                            MyRatedMovies(
                                movieId = movie?.id,
                                movieTitle = movie?.title,
                                moviePoster = movie?.posterPath
                            )
                        }
                    )
                }
            }

            ratedTVShows?.let { shows ->
                val newShows = shows.filterNot { show ->
                    myRatedRoomTVShows.value.any { it.seriesId == show?.id }
                }
                if (newShows.isNotEmpty()) {
                    moviesShowsViewModel.insertRatedTVShows(
                        newShows.mapNotNull { show ->
                            MyRatedTVShows(
                                seriesId = show?.id,
                                seriesTitle = show?.name,
                                seriesPoster = show?.posterPath
                            )
                        }
                    )
                }
            }

        } catch (e: Exception) {
        e.printStackTrace()
    }
}


    //Log.d("favoriteMovies", "HomeScreenOut: $favoriteMovies")

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
                            item(key = 1) {

                                MovieList(
                                    movies = moviesNowPlaying,
                                    categoryTitle = "Now Playing Movies",
                                    color = Color.Yellow,
                                    onItemClick = navigateToMovieDetails
                                )

                            }
                        }

                        if (trendingMedia.itemCount > 0 && trendingMedia.loadState.refresh is LoadState.NotLoading) {
                            item(key = 2) {
                                TrendingMediaCard(
                                    items = trendingMedia,
                                    navigateToMovieDetailsScreen = navigateToMovieDetails,
                                    navigateToCastDetailsScreen = navigateToCastDetailsScreen,
                                    navigateToTVShowDetailsScreen = navigateToTVShowDetails,
                                )
                            }
                        }

                        if (tvShowsPopular.itemCount > 0 && tvShowsPopular.loadState.refresh is LoadState.NotLoading) {
                            item(key = 3) {
                                TVShowList(
                                    tvShows = tvShowsPopular,
                                    categoryTitle = "Popular TV Shows",
                                    color = Color.Red,
                                    onItemClick = navigateToTVShowDetails
                                )
                            }
                        }

                        if (popularPeople.itemCount > 0 && popularPeople.loadState.refresh is LoadState.NotLoading) {
                            item(key = 4) {
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
                            item(key = 5) {
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
                            item(key = 6) {

                                MovieList(
                                    movies = moviesTrending,
                                    categoryTitle = "Trending Movies",
                                    color = Color.Green,
                                    onItemClick = navigateToMovieDetails
                                )

                            }
                        }


                        if (tvShowsTopRated.itemCount > 0 && tvShowsTopRated.loadState.refresh is LoadState.NotLoading) {
                            item(key = 7) {
                                TVShowList(
                                    tvShows = tvShowsTopRated,
                                    categoryTitle = "Top Rated TV Shows",
                                    color = Color.Red,
                                    onItemClick = navigateToTVShowDetails)
                            }
                        }


                        if (moviesPopular.itemCount > 0 && moviesPopular.loadState.refresh is LoadState.NotLoading) {
                            // Popular Movies
                            item(key = 8) {
                                MovieList(
                                    movies = moviesPopular,
                                    categoryTitle = "Popular Movies",
                                    color = Color.Blue,
                                    onItemClick = navigateToMovieDetails
                                )
                            }
                        }

                        if (tvShowsOnTheAirTVShows.itemCount > 0 && tvShowsOnTheAirTVShows.loadState.refresh is LoadState.NotLoading) {
                            item(key = 9) {
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
                            item(key = 10) {
                                MovieList(
                                    movies = moviesTopRated,
                                    categoryTitle = "Top Rated Movies",
                                    color = Color.Magenta,
                                    onItemClick = navigateToMovieDetails
                                )

                            }
                        }

                        if ( tvShowsAiringTodayTVShows.itemCount > 0 && tvShowsAiringTodayTVShows.loadState.refresh is LoadState.NotLoading) {
                            item(key = 11) {
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







