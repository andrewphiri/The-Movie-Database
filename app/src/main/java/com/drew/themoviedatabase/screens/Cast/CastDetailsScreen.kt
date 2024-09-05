package com.drew.themoviedatabase.screens.Cast

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.drew.themoviedatabase.Navigation.MovieTopAppBar
import com.drew.themoviedatabase.Network.NetworkClient
import com.drew.themoviedatabase.POJO.PersonDetails
import com.drew.themoviedatabase.R
import com.drew.themoviedatabase.composeUI.CombinedCreditsMovieList
import com.drew.themoviedatabase.composeUI.PhotosList
import com.drew.themoviedatabase.ui.theme.DarkOrange
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class PersonDetailsScreen(
    val personId: Int
)

@Composable
fun CastDetailsScreen(
    modifier: Modifier = Modifier,
    personId: Int,
    canNavigateBack: Boolean = true,
    navigateUp: () -> Unit = {},
    navigateToMovieDetails: (Int) -> Unit,
    navigateToTVShowDetails: (Int) -> Unit,
    castViewModel: CastViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    var personDetails by remember { mutableStateOf<PersonDetails?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isBiographyShowing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            castViewModel.getPersonDetails(personId)
            isLoading = false
        }
    }

    castViewModel.personDetails.observe(lifecycleOwner) {
        personDetails = it
    }


    Scaffold(
        topBar = {
            MovieTopAppBar(
                title = personDetails?.name ?: "",
                canNavigateBack = canNavigateBack,
                navigateUp = navigateUp
            )
        }
    ) { innerPadding ->

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(50.dp)
                )
            }

        } else {
            LazyColumn(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (personDetails != null) {
                    item {
                        personDetails?.let {
                            CastDetailItem(
                                personDetails = it,
                                onBiographyClick = { isBiographyShowing = true },
                                onDismiss = { isBiographyShowing = false },
                                isBiographyShowing = isBiographyShowing
                            )
                        }
                    }
                }
                if (personDetails?.combinedCredits?.getCombinedCredits()?.isNotEmpty() == true) {
                    item {
                        CombinedCreditsMovieList(
                            combinedCredits = personDetails?.combinedCredits?.getCombinedCredits(),
                            onItemClick = { id, mediaType ->
                                if (mediaType == "movie") navigateToMovieDetails(id)
                                else navigateToTVShowDetails(id)
                            },
                            categoryTitle = "Known For",
                            color = DarkOrange
                        )
                    }
                }

                if (personDetails?.images?.getPersonPhotos()?.isNotEmpty() == true) {
                    item {
                        PhotosList(
                            photos = personDetails?.images?.getPersonPhotos(),
                            categoryTitle = "Photos"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CastDetailItem(
    modifier: Modifier = Modifier,
    personDetails: PersonDetails?,
    onBiographyClick: () -> Unit,
    isBiographyShowing: Boolean,
    onDismiss: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (personDetails != null) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp, start = 8.dp, top = 8.dp),
                text = personDetails.name,
                style = MaterialTheme.typography.headlineMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
        }


        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ){
            if (personDetails != null) {
                AsyncImage(
                    modifier = Modifier.weight(1f, true),
                    model = NetworkClient().getPosterUrl(personDetails.profilePath, imageSize = "w500"),
                    contentDescription = "${personDetails.name} profile picture",
                    placeholder = painterResource(R.drawable.baseline_person_24)
                )
            }

            Column (
                modifier = Modifier.weight(1f, true),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (personDetails != null) {
                    Text(
                        modifier = Modifier.clickable(onClick = onBiographyClick),
                        text = personDetails.biography ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 9,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                BiographyDialog(
                    isBiographyShowing = isBiographyShowing,
                    onDismiss = onDismiss,
                    biography = personDetails?.biography ?: ""
                )

                Column (
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    if (personDetails?.birthday != null) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Born",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text =  personDetails.birthday,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )
                    }
                    if (personDetails?.placeOfBirth != null) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text =  personDetails.placeOfBirth,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BiographyDialog(
    modifier: Modifier = Modifier,
    isBiographyShowing: Boolean,
    onDismiss: () -> Unit,
    biography: String
) {
    if (isBiographyShowing) {
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
                            text = biography,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}