package com.drew.themoviedatabase.screens.Details

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.drew.themoviedatabase.MovieTopAppBar
import com.drew.themoviedatabase.data.model.Episode
import com.drew.themoviedatabase.data.remote.NetworkClient
import com.drew.themoviedatabase.data.remote.SeasonResponse
import com.drew.themoviedatabase.screens.commonComposeUi.LoadingSpinner
import com.drew.themoviedatabase.screens.commonComposeUi.OverviewText
import com.drew.themoviedatabase.ui.theme.DarkOrange
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class SeasonNavScreen(
    val seriesName: String,
    val seriesId: Int,
    val totalSeasons: Int
)

@Composable
fun SeasonsScreen(
    modifier: Modifier = Modifier,
    seriesName: String,
    seriesId: Int,
    totalSeasons: Int,
    navigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    navigateToEpisodeScreen: (Int?, Int?) -> Unit,
    seasonsViewModel: SeasonsViewModel = hiltViewModel()
) {
    val seasonsResponse by seasonsViewModel.seasonDetails.observeAsState()
    var isLoading by rememberSaveable { mutableStateOf(false) }
    val seasons = (1..totalSeasons).toList()
    val pagerState = rememberPagerState {
        seasons.size
    }
    var seasonNumber by rememberSaveable { mutableStateOf(1) }

    LaunchedEffect(pagerState.currentPage) {
        isLoading = true
        seasonNumber = pagerState.currentPage + 1
        //Log.d("Page", "$seasonNumber")
        seasonsViewModel.fetchSeasonDetails(seriesId, seasonNumber)
        delay(1000)
        isLoading = false
    }

   Scaffold(
       contentWindowInsets = WindowInsets(top = 20.dp),
       topBar = {
           MovieTopAppBar(
               canNavigateBack = canNavigateBack,
               title = seriesName,
               navigateUp = navigateUp
           )
       }
   ) {innerPadding ->
       Column(
           modifier = modifier
               .padding(innerPadding)
       ) {
           SeasonTabs(
               seasonTabs = seasons,
               pagerState = pagerState
           )

           Box(modifier = Modifier.fillMaxSize()) {
               if (isLoading) {
                   LoadingSpinner(modifier = Modifier.align(Alignment.Center))
               } else {
                   HorizontalPager(
                       state = pagerState,
                       pageSpacing = 16.dp
                   ) {
                       EpisodeList(
                           seasonResponse = seasonsResponse,
                           onEpisodeClicked = navigateToEpisodeScreen
                       )
                   }
               }

           }
       }
   }
}

@Composable
fun EpisodeList(
    modifier: Modifier = Modifier,
    seasonResponse: SeasonResponse?,
    onEpisodeClicked: (Int?, Int?) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
           SeasonItem(
              seasonResponse = seasonResponse
           )
        }

        if (seasonResponse?.episodes != null) {
            items(key = { index -> index }, count = seasonResponse.episodes.size) { index ->
                seasonResponse.episodes[index]?.let {
                    EpisodeItem(
                        episode = it,
                        onEpisodeClicked = onEpisodeClicked
                    )
                }
            }
        }
    }
}

@Composable
fun EpisodeItem(
    modifier: Modifier = Modifier,
    episode: Episode?,
    onEpisodeClicked: (Int?, Int?) -> Unit
) {
    ElevatedCard(
        modifier = modifier,
        onClick = { onEpisodeClicked(episode?.seasonNumber, episode?.episodeNumber) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                modifier = Modifier
                    .height(150.dp)
                    .width(120.dp),
                model = NetworkClient().getPosterUrl(episode?.stillPath),
                contentDescription = "${episode?.name} poster",
                placeholder = null
            )

            Column(
                modifier = Modifier.fillMaxHeight() ,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Episode ${episode?.episodeNumber}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = episode?.name ?: "No name available",
                    style = MaterialTheme.typography.titleMedium
                )
                OverviewText(
                    overview = episode?.overview ?: "No overview available" ,
                )
            }
        }
    }
}

@Composable
fun SeasonItem(
    modifier: Modifier = Modifier,
    seasonResponse: SeasonResponse?
) {

    OutlinedCard(modifier = Modifier) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                modifier = Modifier
                    .height(175.dp)
                    .width(100.dp),
                model = NetworkClient().getPosterUrl(seasonResponse?.poster_path),
                contentDescription = "${seasonResponse?.name} poster",
                placeholder = null
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OverviewText(
                    overview = seasonResponse?.overview ?: "No overview available",
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Season Rating",
                        colorFilter = ColorFilter.tint(DarkOrange)
                    )

                    Text(
                        text = seasonResponse?.vote_average?.toBigDecimal()?.setScale(1, java.math.RoundingMode.HALF_UP).toString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

        }
    }

}

@Composable
fun SeasonTabs(
    seasonTabs: List<Int>,
    pagerState: PagerState
) {
    val coroutineScope = rememberCoroutineScope()
    ScrollableTabRow( selectedTabIndex = pagerState.currentPage
    ) {
        seasonTabs.forEachIndexed { index, seasonNumber ->
            Tab(
                modifier = Modifier.background(
                    color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary else Color.Unspecified
                ),
                text = { Text(text = "Season $seasonNumber") },
                selected = pagerState.currentPage == index,
                selectedContentColor = MaterialTheme.colorScheme.onPrimary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    }
}