package com.drew.themoviedatabase.composeUI

import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.drew.themoviedatabase.Network.NetworkClient
import com.drew.themoviedatabase.POJO.CastMembers
import com.drew.themoviedatabase.POJO.Crew
import com.drew.themoviedatabase.POJO.MovieDetailsReleaseData
import com.drew.themoviedatabase.POJO.Reviews
import com.drew.themoviedatabase.POJO.TVShowDetails
import com.drew.themoviedatabase.formatDuration
import com.drew.themoviedatabase.ui.theme.DarkOrange

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
fun TVShowList(
    modifier: Modifier = Modifier,
    tvShows: List<TVShowDetails?>?,
    onItemClick: (Int) -> Unit,
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
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            tvShows?.size?.let {
                items(it) { index ->
                    tvShows[index]?.let {
                        TVShowItem(
                            modifier = modifier,
                            tvShow = it,
                            onItemClick = onItemClick,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MovieItem(
    modifier: Modifier = Modifier,
    movie: MovieDetailsReleaseData,
    onItemClick: (Int) -> Unit
) {
    val ageRate = movie.certifications.results
        .find { it.iso31661 == "US" }?.releaseDates?.
        find { it.certification != "" }?.certification ?: ""
    ElevatedCard(
        modifier = modifier
            .height(370.dp)
            .width(150.dp),
        onClick = { onItemClick(movie.id)}
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            model = NetworkClient().getPosterUrl(movie.posterPath),
            contentDescription = "${movie.title} poster",
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
                Image(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Movie Rating",
                    colorFilter = ColorFilter.tint(Color.Yellow)
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
        modifier = modifier
            .background(Color.Black)
            .height(230.dp)
            .fillMaxWidth(),
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                settings.javaScriptEnabled = true
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
                setBackgroundColor(Color(0xFF171717).toArgb())
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
                null
            )
        }
    )
}

@Composable
fun CastList(
    modifier: Modifier = Modifier,
    castMembers: List<CastMembers?>?,
    crew: List<Crew>?
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
                    CastCard(castMember = castMembers[it])
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
fun TVShowItem(
    modifier: Modifier = Modifier,
    tvShow: TVShowDetails,
    onItemClick: (Int) -> Unit
) {
    val ageRate = tvShow.contentRatings
        ?.results?.find { it.iso31661 == "US" }?.rating ?: ""
    ElevatedCard(
        modifier = modifier
            .height(370.dp)
            .width(150.dp),
        onClick = { onItemClick(tvShow.id)}
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            model = NetworkClient().getPosterUrl(tvShow.posterPath),
            contentDescription = "${tvShow.name} poster",
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
                Image(
                    imageVector = Icons.Default.Star,
                    contentDescription = "TV Show Rating",
                    colorFilter = ColorFilter.tint(Color.Yellow)
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
    }
}

@Composable
fun ReviewList(
    modifier: Modifier = Modifier,
    reviews: List<Reviews?>?,
    categoryTitle: String = "",
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
        reviews?.size?.let {
            items(it) { index ->
                reviews.get(index)?.let { review ->
                    ReviewItemCard(
                        modifier = modifier,
                        reviews = review
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewItemCard(
    modifier: Modifier = Modifier,
    reviews: Reviews
) {
    ElevatedCard(
        modifier = modifier
            .height(170.dp).width(300.dp),
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
                Text(text = reviews.authorDetails.rating)
            }
            
            Text(
                text = reviews.content,
                maxLines = 7,
                minLines = 7,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}