package com.drew.themoviedatabase.screens.Profile

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.drew.themoviedatabase.ComposeUtils.PullToRefresh
import com.drew.themoviedatabase.Network.NetworkClient
import com.drew.themoviedatabase.POJO.MovieDetailsReleaseData
import com.drew.themoviedatabase.POJO.TVShowDetails
import com.drew.themoviedatabase.POJO.UserDetails
import com.drew.themoviedatabase.R
import com.drew.themoviedatabase.composeUI.LoadingSpinner
import com.drew.themoviedatabase.composeUI.MovieList
import com.drew.themoviedatabase.composeUI.TVShowList
import com.drew.themoviedatabase.ui.theme.DarkOrange
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object ProfileNavScreen

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    moviesTVsViewModel: MyMoviesTVsViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel,
    userViewModel: UserViewModel = hiltViewModel(),
    navigateToMovieDetailsScreen: (Int) -> Unit,
    navigateToTVShowDetailsScreen: (Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val user by userViewModel.getUser.collectAsState()
    val token by loginViewModel.requestToken.collectAsState()
    val approval by loginViewModel.approved.collectAsState()
    var sessionID by rememberSaveable { mutableStateOf<String?>(null) }
    var accountID by rememberSaveable { mutableStateOf<Int>(21411766) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    val isRefreshing by rememberSaveable { mutableStateOf(false) }
    var loadingMessage by rememberSaveable { mutableStateOf("Requesting authentication....") }

    var showDialog by rememberSaveable { mutableStateOf(false) }



    val favoriteMovies =
        moviesTVsViewModel.getMyFavoriteMovies(accountId = accountID, sessionId = sessionID)
            .collectAsLazyPagingItems()
    val favoriteTVShows =  moviesTVsViewModel.getMyFavoriteTVShows(accountId = accountID , sessionId = sessionID).collectAsLazyPagingItems()
    val ratedMovies =   moviesTVsViewModel.getMyRatedMovies(accountId = accountID , sessionId = sessionID).collectAsLazyPagingItems()
    val ratedTVShows =  moviesTVsViewModel.getMyRatedTVShows(accountId = accountID , sessionId = sessionID).collectAsLazyPagingItems()
    val watchlistMovies =  moviesTVsViewModel.getMyWatchlistMovies(accountId = accountID , sessionId = sessionID).collectAsLazyPagingItems()
    val watchlistTVShows =  moviesTVsViewModel.getMyWatchlistTVShows(accountId = accountID , sessionId = sessionID).collectAsLazyPagingItems()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        //Log.i("LAUNCHER_RESULT", it.data?.data.toString())
    }

   Log.d("PROFILE_SCREEN", "User: $user")
    if (user != null) {
        loadingMessage = "Loading..."
        isLoading = true
        accountID = user?.accountId ?: 21411766
        sessionID = user?.sessionId
        isLoading = false
    }

    LaunchedEffect(key1 = sessionID) {
        try {
            val myProfile = async { loginViewModel.getAccountID(sessionID ?: "") }.await()
                if (myProfile == null && user != null) {

                    Log.d("PROFILE_SCREEN", "My Profile: $myProfile")
                    if (myProfile != null) {

                    }
                    user?.let { userViewModel.delete(it) }
                    sessionID = null
                    accountID = 21411766
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


//Log.d("Approval", approval.toString())
    if (approval) {
        LaunchedEffect(key1 = approval) {
            if (user == null) {
                isLoading = true
                delay(2000)
                sessionID = loginViewModel.requestSessionID(token)
                //Log.d("PROFILE_SCREEN", "Session ID: $sessionID")
                loadingMessage = "Getting account details..."
                delay(2000)
                val myProfile = loginViewModel.getAccountID(sessionID ?: "")
                accountID = myProfile?.id ?: 21411766
                loginViewModel.setUserProfile(myProfile)
                //Log.d("PROFILE_SCREEN", "Account ID: $accountID")
                if (approval == true) {
                    userViewModel.insert(
                        UserDetails(
                            accountId = accountID,
                            sessionId = sessionID,
                            username = myProfile?.username,
                            name = myProfile?.name,
                            avatar = myProfile?.avatar?.tmdb?.avatar_path
                        )
                    )
                }
                refreshData(
                    favoriteMovies = favoriteMovies,
                    favoriteTVShows = favoriteTVShows,
                    ratedMovies = ratedMovies,
                    ratedTVShows = ratedTVShows,
                    watchlistMovies = watchlistMovies,
                    watchlistTVShows = watchlistTVShows,
                )
                isLoading = false
            }
        }
    }


    // UI
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(top = 20.dp)
    ) { innerPadding ->
        Box (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            PullToRefresh(
                onRefresh = {
                    coroutineScope.launch {
                        isLoading = true
                        delay(2000)
                        refreshData(
                            favoriteMovies = favoriteMovies,
                            favoriteTVShows = favoriteTVShows,
                            ratedMovies = ratedMovies,
                            ratedTVShows = ratedTVShows,
                            watchlistMovies = watchlistMovies,
                            watchlistTVShows = watchlistTVShows,
                        )
                        isLoading = false
                    }
                },
                isRefreshing = isRefreshing
            ) {
                if (isLoading) {
                    // Display loading spinner
                    Column(
                        Modifier
                            .align(Alignment.Center)
                            .alpha(0.5f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                       LoadingSpinner()
                        Text(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            text = loadingMessage,
                            textAlign = TextAlign.Center
                        )
                    }

                } else {
                    MoviesTVLists(
                        favoriteMovies = favoriteMovies,
                        favoriteTVShows = favoriteTVShows,
                        ratedMovies = ratedMovies,
                        ratedTVShows = ratedTVShows,
                        watchlistMovies = watchlistMovies,
                        watchlistTVShows = watchlistTVShows,
                        onClickSignUp = {
                            isLoading = true
                            loginViewModel.authenticateUser(launcher)
                        },
                        user = user,
                        showDialog = showDialog,
                        onShowDialog = {
                            showDialog = true
                        },
                        onDismiss = {
                            showDialog = false
                        },
                        onConfirm = {
                            coroutineScope.launch {
                                showDialog = false
                                isLoading = true

                                val logout = loginViewModel.deleteSession(sessionId = sessionID ?: "")
                                if (logout?.success == true) {
                                    user?.let { userViewModel.delete(it) }
                                    sessionID = null
                                    accountID = 21411766
                                    loadingMessage = "Requesting authentication..."
                                    loginViewModel.setApproved(false)
                                    refreshData(
                                        favoriteMovies = favoriteMovies,
                                        favoriteTVShows = favoriteTVShows,
                                        ratedMovies = ratedMovies,
                                        ratedTVShows = ratedTVShows,
                                        watchlistMovies = watchlistMovies,
                                        watchlistTVShows = watchlistTVShows,
                                    )
                                }
                                delay(2000)
                                isLoading = false
                            }

                        },
                        navigateToMovieDetailsScreen = navigateToMovieDetailsScreen,
                        navigateToTVShowDetailsScreen = navigateToTVShowDetailsScreen
                    )
                }

            }
        }
    }
}

@Composable
fun MoviesTVLists(
    modifier: Modifier = Modifier,
    favoriteMovies: LazyPagingItems<MovieDetailsReleaseData>,
    favoriteTVShows: LazyPagingItems<TVShowDetails>,
    ratedMovies: LazyPagingItems<MovieDetailsReleaseData>,
    ratedTVShows: LazyPagingItems<TVShowDetails>,
    watchlistMovies: LazyPagingItems<MovieDetailsReleaseData>,
    watchlistTVShows: LazyPagingItems<TVShowDetails>,
    onClickSignUp: () -> Unit,
    user: UserDetails?,
    onShowDialog: () -> Unit,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    navigateToMovieDetailsScreen: (Int) -> Unit,
    navigateToTVShowDetailsScreen: (Int) -> Unit
) {
    Box(modifier = modifier
        .fillMaxSize()) {

        if (showDialog) {
            SignOutConfirmationDialog(
                showDialog = showDialog,
                onDismiss = onDismiss,
                onConfirm = onConfirm
            )
        }

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                ProfileStatisticsListCard(
                    modifier = modifier,
                    favoriteMovies = favoriteMovies,
                    favoriteTVShows = favoriteTVShows,
                    ratedMovies = ratedMovies,
                    ratedTVShows = ratedTVShows,
                    watchlistMovies = watchlistMovies,
                    watchlistTVShows = watchlistTVShows,
                    onClickSignUp = onClickSignUp,
                    user = user,
                    onShowDialog = onShowDialog
                )
            }

            if (user == null) {
                item {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            text = "Sign in to see your movies and TV shows.",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                if (
                    favoriteMovies.loadState.refresh is LoadState.Loading ||
                    favoriteTVShows.loadState.refresh is LoadState.Loading ||
                    ratedMovies.loadState.refresh is LoadState.Loading ||
                    ratedTVShows.loadState.refresh is LoadState.Loading ||
                    watchlistMovies.loadState.refresh is LoadState.Loading ||
                    watchlistTVShows.loadState.refresh is LoadState.Loading
                ) {
                    item {
                        Box(modifier = Modifier
                            .fillMaxSize(),
                            contentAlignment = Alignment.Center) {
                            LoadingSpinner()
                        }

                    }

                } else if (
                    favoriteMovies.itemCount == 0 &&
                    favoriteTVShows.itemCount == 0 &&
                    ratedMovies.itemCount == 0 &&
                    ratedTVShows.itemCount == 0 &&
                    watchlistMovies.itemCount == 0 &&
                    watchlistTVShows.itemCount == 0
                ) {
                    item {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                text = "No movies or TV shows found.",
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                } else {

                    if (favoriteMovies.itemCount > 0 && favoriteMovies.loadState.refresh is LoadState.NotLoading) {
                        // Trending Movies
                        item {

                            MovieList(
                                movies = favoriteMovies,
                                categoryTitle = "Favorite Movies",
                                color = Color.Green,
                                onItemClick = navigateToMovieDetailsScreen
                            )

                        }
                    }

                    if (favoriteTVShows.itemCount > 0 && favoriteTVShows.loadState.refresh is LoadState.NotLoading) {
                        item {
                            TVShowList(
                                tvShows = favoriteTVShows,
                                categoryTitle = "Favorite TV Shows",
                                color = Color.Red,
                                onItemClick = navigateToTVShowDetailsScreen
                            )
                        }
                    }


                    if (watchlistMovies.itemCount > 0 && watchlistMovies.loadState.refresh is LoadState.NotLoading) {
                        // Upcoming Movies
                        item {
                            MovieList(
                                movies = watchlistMovies,
                                categoryTitle = "Watchlist Movies",
                                color = Color.Red,
                                onItemClick = navigateToMovieDetailsScreen
                            )
                        }
                    }


                    if (watchlistTVShows.itemCount > 0 && watchlistTVShows.loadState.refresh is LoadState.NotLoading) {
                        item {
                            TVShowList(
                                tvShows = watchlistTVShows,
                                categoryTitle = "Watchlist TV Shows",
                                color = Color.Red,
                                onItemClick = navigateToTVShowDetailsScreen
                            )
                        }
                    }


                    if (ratedMovies.itemCount > 0 && ratedMovies.loadState.refresh is LoadState.NotLoading) {
                        // Popular Movies
                        item {
                            MovieList(
                                movies = ratedMovies,
                                categoryTitle = "Rated Movies",
                                color = Color.Blue,
                                onItemClick = navigateToMovieDetailsScreen
                            )
                        }
                    }

                    if (ratedTVShows.itemCount > 0 && ratedTVShows.loadState.refresh is LoadState.NotLoading) {
                        item {
                            TVShowList(
                                tvShows = ratedTVShows,
                                categoryTitle = "Rated TV Shows",
                                color = Color.Red,
                                onItemClick = navigateToTVShowDetailsScreen
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileStatisticsListCard(
    modifier: Modifier,
    user: UserDetails?,
    onClickSignUp: () -> Unit,
    favoriteMovies: LazyPagingItems<MovieDetailsReleaseData>,
    favoriteTVShows: LazyPagingItems<TVShowDetails>,
    ratedMovies: LazyPagingItems<MovieDetailsReleaseData>,
    ratedTVShows: LazyPagingItems<TVShowDetails>,
    watchlistMovies: LazyPagingItems<MovieDetailsReleaseData>,
    watchlistTVShows: LazyPagingItems<TVShowDetails>,
    onShowDialog: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ProfileHeader(
            profilePicture = if (user?.avatar_path != null)
                NetworkClient().getPosterUrl(user.avatar_path, imageSize = "w200") else null,
            name = if (user?.name?.isNotEmpty() == true) user.name else user?.username,
            onShowDialog = onShowDialog,
            user = user
        )
        if (user == null) {
            Button(
                colors = ButtonDefaults.buttonColors().copy(containerColor = DarkOrange),
                onClick = onClickSignUp
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Sign in / Sign up",
                    textAlign = TextAlign.Center
                )

            }
        }

        ProfileStatisticsList(
            modifier = modifier,
            favoriteMovies = favoriteMovies,
            favoriteTVShows = favoriteTVShows,
            ratedMovies = ratedMovies,
            ratedTVShows = ratedTVShows,
            watchlistMovies = watchlistMovies,
            watchlistTVShows = watchlistTVShows
        )
    }
}

@Composable
fun ProfileHeader(
    modifier: Modifier = Modifier,
    profilePicture: String?,
    name: String?,
    onShowDialog: () -> Unit,
    user: UserDetails?
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(30.dp),
                model = profilePicture,
                contentDescription = "Profile Picture",
                fallback = painterResource(R.drawable.outline_account_circle_24),
                colorFilter = if (profilePicture == null) ColorFilter.tint(DarkOrange) else null
            )

            Text(
                text = name ?: "Sign in",
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if(user != null) {
            IconButton(
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.CenterEnd),
                onClick = onShowDialog) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Sign out"
                )

            }
        }
    }

}

@Composable
fun ProfileStatisticsList(
   modifier: Modifier = Modifier,
   favoriteMovies: LazyPagingItems<MovieDetailsReleaseData>,
   favoriteTVShows: LazyPagingItems<TVShowDetails>,
   ratedMovies: LazyPagingItems<MovieDetailsReleaseData>,
   ratedTVShows: LazyPagingItems<TVShowDetails>,
   watchlistMovies: LazyPagingItems<MovieDetailsReleaseData>,
   watchlistTVShows: LazyPagingItems<TVShowDetails>
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ProfileStatisticsCard(
                category = "Watchlist",
                count = watchlistMovies.itemCount + watchlistTVShows.itemCount
            )
        }

        item {
            ProfileStatisticsCard(
                category = "Favorites",
                count = favoriteMovies.itemCount + favoriteTVShows.itemCount
            )
        }

        item {
            ProfileStatisticsCard(
                category = "Ratings",
                count = ratedMovies.itemCount + ratedTVShows.itemCount
            )
        }
    }
}

@Composable
fun ProfileStatisticsCard(
    modifier: Modifier = Modifier,
    category: String,
    count: Int
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = category,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = count.toString(),
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center
            )
        }
    }
}
fun refreshData(
    favoriteMovies: LazyPagingItems<MovieDetailsReleaseData>,
    favoriteTVShows: LazyPagingItems<TVShowDetails>,
    ratedMovies: LazyPagingItems<MovieDetailsReleaseData>,
    ratedTVShows: LazyPagingItems<TVShowDetails>,
    watchlistMovies: LazyPagingItems<MovieDetailsReleaseData>,
    watchlistTVShows: LazyPagingItems<TVShowDetails>
) {
    favoriteMovies.refresh()
    favoriteTVShows.refresh()
    ratedMovies.refresh()
    ratedTVShows.refresh()
    watchlistMovies.refresh()
    watchlistTVShows.refresh()
}

@Composable
fun SignOutConfirmationDialog(
    modifier: Modifier = Modifier,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = onDismiss,
            title = { Text("Sign Out") },
            text = { Text("Are you sure you want to sign out?") },
            confirmButton = {
                Button(onClick = onConfirm) {
                    Text("Sign Out")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}

