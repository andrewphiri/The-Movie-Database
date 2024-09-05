package com.drew.themoviedatabase.screens.Details

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.drew.themoviedatabase.Navigation.MovieTopAppBar
import com.drew.themoviedatabase.Network.MovieDetailsResponse
import com.drew.themoviedatabase.Network.NetworkClient
import com.drew.themoviedatabase.POJO.MovieDetailsReleaseData
import com.drew.themoviedatabase.POJO.Photos
import com.drew.themoviedatabase.POJO.Provider
import com.drew.themoviedatabase.POJO.Reviews
import com.drew.themoviedatabase.POJO.Trailers
import com.drew.themoviedatabase.R
import com.drew.themoviedatabase.Utilities.currencyFormatter
import com.drew.themoviedatabase.Utilities.findPreferredVideo
import com.drew.themoviedatabase.Utilities.getWatchRegion
import com.drew.themoviedatabase.composeUI.CastList
import com.drew.themoviedatabase.composeUI.ExpandableText
import com.drew.themoviedatabase.composeUI.MovieList
import com.drew.themoviedatabase.composeUI.OverviewText
import com.drew.themoviedatabase.composeUI.PhotosList
import com.drew.themoviedatabase.composeUI.ProvidersList
import com.drew.themoviedatabase.composeUI.RatingsAndVotes
import com.drew.themoviedatabase.composeUI.ReviewList
import com.drew.themoviedatabase.composeUI.YouTubePlayer
import com.drew.themoviedatabase.formatDuration
import com.drew.themoviedatabase.screens.Home.MoviesViewModel
import com.drew.themoviedatabase.ui.theme.DarkOrange
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.math.RoundingMode

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
    navigateToReviews: (Int) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    var trailers by remember {
        mutableStateOf(listOf<Trailers?>())
    }
    var isLoading by remember { mutableStateOf(true) }
    var isTrailersEmpty by remember { mutableStateOf(trailers.isEmpty()) }

    var movieDetails by remember { mutableStateOf<MovieDetailsResponse?>(null) }
    var similarMovies by remember { mutableStateOf<List<MovieDetailsReleaseData?>?>(null) }
    var reviews by remember { mutableStateOf<List<Reviews?>?>(null) }
    val photos: SnapshotStateList<Photos>? = remember { mutableStateListOf()}


    LaunchedEffect(Unit) {
        coroutineScope.launch {
           async { moviesViewModel.fetchMovieDetailsWithCastAndVideos(movieId) }.await()
            async { moviesViewModel.fetchSimilarMovies(movieId = movieId, pages = 1) }.await()
            async { moviesViewModel.getReviews(movieId) }.await()
            async { moviesViewModel.getPhotos(movieId) }.await()
        }
    }


    moviesViewModel.movieDetailsWithCastAndVideos.observe(lifecycleOwner) {
        movieDetails = it
    }

    moviesViewModel.similarMovies.observe(lifecycleOwner) {
        similarMovies = it
    }

    moviesViewModel.reviews.observe(lifecycleOwner) {
        reviews = it
    }

    moviesViewModel.movieImages.observe(lifecycleOwner) {
        photos?.addAll(it?.logos ?: emptyList())
        photos?.addAll(it?.posters ?: emptyList())
        photos?.addAll(it?.backdrops ?: emptyList())
    }


    if (movieDetails != null) {
        isLoading = false
    }

    if(movieDetails?.videos?.getResults()?.isNotEmpty() == true){
        isTrailersEmpty  = false
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            MovieTopAppBar(
                canNavigateBack = canNavigateBack,
                title = movieDetails?.title ?: "",
                navigateUp = navigateUp
            )
        }
    ) { innerPadding ->

            if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(50.dp) )
                    }

            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                item {
                    MovieDetailsCard(
                        movieDetails = movieDetails,
                        trailers = movieDetails?.videos?.getResults(),
                        isTrailersEmpty = isTrailersEmpty
                    )
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
                            voteCount = movieDetails?.voteCount
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
                    if (similarMovies?.isNotEmpty() == true) {
                        item {
                            MovieList(
                                movies = similarMovies,
                                color = Color.Cyan,
                                categoryTitle = "More like this",
                                onItemClick = navigateToDetails
                            )
                        }
                    }


                    if (reviews?.isNotEmpty() == true) {
                        item {
                            ReviewList(
                                reviews = reviews,
                                categoryTitle = "Reviews",
                                onItemClick = {navigateToReviews(movieId)}
                            )
                        }
                    }

                    if (photos?.isNotEmpty() == true ) {
                        item {
                            PhotosList(
                                photos = photos.shuffled(),
                                categoryTitle = "Photos" )
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
    isTrailersEmpty: Boolean = false
) {

    val ageRate = movieDetails?.certifications?.results
        ?.find { it.iso31661 == "US" }?.releaseDates?.
        find { it.certification != "" }?.certification ?: ""

    var isExpanded by remember { mutableStateOf(false) }
    //val textLayoutResultState = remember { mutableStateOf<TextLayoutResult?>(null) }
    var isClickable by remember { mutableStateOf(false) }
    val finalText by remember { mutableStateOf(buildAnnotatedString { append(movieDetails?.overview ?: "") }) }

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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
        ) {
            if (isTrailersEmpty) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth(),
                        text = "No trailers found",
                        textAlign = TextAlign.Center
                        )
                }
            } else {
                YouTubePlayer(
                    modifier = modifier,
                    videoId = findPreferredVideo(trailers) ?: ""
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
                model = NetworkClient().getPosterUrl(movieDetails?.posterPath),
                contentDescription = "${movieDetails?.title} poster",
                placeholder = null
            )
//            Text(
//                modifier = Modifier
//                    .fillMaxHeight()
//                    .fillMaxWidth()
//                    .padding(8.dp),
//                text = movieDetails?.overview ?: "",
//                style = MaterialTheme.typography.bodySmall
//            )
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

    val allProviders: SnapshotStateList<Provider>? = remember { mutableStateListOf() }
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
@Preview(showBackground = true)
fun UserScoreItemPreview() {
//    UserScoreItem()
}

