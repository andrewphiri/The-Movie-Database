package com.drew.themoviedatabase.screens.Details

import android.media.MediaPlayer
import android.media.browse.MediaBrowser
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.drew.themoviedatabase.Navigation.MovieTopAppBar
import com.drew.themoviedatabase.Network.MovieDetailsResponse
import com.drew.themoviedatabase.Network.NetworkClient
import com.drew.themoviedatabase.POJO.CastMembers
import com.drew.themoviedatabase.POJO.MovieDetails
import com.drew.themoviedatabase.POJO.Trailers
import com.drew.themoviedatabase.formatDuration
import com.drew.themoviedatabase.screens.Home.MoviesViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class DetailsScreen(
    val movieId: Int,
    val ageRating: String = "N/A",
    val title: String,
)

@Composable
fun MovieDetailsScreen(
    modifier: Modifier = Modifier,
    ageRating: String,
    movieId: Int,
    title: String,
    canNavigateBack: Boolean = true,
    navigateUp: () -> Unit = {},
    moviesViewModel: MoviesViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    var trailers by remember {
        mutableStateOf(listOf<Trailers?>())
    }
    var isLoading by remember { mutableStateOf(true) }
    var isTrailersEmpty by remember { mutableStateOf(trailers.isEmpty()) }
    val scrollState = rememberScrollState()

    var movieDetails by remember { mutableStateOf<MovieDetailsResponse?>(null) }
    var castMembers by remember { mutableStateOf(listOf<CastMembers?>()) }

    Log.d("DetailsScreen", "DetailsScreen: $isLoading")

    LaunchedEffect(Unit) {
        coroutineScope.launch {
          val job = async {
               moviesViewModel.fetchMovieDetailsWithCastAndVideos(movieId)
           }
            job.await()
        }
    }


    moviesViewModel.movieDetailsWithCastAndVideos.observe(lifecycleOwner) {
        movieDetails = it
    }

//    moviesViewModel.trailers.observe(lifecycleOwner) {
//        trailers = it
//    }
//
//    moviesViewModel.cast.observe(lifecycleOwner) {
//        castMembers = it
//    }

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
                title = title,
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
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                item {
                    MovieDetailsScreen(
                        movieDetails = movieDetails,
                        trailers = movieDetails?.videos?.getResults(),
                        isTrailersEmpty = isTrailersEmpty
                    )
                }

                item {
                    CastList(
                        castMembers = movieDetails?.credits?.getCast()
                    )
                }

            }

        }
    }
}

@Composable
fun MovieDetailsScreen(
    modifier: Modifier = Modifier,
    movieDetails: MovieDetailsResponse?,
    trailers: List<Trailers?>?,
    isTrailersEmpty: Boolean = false
) {

    val ageRate = movieDetails?.certifications?.results
        ?.find { it.iso31661 == "US" }?.releaseDates?.
        find { it.certification != "" }?.certification ?: ""

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 8.dp, start = 8.dp),
            text = movieDetails?.title ?: "",
            style = MaterialTheme.typography.titleLarge,
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
                text = movieDetails?.releaseDate?.split('-')?.get(0) ?: "",
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
                    videoId = findPreferredVideo(trailers) ?: ""
                )
            }

        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                modifier = Modifier
                    .height(150.dp)
                    .width(100.dp),
                model = NetworkClient().getPosterUrl(movieDetails?.posterPath),
                contentDescription = "${movieDetails?.title} poster",
                placeholder = null
            )
            Text(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(8.dp),
                text = movieDetails?.overview ?: "",
                style = MaterialTheme.typography.bodySmall
            )
        }

}
}

/**
 * A composable that displays a YouTube video using a WebView.
 */

@Composable
fun YouTubePlayer(
    modifier: Modifier = Modifier,
    videoId: String
) {
    val htmlData = """
        <!DOCTYPE html>
        <html>
        <head>
          <style>
            body {
              margin: 0;
              padding: 0;
            }
            #player {
              position: absolute;
              top: 0;
              left: 0;
              width: 100%;
              height: 100%;
            }
            .video-container {
              position: relative;
              width: 100%;
              padding-bottom: 56.25%; /* 16:9 aspect ratio */
              height: 0;
              overflow: hidden;
            }
            .video-container iframe,
            .video-container object,
            .video-container embed {
              position: absolute;
              top: 0;
              left: 0;
              width: 100%;
              height: 100%;
            }
          </style>
        </head>
        <body>
          <div class="video-container">
            <div id="player"></div>
          </div>
          <script>
            var tag = document.createElement('script');
            tag.src = "https://www.youtube.com/iframe_api";
            var firstScriptTag = document.getElementsByTagName('script')[0];
            firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

            var player;
            function onYouTubeIframeAPIReady() {
              player = new YT.Player('player', {
                videoId: '$videoId',
                playerVars: { 
                'autoplay': 1, 
                'playsinline': 1, 
                'mute': 1, 
                'controls': 1, 
                'rel': 0 
                },
                
                events: {
                  'onReady': onPlayerReady,
                  'onStateChange': onPlayerStateChange
                }
              });
            }

            function onPlayerReady(events) {
              event.target.playVideo();
              player.unMute();
            }
            
           function onPlayerStateChange(events) {
              if (event.data == YT.PlayerState.CUED) {
                event.target.playVideo();
              }
            }
          </script>
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        modifier = modifier.background(Color.Black),
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                settings.javaScriptEnabled = true
                settings.cacheMode = WebSettings.LOAD_NO_CACHE
                webChromeClient = WebChromeClient()
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
                loadDataWithBaseURL(
                    "https://www.youtube.com",
                    htmlData,
                    "text/html",
                    "utf-8",
                    null
                )
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(
                "https://www.youtube.com",
                htmlData,
                "text/html",
                "utf-8",
                null
            )
        }
    )
}

@Composable
fun CastList(
    modifier: Modifier = Modifier,
    castMembers: List<CastMembers?>?
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        castMembers?.size?.let {
            items(it) {
                CastCard(castMember = castMembers[it])
            }
        }
    }
}

@Composable
fun CastCard(
    modifier: Modifier = Modifier,
    castMember: CastMembers?
) {
    ElevatedCard(
        modifier = modifier
            .width(120.dp)
            .height(250.dp),
        onClick = { /*TODO*/ }
    ) {
       AsyncImage(
           modifier = Modifier
               .height(150.dp)
               .width(100.dp),
           model = NetworkClient().getPosterUrl(castMember?.profilePath),
           contentDescription = "${castMember?.name} profile picture",
       )
       Text(
           modifier = Modifier.padding(4.dp),
           text = castMember?.name ?: "",
           style = MaterialTheme.typography.bodySmall,
           maxLines = 2,
           minLines = 2,
           overflow = TextOverflow.Ellipsis
       )
       Text(
           modifier = Modifier.padding(4.dp),
           text = castMember?.character ?: "",
           style = MaterialTheme.typography.bodySmall,
           fontSize = TextUnit(10f, TextUnitType.Sp),
           maxLines = 2,
           overflow = TextOverflow.Ellipsis
       )
    }
}

fun findPreferredVideo(trailers: List<Trailers?>?): String? {
    val officialTrailer = trailers?.find { it?.type == "Trailer" && it.name == "Official Trailer" }?.key
    if (officialTrailer != null) return officialTrailer

    val officialTeaser = trailers?.find { it?.type == "Teaser" && it.name == "Official Teaser" }?.key
    if (officialTeaser != null) return officialTeaser

    return trailers?.find { it?.type == "Teaser" }?.key
}