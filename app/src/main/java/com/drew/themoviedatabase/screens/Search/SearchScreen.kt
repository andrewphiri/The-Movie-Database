package com.drew.themoviedatabase.screens.Search

import android.app.appsearch.SearchResults
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.drew.themoviedatabase.Network.MultiSearchResult
import com.drew.themoviedatabase.Network.NetworkClient
import com.drew.themoviedatabase.POJO.CastMembers
import com.drew.themoviedatabase.POJO.Provider
import com.drew.themoviedatabase.POJO.TVShowDetails
import com.drew.themoviedatabase.R
import com.drew.themoviedatabase.composeUI.ProvidersList
import com.drew.themoviedatabase.formatDuration
import com.drew.themoviedatabase.screens.Home.MoviesViewModel
import com.drew.themoviedatabase.ui.theme.DarkOrange
import kotlinx.serialization.Serializable
import retrofit2.http.Query

@Serializable
object SearchScreen

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    navigateToMovieDetailsScreen: (Int) -> Unit,
    navigateToCastDetailsScreen: (Int) -> Unit,
    navigateToTVShowDetailsScreen: (Int) -> Unit,
    moviesViewModel: MoviesViewModel = hiltViewModel()
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val searchResultsPager = moviesViewModel
        .getMultiSearch(searchQuery).collectAsLazyPagingItems()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                SearchTextField(
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .background(Color.Transparent),
                    searchItem = searchQuery,
                ) {
                    searchQuery = it
                }
                SearchResults(
                    searchResults = searchResultsPager,
                    navigateToMovieDetailsScreen = navigateToMovieDetailsScreen,
                    navigateToCastDetailsScreen = navigateToCastDetailsScreen,
                    navigateToTVShowDetailsScreen = navigateToTVShowDetailsScreen
                )
            }
    }
}

@Composable
fun SearchResults(
    modifier: Modifier = Modifier,
    searchResults: LazyPagingItems<MultiSearchResult>,
    navigateToMovieDetailsScreen: (Int) -> Unit,
    navigateToCastDetailsScreen: (Int) -> Unit,
    navigateToTVShowDetailsScreen: (Int) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(searchResults.itemCount) { index ->
            when(val item = searchResults[index]) {
                is MultiSearchResult.Movie -> {
                    MovieItemSearch(
                        movie = item,
                        onItemClick = navigateToMovieDetailsScreen
                    )
                }
                is MultiSearchResult.Person -> {
                    CastCardSearchItem(
                        person = item,
                        navigateToCastDetailsScreen = navigateToCastDetailsScreen
                    )
                }
                is MultiSearchResult.TV -> {
                    TVShowItemsSearch(
                        tvShow = item,
                        onItemClick = navigateToTVShowDetailsScreen
                    )
                }

                null -> return@items
            }
        }
    }
}

@Composable
fun SearchTextField(
    modifier: Modifier = Modifier,
    searchItem: String,
    onValueChange: (String) -> Unit,
) {
   Box(modifier = modifier) {
       Row(
           verticalAlignment = Alignment.CenterVertically,
           horizontalArrangement = Arrangement.spacedBy(16.dp)
       ) {
           OutlinedTextField(
               modifier = Modifier
                   .weight(3f, true),
               value = searchItem,
               onValueChange = onValueChange,
               leadingIcon = {
                   Icon(
                       imageVector = Icons.Default.Search,
                       contentDescription = "Search"
                   )
               },
               placeholder = {
                   Text(text = "Search")
               }
           )
           if(searchItem.isNotEmpty()) {
               Button(
                   modifier = Modifier.weight(1f, true),
                   onClick = { onValueChange("") }
               ) {
                   Text(text = "Cancel")
               }
           }
       }
   }
}

@Composable
fun TVShowItemsSearch(
    modifier: Modifier = Modifier,
    onItemClick: (Int) -> Unit,
    tvShow: MultiSearchResult.TV?,
    height: Dp = 350.dp,
    width: Dp = 120.dp
) {

    ElevatedCard(
        modifier = modifier
            .height(height)
            .width(width),
        onClick = {
            if (tvShow != null) {
                onItemClick(tvShow.id)
            }
        }
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth(),
            model = NetworkClient().getPosterUrl(tvShow?.poster_path),
            contentDescription = "${tvShow?.name} poster",
            placeholder = painterResource(R.drawable.mdb_1)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp),
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
                    text = tvShow?.vote_average?.toBigDecimal()?.setScale(1, java.math.RoundingMode.HALF_UP)
                        .toString(),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = tvShow?.name ?: "",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (tvShow?.first_air_date != null) {
                tvShow.first_air_date.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun CastCardSearchItem(
    modifier: Modifier = Modifier,
    navigateToCastDetailsScreen: (Int) -> Unit,
    person: MultiSearchResult.Person?,
    height: Dp = 350.dp,
    width: Dp = 120.dp
) {
    ElevatedCard(
        modifier = modifier
            .width(width)
            .height(height),
        onClick = {
            if (person != null) {
                navigateToCastDetailsScreen(person.id)
            }
        }
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxWidth(),
            model = NetworkClient().getPosterUrl(person?.profile_path),
            contentDescription = "${person?.name} profile picture",
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                modifier = Modifier.padding(4.dp),
                text = person?.name ?: "",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                modifier = Modifier.padding(4.dp),
                text = "Known for ${person?.known_for_department}",
                style = MaterialTheme.typography.bodySmall,
                fontSize = TextUnit(10f, TextUnitType.Sp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

    }
}

@Composable
fun MovieItemSearch(
    modifier: Modifier = Modifier,
    onItemClick: (Int) -> Unit,
    movie: MultiSearchResult.Movie?,
    height: Dp = 350.dp,
    width: Dp = 120.dp
) {
    ElevatedCard(
        modifier = modifier
            .height(height)
            .width(width),
        onClick = {
            if (movie != null) {
                onItemClick(movie.id)
            }
        }
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth(),
            model = NetworkClient().getPosterUrl(movie?.poster_path),
            contentDescription = "${movie?.title} poster",
            placeholder = painterResource(R.drawable.mdb_1)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp),
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

                if (movie?.vote_average != null) {
                    Text(
                        text = movie?.vote_average?.toBigDecimal()
                            ?.setScale(1, java.math.RoundingMode.HALF_UP)
                            .toString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            movie?.title?.let {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }


            if (movie?.release_date != null) {
                Text(
                    text = movie.release_date,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

    }
}
