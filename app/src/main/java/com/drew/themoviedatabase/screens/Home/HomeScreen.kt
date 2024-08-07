package com.drew.themoviedatabase.screens.Home

import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.drew.themoviedatabase.Navigation.MovieTopAppBar
import com.drew.themoviedatabase.Network.NetworkClient
import com.drew.themoviedatabase.POJO.MovieDetails
import com.drew.themoviedatabase.POJO.MovieDetailsReleaseData
import com.drew.themoviedatabase.formatDuration
import kotlinx.serialization.Serializable

@Serializable
object HomeScreen

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    moviesViewModel: MoviesViewModel = hiltViewModel(),
    navigateToDetails: (Int, String,String) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val lazyListState = rememberLazyListState()
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    var popularMovies by remember { mutableStateOf<MovieDetailsReleaseData?>(null) }
    var topRatedMovies by remember { mutableStateOf<MovieDetailsReleaseData?>(null) }
    var nowPlayingMovies by remember { mutableStateOf<MovieDetailsReleaseData?>(null) }
    var upcomingMovies by remember { mutableStateOf<MovieDetailsReleaseData?>(null) }
    var trendingMovies by remember { mutableStateOf<MovieDetailsReleaseData?>(null) }

    // Observing movie lists
    moviesViewModel.popularMovies.observe(lifecycleOwner) {
        popularMovies = it
    }
    moviesViewModel.topRatedMovies.observe(lifecycleOwner){
        topRatedMovies = it
    }
    moviesViewModel.nowPlayingMovies.observe(lifecycleOwner) {
        nowPlayingMovies = it
    }
    moviesViewModel.upcomingMovies.observe(lifecycleOwner) {
        upcomingMovies = it
    }

    moviesViewModel.trendingMovies.observe(lifecycleOwner) {
        trendingMovies = it
    }

    // Loading state
    var isLoading by remember { mutableStateOf(true) }

        if (!popularMovies?.movieDetails.isNullOrEmpty()) {
        isLoading = false
    }

    // UI
    Scaffold(
        modifier = modifier,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                // Display loading spinner
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(50.dp)
                )
            } else {
                // Display movie lists
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Upcoming Movies
                    item {
                        trendingMovies?.let { trending ->
                            MovieList(
                                movies = trending,
                                categoryTitle = "Trending Movies",
                                color = Color.Green,
                                onItemClick = navigateToDetails
                            )
                        }
                    }

                    // Upcoming Movies
                    item {
                        upcomingMovies?.let { upcoming ->
                            MovieList(
                                movies = upcoming,
                                categoryTitle = "Upcoming Movies",
                                color = Color.Red,
                                onItemClick = navigateToDetails
                            )
                        }
                    }

                    // Popular Movies
                    item {
                        popularMovies?.let { popular ->
                            MovieList(
                                movies = popular,
                                categoryTitle = "Popular Movies",
                                color = Color.Blue,
                                onItemClick = navigateToDetails
                            )
                        }
                    }

                    // Now Playing Movies
                    item {
                        nowPlayingMovies?.let { nowPlaying ->
                            MovieList(
                                movies = nowPlaying,
                                categoryTitle = "Now Playing Movies",
                                color = Color.Yellow,
                                onItemClick = navigateToDetails
                            )
                        }
                    }

                    // Top Rated Movies
                    item {
                        topRatedMovies?.let { topRated ->
                            MovieList(
                                movies = topRated.copy(movieDetails = topRated.movieDetails?.sortedByDescending { it?.voteAverage }
                                    ?.distinct()),
                                categoryTitle = "Top Rated Movies",
                                color = Color.Magenta,
                                onItemClick = navigateToDetails
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
    movies: MovieDetailsReleaseData,
    categoryTitle: String = "",
    color: Color,
    onItemClick: (Int,String, String) -> Unit
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
            movies.movieDetails?.size?.let {
                items(it) { index ->
                    movies.movieDetails[index]?.let {
                        MovieItem(
                            modifier = modifier,
                            movie = it,
                            movieReleaseData = movies,
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
    movie: MovieDetails,
    movieReleaseData: MovieDetailsReleaseData,
    onItemClick: (Int, String, String) -> Unit
) {
    val ageRate = movieReleaseData.movieReleaseData?.find { it?.id == movie.id }?.results?.find { it.iso31661 == "US" }?.releaseDates?.find { it.certification != "" }?.certification ?: ""
    ElevatedCard(
        modifier = modifier
            .height(370.dp)
            .width(150.dp),
        onClick = { onItemClick(movie.id, ageRate, movie.title)}
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