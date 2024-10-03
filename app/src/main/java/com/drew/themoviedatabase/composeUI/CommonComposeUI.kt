package com.drew.themoviedatabase.composeUI

import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImage
import com.drew.themoviedatabase.Network.NetworkClient
import com.drew.themoviedatabase.POJO.CastMembers
import com.drew.themoviedatabase.POJO.CombinedCredits
import com.drew.themoviedatabase.POJO.Crew
import com.drew.themoviedatabase.POJO.Genre
import com.drew.themoviedatabase.POJO.MovieDetailsReleaseData
import com.drew.themoviedatabase.POJO.Photos
import com.drew.themoviedatabase.POJO.PopularPerson
import com.drew.themoviedatabase.POJO.Provider
import com.drew.themoviedatabase.POJO.Reviews
import com.drew.themoviedatabase.POJO.TVShowDetails
import com.drew.themoviedatabase.POJO.Trailers
import com.drew.themoviedatabase.R
import com.drew.themoviedatabase.Utilities.getWatchRegion
import com.drew.themoviedatabase.formatDuration
import com.drew.themoviedatabase.ui.theme.DarkOrange
import java.math.RoundingMode

@Composable
fun ExpandableText(
    modifier: Modifier = Modifier,
    overview: AnnotatedString,
    isExpanded: Boolean = false,
    isClickable: Boolean = false,
    textLayoutResultState: MutableState<TextLayoutResult?> = remember { mutableStateOf<TextLayoutResult?>(null) },
    onExpandClick: () -> Unit,
    onClickable: () -> Unit,
    lineLimit: Int = 7
) {

    var finalOverview by remember { mutableStateOf(overview) }
    val textResult = textLayoutResultState.value
    LaunchedEffect(textResult) {
        if (textResult == null) return@LaunchedEffect

        when {
            isExpanded -> {
                finalOverview = buildAnnotatedString {
                    append(overview)
                    withStyle(SpanStyle(color = Color.Cyan)) {
                        append(" Show Less")
                    }
                }
            }
            !isExpanded && textResult.hasVisualOverflow -> {
                val lastCharIndex = textResult.getLineEnd(lineLimit - 1)
                val showMoreText = "... Show More"
                val adjustedText = overview
                    .substring(startIndex = 0, endIndex = lastCharIndex)
                    .dropLast(showMoreText.length)
                    .dropLastWhile { it == ' ' || it == '.'}

                finalOverview = buildAnnotatedString {
                    append(adjustedText)
                    withStyle(SpanStyle(color = Color.Cyan)) {
                        append(" Show More")
                    }
                }
                onClickable()
            }

        }
    }

    Text(
        modifier = modifier
            .clickable(enabled = isClickable, onClick = onExpandClick)
            .animateContentSize()
            .fillMaxWidth()
            .fillMaxHeight(),
        text = finalOverview,
        maxLines = if (isExpanded) Int.MAX_VALUE else 7,
        style = MaterialTheme.typography.bodySmall,
        onTextLayout = { textLayoutResultState.value = it }
    )
}

@Composable
fun CastCard(
    modifier: Modifier = Modifier,
    castMember: CastMembers?,
    navigateToCastDetailsScreen: (Int) -> Unit
) {
    ElevatedCard(
        modifier = modifier
            .width(120.dp)
            .height(250.dp),
        onClick = { navigateToCastDetailsScreen(castMember?.id ?: 0) }
    ) {
        AsyncImage(
            modifier = Modifier
                .height(150.dp)
                .width(120.dp),
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

@Composable
fun PopularPersonCard(
    modifier: Modifier = Modifier,
    person: PopularPerson?,
    navigateToCastDetailsScreen: (Int) -> Unit
) {
    ElevatedCard(
        modifier = modifier
            .width(150.dp)
            .height(320.dp),
        onClick = { navigateToCastDetailsScreen(person?.id ?: 0) }
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth(),
            model = NetworkClient().getPosterUrl(person?.profile_path),
            contentDescription = "${person?.name} profile picture",
        )
        Text(
            modifier = Modifier.padding(4.dp),
            text = person?.name ?: "",
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            modifier = Modifier.padding(4.dp),
            text = "Known for ${person?.known_for_department }" ?: "",
            style = MaterialTheme.typography.bodySmall,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            modifier = Modifier.padding(4.dp),
            text = "Popularity: ${person?.popularity ?: ""}",
            style = MaterialTheme.typography.bodySmall,
            overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun MovieList(
    modifier: Modifier = Modifier,
    movies: List<MovieDetailsReleaseData?>?,
    categoryTitle: String = "",
    color: Color,
    onItemClick: (Int) -> Unit
) {

    val watchRegion = getWatchRegion()
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
                color = color
            )
            Text(
                text = categoryTitle,
                style = MaterialTheme.typography.titleLarge
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            movies?.size?.let {
                items(it) { index ->
                    movies[index]?.let {
                        MovieItem(
                            modifier = modifier,
                            movie = it,
                            onItemClick = onItemClick,
                            watchRegion = watchRegion
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun CrewCard(
    modifier: Modifier = Modifier,
    crew: List<Crew>
) {
    val director = crew.find { it.job == "Director" }?.name
    val seriesCreator = crew.find { it.job == "Original Series Creator" }?.name
    val writers = crew.filter { it.job == "Writer" || it.job == "Screenplay" }
    val getPreferredCrew =
        if (writers.size > 2) "${writers[0].name}, ${writers[1].name} and others"
        else if (writers.size == 2) "${writers[0].name}, ${writers[1].name}" else if (writers.size == 1)
            writers[0].name else ""

    ElevatedCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (director != null) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Director",
                    style = MaterialTheme.typography.titleMedium
                )

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = director,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (seriesCreator != null) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Original Series Creator",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = seriesCreator,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (getPreferredCrew != "") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Writers",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = getPreferredCrew,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

        }
    }
}


@Composable
fun PopularPeopleList(
    modifier: Modifier = Modifier,
    peopleList: LazyPagingItems<PopularPerson>,
    categoryTitle: String = "",
    color: Color,
    onItemClick: (Int) -> Unit
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
                color = color
            )
            Text(
                text = categoryTitle,
                style = MaterialTheme.typography.titleLarge
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            peopleList?.itemCount.let {
                if (it != null) {
                    items(it) { index ->
                        peopleList[index]?.let {
                            PopularPersonCard(
                                modifier = modifier,
                                person = it,
                                navigateToCastDetailsScreen = onItemClick
                            )
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun MovieList(
    modifier: Modifier = Modifier,
    movies: LazyPagingItems<MovieDetailsReleaseData>,
    categoryTitle: String = "",
    color: Color,
    onItemClick: (Int) -> Unit
) {

    val watchRegion = getWatchRegion()
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
                color = color
            )
            Text(
                text = categoryTitle,
                style = MaterialTheme.typography.titleLarge
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {

                items(movies.itemCount) { index ->
                    movies[index]?.let {
                        MovieItem(
                            modifier = modifier,
                            movie = it,
                            onItemClick = onItemClick,
                            watchRegion = watchRegion
                        )
                    }
                }


        }

    }
}

@Composable
fun MovieItem(
    modifier: Modifier = Modifier,
    movie: MovieDetailsReleaseData,
    onItemClick: (Int) -> Unit,
    watchRegion: String = "US"
) {
    val ageRate = movie.certifications.results
        .find { it.iso31661 == "US" }?.releaseDates?.
        find { it.certification != "" }?.certification ?: ""

    val watchProvidersBuy = movie.watchProviders.results.get(watchRegion)?.buy?.toList()
    val watchProvidersRent = movie.watchProviders.results.get(watchRegion)?.rent?.toList()
    val watchProvidersFlatRate = movie.watchProviders.results.get(watchRegion)?.flatrate?.toList()
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

    ElevatedCard(
        modifier = modifier
            .height(390.dp)
            .width(150.dp),
        onClick = { onItemClick(movie.id)}
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth(),
            model = NetworkClient().getPosterUrl(movie.posterPath),
            contentDescription = "${movie.title} poster",
            placeholder = painterResource(R.drawable.mdb_1)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Movie Rating",
                    colorFilter = ColorFilter.tint(DarkOrange)
                )

                Text(
                    text = movie.voteAverage.toBigDecimal().setScale(1, java.math.RoundingMode.HALF_UP).toString(),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = movie.title,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = movie.releaseDate.split('-')[0],
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = ageRate,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = formatDuration(movie.runtime),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        if (allProviders != null) {
            ProvidersList(
                modifier = modifier,
                providers = allProviders.toSet().toList().take(4).sortedBy { it.displayPriority }
            )
        }
    }
}

@Composable
fun ProvidersList(
    modifier: Modifier = Modifier,
    providers: List<Provider>,
    size: Dp = 27.dp
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(providers) {
            WatchProvidersItem(
                provider = it,
                size = size
            )
        }
    }
}

@Composable
fun WatchProvidersItem(
    modifier: Modifier = Modifier,
    provider: Provider,
    size: Dp
    ) {
    Card(
        modifier = modifier
            .size(size),
    ) {
        AsyncImage(
            model = NetworkClient().getPosterUrl(provider.logoPath),
            contentDescription = provider.providerName
        )
    }
}

@Composable
fun CombinedCreditsMovieList(
    modifier: Modifier = Modifier,
    combinedCredits: List<CombinedCredits?>?,
    onItemClick: (Int, String) -> Unit,
    categoryTitle: String,
    color: Color
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
                color = color
            )
            Text(
                text = categoryTitle,
                style = MaterialTheme.typography.titleLarge
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            combinedCredits?.size?.let {
                items(it) {
                    combinedCredits.get(it)?.let { credit ->
                        CombinedCreditsItem(
                            modifier = modifier,
                            combinedCredits = credit,
                            onItemClick = { onItemClick(it, credit.mediaType) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CombinedCreditsItem(
    modifier: Modifier = Modifier,
    combinedCredits: CombinedCredits?,
    onItemClick: (Int) -> Unit
) {
    ElevatedCard(
        modifier = modifier
            .height(370.dp)
            .width(150.dp),
        onClick = {
            if (combinedCredits?.id != null) {
                onItemClick(combinedCredits.id)
            }
        }
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            model = NetworkClient().getPosterUrl(combinedCredits?.posterPath),
            contentDescription = "${combinedCredits?.title} poster",
            placeholder = null
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp)
                .weight(0.5f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                if (combinedCredits?.voteAverage != null) {
                    Image(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Movie Rating",
                        colorFilter = ColorFilter.tint(Color.Yellow)
                    )

                    Text(
                        text = combinedCredits?.voteAverage?.toBigDecimal()
                            ?.setScale(1, java.math.RoundingMode.HALF_UP).toString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = combinedCredits?.title ?: "",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (combinedCredits?.releaseDate != null) {
                    Text(
                        text = combinedCredits.releaseDate.split('-')[0],
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (combinedCredits?.character != null) {
                    Text(
                        text = combinedCredits?.character,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

        }
    }
}
/**
 * A composable that displays a YouTube video using a WebView.
 */

@Composable
fun YouTubePlayer(
    modifier: Modifier = Modifier,
    videoId: String?
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

    // Constraining the WebView within the box to respect the height and size
    Box(
        modifier = modifier
            .height(230.dp) // Explicit height
            .fillMaxWidth()
    ) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    settings.javaScriptEnabled = true
                    isVerticalScrollBarEnabled = false
                    isHorizontalScrollBarEnabled = false
                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S) {
                        setBackgroundColor(Color(0xFF171717).toArgb())
                    }

                    settings.useWideViewPort = false

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
                    null)

            },
            modifier = Modifier
                .height(230.dp) // Explicit height for the WebView
                .fillMaxWidth()  // Fill the width of the parent container
        )
    }
}

@Composable
fun YouTubeSinglePlayer(
    modifier: Modifier = Modifier,
    videoId: String?,
) {
    val htmlData = """
        <!DOCTYPE html>
        <html>
        <head>
          <style>
            body {
              margin: 0;
              padding: 0;
              background-color: black;
            }
            #player {
              position: absolute;
              top: 0;
              left: 0;
              width: 100vw;
              height: 100vh;
            }
            .video-container {
              position: relative;
              width: 100vw;
              height: 100vh;
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

    // Constraining the WebView within the box to respect the height and size
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    settings.javaScriptEnabled = true
                    isVerticalScrollBarEnabled = false
                    isHorizontalScrollBarEnabled = false
                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S) {
                        setBackgroundColor(Color(0xFF171717).toArgb())
                    }

                    settings.useWideViewPort = false

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
                    null)

            },
            modifier = Modifier
                .fillMaxSize() // Fill the width of the parent container
        )
    }
}

@Composable
fun CastList(
    modifier: Modifier = Modifier,
    castMembers: List<CastMembers?>?,
    crew: List<Crew>?,
    navigateToCastDetailsScreen: (Int) -> Unit,
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
                    .width(10.dp),
                color = Color.Cyan
            )
            Text(
                text = "Cast",
                style = MaterialTheme.typography.titleLarge
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            castMembers?.size?.let {
                items(it) {
                    CastCard(
                        castMember = castMembers[it],
                        navigateToCastDetailsScreen = navigateToCastDetailsScreen
                    )
                }
            }
        }

        CrewCard(
            modifier = modifier,
            crew = crew ?: emptyList()
        )
    }
}


@Composable
fun TVShowList(
    modifier: Modifier = Modifier,
    tvShows: LazyPagingItems<TVShowDetails>,
    onItemClick: (Int) -> Unit,
    categoryTitle: String = "",
    color: Color,
) {

    val watchRegion = getWatchRegion()

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
                color = color
            )
            Text(
                text = categoryTitle,
                style = MaterialTheme.typography.titleLarge
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {

                items(tvShows.itemCount) { index ->
                    tvShows[index]?.let {
                        TVShowItem(
                            modifier = modifier,
                            tvShow = it,
                            onItemClick = onItemClick,
                            watchRegion = watchRegion
                        )
                    }
                }
        }
    }
}

@Composable
fun TVShowList(
    modifier: Modifier = Modifier,
    tvShows: List<TVShowDetails?>?,
    onItemClick: (Int) -> Unit,
    categoryTitle: String = "",
    color: Color,
) {

    val watchRegion = getWatchRegion()

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
                color = color
            )
            Text(
                text = categoryTitle,
                style = MaterialTheme.typography.titleLarge
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {

            if (tvShows != null) {
                items(tvShows.size) { index ->
                    tvShows[index]?.let {
                        TVShowItem(
                            modifier = modifier,
                            tvShow = it,
                            onItemClick = onItemClick,
                            watchRegion = watchRegion
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TVShowItem(
    modifier: Modifier = Modifier,
    tvShow: TVShowDetails,
    onItemClick: (Int) -> Unit,
    watchRegion: String = "US"
) {
    val ageRate = tvShow.contentRatings
        ?.results?.find { it.iso31661 == "US" }?.rating ?: ""

    val watchProvidersBuy = tvShow.watchProviders.results.get(watchRegion)?.buy?.toList()
    val watchProvidersRent = tvShow.watchProviders.results.get(watchRegion)?.rent?.toList()
    val watchProvidersFlatrate = tvShow.watchProviders.results.get(watchRegion)?.flatrate?.toList()
    val watchProvidersFree = tvShow.watchProviders.results.get(watchRegion)?.free?.toList()

    val allProviders: SnapshotStateList<Provider>? = remember { mutableStateListOf() }
    if (watchProvidersFlatrate != null) {
        allProviders?.addAll(watchProvidersFlatrate)
    }
    if (watchProvidersFree != null) {
        allProviders?.addAll(watchProvidersFree)
    }

    if (watchProvidersRent != null) {
        allProviders?.addAll(watchProvidersRent)
    }
    if (watchProvidersBuy != null) {
        allProviders?.addAll(watchProvidersBuy)
    }

    ElevatedCard(
        modifier = modifier
            .height(400.dp)
            .width(150.dp),
        onClick = { onItemClick(tvShow.id)}
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth(),
            model = NetworkClient().getPosterUrl(tvShow.posterPath),
            contentDescription = "${tvShow.name} poster",
            placeholder = painterResource(R.drawable.mdb_1)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    imageVector = Icons.Default.Star,
                    contentDescription = "TV Show Rating",
                    colorFilter = ColorFilter.tint(DarkOrange)
                )

                Text(
                    text = tvShow.voteAverage.toBigDecimal().setScale(1, java.math.RoundingMode.HALF_UP).toString(),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = tvShow.name,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = tvShow.firstAirDate.split('-')[0],
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = ageRate,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "${tvShow.numberOfEpisodes}eps",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        if (allProviders != null) {
           ProvidersList(
               providers = allProviders.toSet().toList().take(4).sortedBy { it.displayPriority }
           )
        }
    }
}


@Composable
fun ReviewList(
    modifier: Modifier = Modifier,
    reviews: LazyPagingItems<Reviews>?,
    categoryTitle: String = "",
    onItemClick: () -> Unit
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
                color = DarkOrange
            )
            Text(
                text = categoryTitle,
                style = MaterialTheme.typography.titleLarge
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            reviews?.itemCount.let {
                if (it != null) {
                    items(it) { index ->
                        if (reviews != null) {
                            reviews.get(index)?.let { review ->
                                ReviewItemCard(
                                    modifier = modifier,
                                    reviews = review,
                                    onItemClick = onItemClick
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
fun ReviewItemCard(
    modifier: Modifier = Modifier,
    reviews: Reviews?,
    onItemClick: () -> Unit
) {
    ElevatedCard(
        modifier = modifier
            .height(170.dp)
            .width(300.dp)
            .clickable { onItemClick() },
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row {
                Image(
                    imageVector = Icons.Default.Star,
                    colorFilter = ColorFilter.tint(Color.Yellow),
                    contentDescription = "Review rating"
                )
                reviews?.authorDetails?.rating?.let {
                    Text(
                        text = it
                    )
                }
            }
            reviews?.content?.let {
                Text(
                    text = it,
                    maxLines = 7,
                    minLines = 7,
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun PhotosList(
    modifier: Modifier = Modifier,
    photos: LazyPagingItems<Photos>?,
    categoryTitle: String,
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
                color = DarkOrange
            )
            Text(
                text = categoryTitle,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = " ${photos?.itemCount}",
                style = MaterialTheme.typography.bodySmall
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (photos != null) {
                items(photos.itemCount) { index ->
                    photos[index]?.let { photo ->
                        PhotosItem(
                            modifier = modifier,
                            movieImages = photo
                        )
                    }
                }
            }

        }
    }

}

@Composable
fun PhotosItem(
    modifier: Modifier = Modifier,
    movieImages: Photos
) {
    ElevatedCard(
        modifier = modifier
            .height(150.dp)
            .width(120.dp),
    ) {
        AsyncImage(
            model = NetworkClient().getPosterUrl(movieImages.filePath) ,
            contentDescription = "" )
    }
}

@Composable
fun UserReviewList(
    modifier: Modifier = Modifier,
    reviews: List<Reviews?>?,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        reviews?.size?.let {
            items(it) { index ->
                reviews.get(index)?.let { review ->
                    UserReviewItemCard(
                        modifier = modifier,
                        reviews = review
                    )
                }
            }
        }
    }

}

@Composable
fun UserReviewItemCard(
    modifier: Modifier = Modifier,
    reviews: Reviews?
) {

    ElevatedCard(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if(reviews?.authorDetails?.rating != null) {
                Row {
                    Image(
                        imageVector = Icons.Default.Star,
                        colorFilter = ColorFilter.tint(DarkOrange),
                        contentDescription = "Review rating"
                    )
                    reviews.authorDetails.rating.let {
                        Text(
                            text = "$it/10"
                        )
                    }
                }
            }


            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                reviews?.author?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                reviews?.date?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            reviews?.content?.let {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
fun RatingsAndVotes(
    modifier: Modifier = Modifier,
    voteAverage: Double? = 0.0,
    voteCount: Int? = 0,
){
    ElevatedCard(
        modifier = modifier
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (voteAverage != null) {
                UserScoreItem(
                    voteAverage = voteAverage,
                    voteCount = voteCount
                )
                FavoriteItem()
                AddToWatchlistItem()
            }
        }
    }
}

@Composable
fun UserScoreItem(
    modifier: Modifier = Modifier,
    voteAverage: Double? = 0.0,
    voteCount: Int? = 0
) {
    var ratingPercentage by remember { mutableStateOf(0.0) }
    ratingPercentage = (voteAverage)?.times(10) ?: 0.0
    var rating by remember { mutableStateOf("") }
    rating = ratingPercentage?.toBigDecimal()?.setScale(0, RoundingMode.UP).toString()

    val percentage = buildAnnotatedString {
        append(rating)
        withStyle(style = androidx.compose.ui.text.SpanStyle(
            fontSize = TextUnit(6f, TextUnitType.Sp),
            fontStyle = FontStyle.Italic,
            baselineShift = BaselineShift.Superscript
        )) {
            append("%")
        }
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)) {

        Text(
            modifier = Modifier.padding(4.dp),
            text = "User Score",
            style = MaterialTheme.typography.bodyMedium
        )

        Card(
            shape = CircleShape
        ) {
            Box {
                CircularProgressIndicator(
                    modifier = Modifier.size(50.dp),
                    color = DarkOrange,
                    progress = { voteAverage?.div(10)?.toFloat() ?: 0f }
                )
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = percentage ,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Text(
            modifier = Modifier.padding(4.dp),
            text = voteCount.toString(),
            style = MaterialTheme.typography.bodySmall
        )

    }
}

@Composable
fun FavoriteItem(
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.size(50.dp),
        shape = CircleShape
    ) {
        Image(
            modifier = Modifier
                .size(50.dp)
                .padding(8.dp),
            painter = painterResource(id = R.drawable.baseline_favorite_24),
            contentDescription = "favorite",
        )
    }
}

@Composable
fun AddToWatchlistItem(
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.size(50.dp),
        shape = CircleShape
    ) {
        Image(
            modifier = Modifier
                .size(50.dp)
                .padding(8.dp),
            painter = painterResource(id = R.drawable.baseline_bookmark_24),
            contentDescription = "Add to watchlist",
        )
    }
}

@Composable
fun OverviewDialog(
    modifier: Modifier = Modifier,
    isOverviewShowing: Boolean,
    onDismiss: () -> Unit,
    overview: String
) {
    if (isOverviewShowing) {
        Dialog(
            onDismissRequest = onDismiss
        ) {
            Card(
                modifier = modifier,
                shape = MaterialTheme.shapes.extraLarge
            ) {
                LazyColumn{
                    item {
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text = overview,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OverviewText(
    modifier: Modifier = Modifier,
    overview: String
) {
    var isOverviewShowing by remember { mutableStateOf(false) }
    val textLayoutResultState: MutableState<TextLayoutResult?> = remember { mutableStateOf<TextLayoutResult?>(null) }
    val textLayoutResult = textLayoutResultState.value
    var hasVisualOverflow by remember { mutableStateOf(false)  }

    LaunchedEffect(textLayoutResult) {
        if (textLayoutResult == null) return@LaunchedEffect
        if (textLayoutResult.hasVisualOverflow) {
            hasVisualOverflow = true
        }
    }

    OverviewDialog(
        isOverviewShowing = isOverviewShowing,
        onDismiss = { isOverviewShowing = false },
        overview = overview
    )
    Text(
        modifier = modifier
            .clickable(enabled = hasVisualOverflow) {
                    if (hasVisualOverflow) {
                        isOverviewShowing = true
                    }
        },
        text = overview,
        style = MaterialTheme.typography.bodySmall,
        maxLines = 7,
        overflow = TextOverflow.Ellipsis,
        onTextLayout = { textLayoutResultState.value = it}
    )
}

@Composable
fun GenreList(
    modifier: Modifier = Modifier,
    genres: List<Genre?>?
) {
    LazyRow(
        modifier = modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        genres?.size?.let {
            items(it) { index ->
                genres.get(index)?.let {
                    ElevatedCard(
                        shape = RectangleShape
                    ) {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = it.name,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VideosList(
    modifier: Modifier = Modifier,
    trailers: List<Trailers?>?,
    listState: LazyListState
) {
        LazyColumn (
            modifier = modifier,
            state = listState,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (trailers != null) {
                items(trailers.size) { index ->
                    trailers[index]?.let { trailer ->

                            YouTubePlayer(
                                videoId = trailer.key
                            )
                            trailer.name?.let {
                                Text(
                                    modifier = Modifier.padding(8.dp),
                                    text = it,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                    }
                }
            }
        }
}

@Composable
fun MovieTVCertifications(
    modifier: Modifier = Modifier,
    ratingMeaning: String,
    rating: String,
    categoryTitle: String = "",
    color: Color,
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
                color = color
            )
            Text(
                text = categoryTitle,
                style = MaterialTheme.typography.titleLarge
            )
        }
        ElevatedCard(
            modifier = Modifier.padding(8.dp),
            shape = RectangleShape
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                text = rating,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                text = ratingMeaning,
            )
    }
}
}
@Composable
fun VideosPager(
    modifier: Modifier = Modifier,
    trailers: List<Trailers?>?,
) {
    val pagerState = rememberPagerState {
        trailers?.size ?: 0
    }

    Box(modifier = modifier.fillMaxSize()) {

        Column {
            HorizontalPager(modifier = Modifier.weight(1f, true), state = pagerState) {
                YouTubeSinglePlayer(
                    videoId = trailers?.get(it)?.key ?: ""
                )
            }
           Box(
               modifier = Modifier.weight(0.1f, true)
               .fillMaxWidth()
           ) {
               Row(
                   Modifier
                       .wrapContentHeight()
                       .fillMaxSize()
                       .align(Alignment.BottomCenter)
                       .padding(bottom = 8.dp),
                   horizontalArrangement = Arrangement.Center,
                   verticalAlignment = Alignment.CenterVertically
               ) {
                   repeat(pagerState.pageCount) { iteration ->
                       val color = if (pagerState.currentPage == iteration) Color.DarkGray else Color.Transparent
                       Box(
                           modifier = Modifier
                               .padding(2.dp)
                               .clip(CircleShape)
                               .border(Dp.Hairline, Color.LightGray)
                               .background(color)
                               .size(6.dp)
                       )
                   }
               }
           }
        }

    }
}