package com.drew.themoviedatabase.screens.Details

import android.content.res.Resources
import android.util.Log
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.filter
import androidx.paging.map
import coil.compose.AsyncImage
import com.drew.themoviedatabase.MovieTopAppBar
import com.drew.themoviedatabase.Network.MovieDetailsResponse
import com.drew.themoviedatabase.Network.NetworkClient
import com.drew.themoviedatabase.Network.TVShowDetailsWithCastAndVideos
import com.drew.themoviedatabase.POJO.Certifications
import com.drew.themoviedatabase.POJO.Photos
import com.drew.themoviedatabase.POJO.Provider
import com.drew.themoviedatabase.POJO.Reviews
import com.drew.themoviedatabase.POJO.TVShowDetails
import com.drew.themoviedatabase.POJO.Trailers
import com.drew.themoviedatabase.Utilities.findPreferredVideo
import com.drew.themoviedatabase.Utilities.getWatchRegion
import com.drew.themoviedatabase.composeUI.CastList
import com.drew.themoviedatabase.composeUI.ExpandableText
import com.drew.themoviedatabase.composeUI.GenreList
import com.drew.themoviedatabase.composeUI.LoadingSpinner
import com.drew.themoviedatabase.composeUI.MovieTVCertifications
import com.drew.themoviedatabase.composeUI.OverviewText
import com.drew.themoviedatabase.composeUI.PhotosList
import com.drew.themoviedatabase.composeUI.ProvidersList
import com.drew.themoviedatabase.composeUI.RatingsAndVotes
import com.drew.themoviedatabase.composeUI.ReviewList
import com.drew.themoviedatabase.composeUI.TVShowList
import com.drew.themoviedatabase.composeUI.VideosList
import com.drew.themoviedatabase.composeUI.YouTubePlayer
import com.drew.themoviedatabase.screens.Home.TVShowsViewModel
import com.drew.themoviedatabase.screens.Profile.MyMoviesTVsViewModel
import com.drew.themoviedatabase.screens.Profile.UserViewModel
import com.drew.themoviedatabase.ui.theme.DarkOrange
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable
data class DetailsTVScreen(
    val seriesId: Int
)

@Composable
fun TVDetailsScreen(
    modifier: Modifier = Modifier,
    seriesId: Int,
    canNavigateBack: Boolean = true,
    navigateUp: () -> Unit = {},
    navigateToTVShowDetails: (Int) -> Unit = {},
    tvShowsViewModel: TVShowsViewModel = hiltViewModel(),
    navigateToCastDetails: (Int) -> Unit,
    navigateToReviews: (Int) -> Unit,
    navigateToTrailers: (Int) -> Unit,
    userViewModel: UserViewModel = hiltViewModel(),
    moviesTVsViewModel: MyMoviesTVsViewModel = hiltViewModel()
) {

    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val user by userViewModel.getUser.collectAsState(initial = null)
//    val favoriteTvShows = moviesTVsViewModel.getMyFavoriteTVShows(
//        user?.id ?: 0,
//        user?.sessionId ?: ""
//    ).map { data ->
//        data.filter { it.id == seriesId }
//    }.cachedIn(moviesTVsViewModel.viewModelScope)
//        .asLiveData()
//
//    favoriteTvShows.observe(lifecycleOwner) {
//        Log.d("FavoriteTVShows", "FavoriteTVShows: $it")
//        it.
//    }

    var isLoading by rememberSaveable { mutableStateOf(true) }
    var isTrailersEmpty by rememberSaveable { mutableStateOf(true) }
    var isFavorite by rememberSaveable { mutableStateOf(false) }
    var isAddedToWatchlist by rememberSaveable { mutableStateOf(false) }

    val tvDetails by tvShowsViewModel.tvShowsWithCastAndVideos.observeAsState()
    val similarTVShows = tvShowsViewModel.getSimilarTVShows(seriesId).collectAsLazyPagingItems()
    val reviews  = tvShowsViewModel.getReviews(seriesId).collectAsLazyPagingItems()
    val photos = tvShowsViewModel.getPhotos(seriesId).collectAsLazyPagingItems()
    val certifications by tvShowsViewModel.certifications.observeAsState()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            async { tvShowsViewModel.fetchTVDetailsWithCastAndVideos(seriesId) }.await()
            async { tvShowsViewModel.fetchCertifications() }.await()
            async { tvShowsViewModel.fetchReviews(seriesId) }.await()
            //async { tvShowsViewModel.getPhotos(seriesId) }.await()
        }
    }
    //Log.d("TVCertifications", "MovieCertifications: $certifications")

//    tvShowsViewModel.tvShowsWithCastAndVideos.observe(lifecycleOwner) {
//        tvDetails = it
//    }
//
//    tvShowsViewModel.reviews.observe(lifecycleOwner) {
//        reviews = it
//    }

//    tvShowsViewModel.tvShowImages.observe(lifecycleOwner) {
//        photos?.addAll(it?.logos ?: emptyList())
//        photos?.addAll(it?.posters ?: emptyList())
//        photos?.addAll(it?.backdrops ?: emptyList())
//    }


    if (tvDetails != null) {
        isLoading = false
    }

    if(tvDetails?.videos?.getResults()?.isNotEmpty() == true){
        isTrailersEmpty  = false
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            MovieTopAppBar(
                canNavigateBack = canNavigateBack,
                title = tvDetails?.name ?: "",
                navigateUp = navigateUp
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding).fillMaxSize()) {
            if (isLoading) {
                LoadingSpinner(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    item {
                        TVDetailsCard(
                            tvShow = tvDetails,
                            trailers = tvDetails?.videos?.getResults(),
                            isTrailersEmpty = isTrailersEmpty,
                            navigateToTrailers = { navigateToTrailers(seriesId) }
                        )
                    }

                    if (tvDetails?.genres?.isNotEmpty() == true) {
                        item {
                            GenreList(
                                genres = tvDetails?.genres
                            )
                        }
                    }

                    if (tvDetails?.watchProviders?.results?.get(getWatchRegion())?.buy?.isNotEmpty() == true ||
                        tvDetails?.watchProviders?.results?.get(getWatchRegion())?.rent?.isNotEmpty() == true ||
                        tvDetails?.watchProviders?.results?.get(getWatchRegion())?.flatrate?.isNotEmpty() == true ||
                        tvDetails?.watchProviders?.results?.get(getWatchRegion())?.free?.isNotEmpty() == true
                    ) {
                        item {
                            TVWatchProviderList(
                                tvShow = tvDetails
                            )
                        }
                    }
                    item {
                        RatingsAndVotes(
                            voteAverage = tvDetails?.voteAverage,
                            voteCount = tvDetails?.voteCount,
                            isFavorite = isFavorite ,
                            isAddedToWatchlist = isAddedToWatchlist ,
                            onAddToFavorites = {
                                //isFavorite = !isFavorite
                                if(user != null) {
                                    coroutineScope.launch {
                                       val addedToListResponse = moviesTVsViewModel.addToFavoriteOrWatchlist(
                                            mediaType = "tv",
                                            mediaID = seriesId,
                                            listType = "favorite",
                                            accountId = user?.id ?: 0,
                                            sessionId = user?.sessionId,
                                            addToList = isFavorite
                                        )
                                        if (addedToListResponse?.success == true) {
                                            isFavorite = !isFavorite
                                        }
                                    }
                                }
                            },
                            onAddToWatchlist = {
                                //isAddedToWatchlist = !isAddedToWatchlist
                                if(user != null) {
                                    coroutineScope.launch {
                                       val addedToListResponse = moviesTVsViewModel.addToFavoriteOrWatchlist(
                                            mediaType = "tv",
                                            mediaID = seriesId,
                                            listType = "watchlist",
                                            accountId = user?.id ?: 0,
                                            sessionId = user?.sessionId,
                                            addToList = isAddedToWatchlist
                                        )
                                        if (addedToListResponse?.success == true) {
                                            isAddedToWatchlist = !isAddedToWatchlist
                                        }
                                    }
                                }
                            }
                        )
                    }

                    if (tvDetails?.credits?.getCast()?.isNotEmpty() == true) {
                        item {
                            CastList(
                                castMembers = tvDetails?.credits?.getCast(),
                                crew = tvDetails?.credits?.getCrew(),
                                navigateToCastDetailsScreen = navigateToCastDetails
                            )
                        }
                    }

                    item {
                        OtherTVDetailsCard(
                            tvDetails = tvDetails
                        )
                    }


                    if (similarTVShows.itemCount > 0) {
                        item {
                            TVShowList(
                                tvShows = similarTVShows,
                                onItemClick = navigateToTVShowDetails,
                                categoryTitle = "More like this",
                                color = Color.Red
                            )
                        }
                    }

                    if (reviews.itemCount > 0) {
                        item {
                            ReviewList(
                                reviews = reviews,
                                categoryTitle = "Reviews",
                                onItemClick = { navigateToReviews(seriesId) }
                            )
                        }
                    }

                    if (photos.itemCount > 0) {
                        item {
                            PhotosList(
                                photos = photos,
                                categoryTitle = "Photos"
                            )
                        }
                    }

                    if (certifications != null) {
                        item {
                            TVCertifications(
                                certifications = certifications,
                                tvShow = tvDetails
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TVDetailsCard(
    modifier: Modifier = Modifier,
    tvShow: TVShowDetailsWithCastAndVideos?,
    trailers: List<Trailers?>?,
    isTrailersEmpty: Boolean = false,
    navigateToTrailers: () -> Unit
) {
    val ageRate = tvShow?.contentRatings
        ?.results?.find { it.iso31661 == "US" }?.rating ?: ""


    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 8.dp, start = 8.dp, top = 8.dp),
            text = tvShow?.name ?: "",
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
                text = tvShow?.firstAirDate ?: "",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = ageRate,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "${tvShow?.numberOfSeasons} seasons",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "${tvShow?.numberOfEpisodes} eps",
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
                    modifier = modifier,
                    videoId = findPreferredVideo(trailers) ?: ""
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
                model = NetworkClient().getPosterUrl(tvShow?.posterPath),
                contentDescription = "${tvShow?.name} poster",
                placeholder = null
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (tvShow?.tagline != null) {
                    Text(
                        text = tvShow.tagline,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
               OverviewText(
                   overview = tvShow?.overview ?: "",
               )
            }

        }
    }
}



@Composable
fun OtherTVDetailsCard(
    modifier: Modifier = Modifier,
    tvDetails: TVShowDetailsWithCastAndVideos?
) {

    val systemLocale = ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]
    val locale = if (systemLocale != null) {
        Locale(systemLocale.language, systemLocale.country)
    } else {
        Locale("en", "US")
    }
    val defaultLocale = locale.toLanguageTag()

    val originalLanguage = Locale(tvDetails?.originalLanguage ?: "en").getDisplayLanguage()

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
                    text = tvDetails?.status ?: "N/A",
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
                    text = originalLanguage ?: "N/A",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Column {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Creator",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = tvDetails?.createdBy?.find { it.name != "" }?.name ?: "N/A",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

//            Column {
//                Text(
//                    modifier = Modifier.fillMaxWidth(),
//                    text = "Revenue",
//                    style = MaterialTheme.typography.titleMedium
//                )
//                Text(
//                    modifier = Modifier.fillMaxWidth(),
//                    text = currencyFormatter(movieDetails?.revenue?.toDouble()) ?: "N/A",
//                    style = MaterialTheme.typography.bodyMedium
//                )
//            }

        }

    }
}

@Composable
fun TVWatchProviderList(
    modifier: Modifier = Modifier,
    tvShow: TVShowDetailsWithCastAndVideos?,
) {
    val uriHandler = LocalUriHandler.current
    val watchRegion = getWatchRegion()
    val watchProvidersBuy = tvShow?.watchProviders?.results?.get(watchRegion)?.buy?.toList()
    val watchProvidersRent = tvShow?.watchProviders?.results?.get(watchRegion)?.rent?.toList()
    val watchProvidersFlatrate = tvShow?.watchProviders?.results?.get(watchRegion)?.flatrate?.toList()
    val watchProvidersFree = tvShow?.watchProviders?.results?.get(watchRegion)?.free?.toList()
    val allProviders: SnapshotStateList<Provider>? = remember { mutableStateListOf() }
    if (watchProvidersRent != null) {
        allProviders?.addAll(watchProvidersRent)
    }
    if (watchProvidersBuy != null) {
        allProviders?.addAll(watchProvidersBuy)
    }
    if (watchProvidersFlatrate != null) {
        allProviders?.addAll(watchProvidersFlatrate)
    }
    if (watchProvidersFree != null) {
        allProviders?.addAll(watchProvidersFree)
    }
    if (allProviders != null) {
        val link = tvShow?.watchProviders?.results?.get(watchRegion)?.link ?: ""
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
                size = 44.dp
            )

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
}

@Composable
fun TVCertifications(
    certifications: Certifications?,
    tvShow: TVShowDetailsWithCastAndVideos?,
) {

    val ageRate = tvShow?.contentRatings
        ?.results?.find { it.iso31661 == getWatchRegion() }?.rating ?: ""
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