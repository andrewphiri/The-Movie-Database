package com.drew.themoviedatabase.screens.Details

import android.content.res.Resources
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.drew.themoviedatabase.Navigation.MovieTopAppBar
import com.drew.themoviedatabase.Network.NetworkClient
import com.drew.themoviedatabase.Network.TVShowDetailsWithCastAndVideos
import com.drew.themoviedatabase.POJO.Photos
import com.drew.themoviedatabase.POJO.Provider
import com.drew.themoviedatabase.POJO.Reviews
import com.drew.themoviedatabase.POJO.TVShowDetails
import com.drew.themoviedatabase.POJO.Trailers
import com.drew.themoviedatabase.Utilities.findPreferredVideo
import com.drew.themoviedatabase.Utilities.getWatchRegion
import com.drew.themoviedatabase.composeUI.CastList
import com.drew.themoviedatabase.composeUI.ExpandableText
import com.drew.themoviedatabase.composeUI.OverviewText
import com.drew.themoviedatabase.composeUI.PhotosList
import com.drew.themoviedatabase.composeUI.ProvidersList
import com.drew.themoviedatabase.composeUI.RatingsAndVotes
import com.drew.themoviedatabase.composeUI.ReviewList
import com.drew.themoviedatabase.composeUI.TVShowList
import com.drew.themoviedatabase.composeUI.YouTubePlayer
import com.drew.themoviedatabase.screens.Home.TVShowsViewModel
import com.drew.themoviedatabase.ui.theme.DarkOrange
import kotlinx.coroutines.async
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

    var tvDetails by remember { mutableStateOf<TVShowDetailsWithCastAndVideos?>(null) }
    var similarTVShows by remember { mutableStateOf<List<TVShowDetails?>?>(null) }
    var reviews by remember { mutableStateOf<List<Reviews?>?>(null) }
    val photos: SnapshotStateList<Photos>? = remember { mutableStateListOf() }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            async { tvShowsViewModel.fetchTVDetailsWithCastAndVideos(seriesId) }.await()
            async { tvShowsViewModel.fetchSimilarTVShows(seriesId, 3) }.await()
            async { tvShowsViewModel.fetchReviews(seriesId) }.await()
            async { tvShowsViewModel.getPhotos(seriesId) }.await()
        }
    }

    tvShowsViewModel.tvShowsWithCastAndVideos.observe(lifecycleOwner) {
        tvDetails = it
    }

    tvShowsViewModel.similarTVShows.observe(lifecycleOwner) {
        similarTVShows = it
    }

    tvShowsViewModel.reviews.observe(lifecycleOwner) {
        reviews = it
    }

    tvShowsViewModel.tvShowImages.observe(lifecycleOwner) {
        photos?.addAll(it?.logos ?: emptyList())
        photos?.addAll(it?.posters ?: emptyList())
        photos?.addAll(it?.backdrops ?: emptyList())
    }


    if (tvDetails != null) {
        isLoading = false
    }

    if(tvDetails?.videos?.getResults()?.isNotEmpty() == true){
        isTrailersEmpty  = false
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            MovieTopAppBar(
                canNavigateBack = canNavigateBack,
                title = tvDetails?.name ?: "",
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
                    TVDetailsCard(
                        tvShow = tvDetails ,
                        trailers = tvDetails?.videos?.getResults(),
                        isTrailersEmpty = isTrailersEmpty
                    )
                }
                if (tvDetails?.watchProviders?.results?.isNotEmpty() == true) {
                    item {
                        TVWatchProviderList(
                            tvShow = tvDetails
                        )
                    }
                }
                item {
                    RatingsAndVotes(
                        voteAverage = tvDetails?.voteAverage,
                        voteCount = tvDetails?.voteCount
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

                if (similarTVShows?.isNotEmpty() == true) {
                    item {
                        TVShowList(
                            tvShows = similarTVShows,
                            onItemClick = navigateToTVShowDetails,
                            categoryTitle = "More like this",
                            color = Color.Red )
                    }
                }

                if (reviews?.isNotEmpty() == true) {
                    item {
                        ReviewList(
                            reviews = reviews,
                            categoryTitle = "Reviews",
                            onItemClick = { navigateToReviews(seriesId) }
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
fun TVDetailsCard(
    modifier: Modifier = Modifier,
    tvShow: TVShowDetailsWithCastAndVideos?,
    trailers: List<Trailers?>?,
    isTrailersEmpty: Boolean = false
) {
    val ageRate = tvShow?.contentRatings
        ?.results?.find { it.iso31661 == "US" }?.rating ?: ""

    var isExpanded by remember { mutableStateOf(false) }
    var isClickable by remember { mutableStateOf(false) }
    val finalText by remember { mutableStateOf(buildAnnotatedString { append(tvShow?.overview ?: "") }) }

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
