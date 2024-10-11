package com.drew.themoviedatabase.screens.Details


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.transition.Transition
import com.drew.themoviedatabase.MovieTopAppBar
import com.drew.themoviedatabase.Utilities.currencyFormatter
import com.drew.themoviedatabase.Utilities.findPreferredVideo
import com.drew.themoviedatabase.Utilities.getWatchRegion
import com.drew.themoviedatabase.data.model.Trailers
import com.drew.themoviedatabase.data.remote.MovieDetailsResponse
import com.drew.themoviedatabase.screens.commonComposeUi.CastList
import com.drew.themoviedatabase.screens.commonComposeUi.GenreList
import com.drew.themoviedatabase.screens.commonComposeUi.LoadingSpinner
import com.drew.themoviedatabase.screens.commonComposeUi.MovieList
import com.drew.themoviedatabase.screens.commonComposeUi.MovieTVCertifications
import com.drew.themoviedatabase.screens.commonComposeUi.OverviewText
import com.drew.themoviedatabase.screens.commonComposeUi.PhotosList
import com.drew.themoviedatabase.screens.commonComposeUi.ProvidersList
import com.drew.themoviedatabase.screens.commonComposeUi.RatingsAndVotes
import com.drew.themoviedatabase.screens.commonComposeUi.ReviewList
import com.drew.themoviedatabase.screens.commonComposeUi.YouTubePlayer
import com.drew.themoviedatabase.formatDuration
import com.drew.themoviedatabase.screens.Profile.MyMoviesTVsViewModel
import com.drew.themoviedatabase.screens.Profile.UserViewModel
import com.drew.themoviedatabase.screens.commonComposeUi.ShowPhotosDialog
import com.drew.themoviedatabase.ui.theme.DarkOrange
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class DetailsMovieScreen(
    val movieId: Int,
)

@Composable
fun MovieDetailsScreen(
    modifier: Modifier = Modifier,
    movieId: Int,
    canNavigateBack: Boolean = true,
    navigateUp: () -> Unit = {},
    navigateToDetails: (Int) -> Unit = {},
    moviesViewModel: MoviesViewModel = hiltViewModel(),
    navigateToCastDetails: (Int) -> Unit,
    navigateToReviews: (Int) -> Unit,
    navigateToTrailers: (Int) -> Unit,
    moviesTVsViewModel: MyMoviesTVsViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()

    val user by userViewModel.getUser.collectAsState(initial = null)

    var isLoading by remember { mutableStateOf(true) }
    var isTrailersEmpty by remember { mutableStateOf(true) }
    var isFavorite by rememberSaveable { mutableStateOf(false) }
    var isAddedToWatchlist by rememberSaveable { mutableStateOf(false) }

    val movieDetails by moviesViewModel.movieDetailsWithCastAndVideos.observeAsState()
    val similarMovies = moviesViewModel.getSimilarMovies(movieId).collectAsLazyPagingItems()
    val reviews = moviesViewModel.getReviews(movieId).collectAsLazyPagingItems()
    val photos = moviesViewModel.getPhotos(movieId).collectAsLazyPagingItems()
    val certifications by moviesViewModel.certifications.observeAsState()
    var isPhotosDialogShowing by rememberSaveable { mutableStateOf(false) }
    var page by rememberSaveable { mutableStateOf(1) }


    LaunchedEffect(Unit) {
        coroutineScope.launch {
            async { moviesViewModel.fetchMovieDetailsWithCastAndVideos(movieId) }.await()
            async { moviesViewModel.fetchCertifications()}.await()
            async { moviesViewModel.getReviews(movieId) }.await()
        }
    }

//    moviesViewModel.movieDetailsWithCastAndVideos.observe(lifecycleOwner) {
//        movieDetails = it
//    }

//    moviesViewModel.certifications.observe(lifecycleOwner) {
//        certifications = it
//    }
    //Log.d("MovieCertifications", "MovieCertifications: $certifications")

    //moviesViewModel.fetchCertifications()

//    moviesViewModel.reviews.observe(lifecycleOwner) {
//        reviews = it
//    }


    if (movieDetails != null) {
        isLoading = false
    }

    if(movieDetails?.videos?.getResults()?.isNotEmpty() == true){
        isTrailersEmpty  = false
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(top = 16.dp),
        topBar = {
            MovieTopAppBar(
                canNavigateBack = canNavigateBack,
                title = movieDetails?.title ?: "",
                navigateUp = navigateUp
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {


            AnimatedVisibility(
                visible = isPhotosDialogShowing,
            ) {
                ShowPhotosDialog(
                    photos = photos.itemSnapshotList.items.map { it.filePath },
                    onDismiss = { isPhotosDialogShowing = false },
                    initialPage = page
                )
            }

            if (isLoading) {
                LoadingSpinner(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    item {
                        MovieDetailsCard(
                            movieDetails = movieDetails,
                            trailers = movieDetails?.videos?.getResults(),
                            isTrailersEmpty = isTrailersEmpty,
                            navigateToTrailers = { navigateToTrailers(movieId) }
                        )
                    }
                    if (movieDetails?.genres?.isNotEmpty() == true) {
                        item {
                            GenreList(
                                genres = movieDetails?.genres
                            )
                        }
                    }

                    if (movieDetails?.watchProviders?.results?.isNotEmpty() == true) {
                        item {
                            MovieWatchProviderList(
                                movie = movieDetails
                            )
                        }
                    }

                    item {
                        RatingsAndVotes(
                            voteAverage = movieDetails?.voteAverage,
                            voteCount = movieDetails?.voteCount,
                            isFavorite = isFavorite,
                            isAddedToWatchlist = isAddedToWatchlist,
                            onAddToFavorites = {
                                //isFavorite = !isFavorite
                                if(user != null) {
                                    coroutineScope.launch {
                                       val addedToListResponse = moviesTVsViewModel.addToFavoriteOrWatchlist(
                                            mediaType = "movie",
                                            mediaID = movieId,
                                            listType = "favorite",
                                            accountId = user?.id ?: 0,
                                            sessionId = user?.sessionId,
                                            addToList = !isFavorite
                                        )
                                        //Log.d("addToFavorites", "MovieDetailsScreen: $addedToListResponse")
                                        if (addedToListResponse?.success == true) {
                                            isFavorite = !isFavorite
                                        }
                                    }
                                }
                            },
                            onAddToWatchlist = {
//                                isAddedToWatchlist = !isAddedToWatchlist

                                if(user != null) {
                                    coroutineScope.launch {
                                       val addedToListResponse = moviesTVsViewModel.addToFavoriteOrWatchlist(
                                            mediaType = "movie",
                                            mediaID = movieId,
                                            listType = "watchlist",
                                            accountId = user?.id ?: 0,
                                            sessionId = user?.sessionId,
                                            addToList = !isAddedToWatchlist
                                        )
                                        //Log.d("addToWatchlist", "MovieDetailsScreen: $addedToListResponse")
                                        if (addedToListResponse?.success == true) {
                                            isAddedToWatchlist = !isAddedToWatchlist
                                        }
                                    }
                                }
                            }
                        )
                    }

                    item {
                        CastList(
                            castMembers = movieDetails?.credits?.getCast(),
                            crew = movieDetails?.credits?.getCrew(),
                            navigateToCastDetailsScreen = navigateToCastDetails
                        )
                    }

                    item {
                        OtherMovieDetailsCard(
                            movieDetails = movieDetails
                        )
                    }

                    if (similarMovies.itemCount > 0) {
                        item {
                            MovieList(
                                movies = similarMovies,
                                color = Color.Cyan,
                                categoryTitle = "More like this",
                                onItemClick = navigateToDetails
                            )
                        }
                    }

                    if (reviews.itemCount > 0) {
                        item {
                            ReviewList(
                                reviews = reviews,
                                categoryTitle = "Reviews",
                                onItemClick = { navigateToReviews(movieId) }
                            )
                        }
                    }

                    if (photos.itemCount > 0) {
                        item {
                            PhotosList(
                                photos = photos,
                                categoryTitle = "Photos",
                                onPhotoClick = {
                                    isPhotosDialogShowing = true
                                    page = it
                                }
                            )
                        }
                    }

                    if (certifications != null) {
                        item {
                            MovieCertifications(
                                certifications = certifications,
                                movie = movieDetails
                            )
                        }
                    }
               }
            }
        }
    }
}

@Composable
fun MovieDetailsCard(
    modifier: Modifier = Modifier,
    movieDetails: MovieDetailsResponse?,
    trailers: List<Trailers?>?,
    isTrailersEmpty: Boolean = false,
    navigateToTrailers: () -> Unit
) {
    val ageRate = movieDetails?.certifications?.results
        ?.find { it.iso31661 == getWatchRegion() }?.releaseDates?.
        find { it.certification != "" }?.certification ?: ""

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 8.dp, start = 8.dp, top = 8.dp),
            text = movieDetails?.title ?: "",
            style = MaterialTheme.typography.headlineMedium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 8.dp, start = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = movieDetails?.releaseDate ?: "",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = ageRate,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = formatDuration(movieDetails?.runtime),
                style = MaterialTheme.typography.bodySmall
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
        ) {
            if (isTrailersEmpty) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth(),
                        text = "No trailers found",
                        textAlign = TextAlign.Center
                        )

            } else {
                YouTubePlayer(
                    modifier = Modifier.align(Alignment.TopCenter),
                    videoId = findPreferredVideo(trailers) ?: "",
                )
            }
        }
        if (!isTrailersEmpty) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .clickable { navigateToTrailers() },
                    text = "See more videos",
                    color = Color.Cyan,
                )
            }
        }



        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                modifier = Modifier
                    .height(175.dp)
                    .width(100.dp),
                model = com.drew.themoviedatabase.data.remote.NetworkClient().getPosterUrl(movieDetails?.posterPath),
                contentDescription = "${movieDetails?.title} poster",
                placeholder = null
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (movieDetails?.tagline != null) {
                    Text(
                        text = movieDetails.tagline,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                OverviewText(
                    overview = movieDetails?.overview ?: "",
                )
            }
        }

    }
}



@Composable
fun OtherMovieDetailsCard(
    modifier: Modifier = Modifier,
    movieDetails: MovieDetailsResponse?
) {
    ElevatedCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Status",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = movieDetails?.status ?: "N/A",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Column {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Original Language",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = movieDetails?.spoken_languages?.find { it.iso6391 == movieDetails.originalLanguage }?.name ?: "N/A",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Column {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Budget",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = currencyFormatter(movieDetails?.budget?.toDouble()) ?: "N/A",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Column {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Revenue",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = currencyFormatter(movieDetails?.revenue?.toDouble()) ?: "N/A",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun MovieWatchProviderList(
    modifier: Modifier = Modifier,
    movie: MovieDetailsResponse?,
) {
    val uriHandler = LocalUriHandler.current
    val watchRegion = getWatchRegion()
    val watchProvidersBuy = movie?.watchProviders?.results?.get(watchRegion)?.buy?.toList()
    val watchProvidersRent = movie?.watchProviders?.results?.get(watchRegion)?.rent?.toList()
    val watchProvidersFlatRate = movie?.watchProviders?.results?.get(watchRegion)?.flatrate?.toList()

    val allProviders: SnapshotStateList<com.drew.themoviedatabase.data.model.Provider>? = remember { mutableStateListOf() }

    if (watchProvidersRent != null) {
        allProviders?.addAll(watchProvidersRent)
    }

    if (watchProvidersBuy != null) {
        allProviders?.addAll(watchProvidersBuy)
    }

    if (watchProvidersFlatRate != null) {
        allProviders?.addAll(watchProvidersFlatRate)
    }

    if (allProviders != null) {
        val link = movie?.watchProviders?.results?.get(watchRegion)?.link ?: ""
        val attribution = buildAnnotatedString {
            append("Watch providers data provided by ")
            withStyle(style = androidx.compose.ui.text.SpanStyle(fontStyle = FontStyle.Italic, fontWeight = FontWeight.Bold)) {
                append("justWatch. ")
            }
        }

        val justWatchAttribution = buildAnnotatedString {
            append(attribution)
            append("For more details, ")
             pushStringAnnotation("URL", annotation = link)
            withStyle(style = androidx.compose.ui.text.SpanStyle(color = Color.Cyan, fontStyle = FontStyle.Italic)) {
                append("visit the TMDb website.")
            }
            pop()
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                    color = DarkOrange
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Watch",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            ProvidersList(
                modifier = modifier,
                providers = allProviders.toSet().toList().sortedBy { it.displayPriority },
                size = 30.dp
            )
        }
        Text(
            modifier = Modifier
                .padding(8.dp)
                .clickable {
                    uriHandler.openUri(link)
                },
            text = justWatchAttribution,
            style = MaterialTheme.typography.bodyMedium
        )

    }
}

@Composable
fun MovieCertifications(
    certifications: com.drew.themoviedatabase.data.model.Certifications?,
    movie: MovieDetailsResponse?,
) {

    val ageRate = movie?.certifications?.results
        ?.find { it.iso31661 == getWatchRegion() }?.releaseDates?.
        find { it.certification != "" }?.certification ?: ""
    val cert = certifications?.certifications?.get(getWatchRegion())
        ?.find { it.certification == ageRate }


    if (cert != null) {
        MovieTVCertifications(
            rating = "Rated $ageRate",
            ratingMeaning = cert.meaning,
            color = Color.Red,
            categoryTitle = "Parental Guide"
        )
    }
}



@Composable
@Preview(showBackground = true)
fun UserScoreItemPreview() {
//    UserScoreItem()
}

