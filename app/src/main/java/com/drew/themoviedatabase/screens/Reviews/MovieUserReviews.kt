package com.drew.themoviedatabase.screens.Reviews

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.drew.themoviedatabase.MovieTopAppBar
import com.drew.themoviedatabase.screens.commonComposeUi.LoadingSpinner
import com.drew.themoviedatabase.screens.commonComposeUi.UserReviewList
import com.drew.themoviedatabase.screens.Details.MoviesViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class MoviesReviewsScreen(val id: Int)

@Composable
fun MovieUserReviewsScreen(
    id: Int,
    canNavigateBack: Boolean = true,
    onNavigateBack: () -> Unit,
    moviesViewModel: MoviesViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    var reviews by remember { mutableStateOf<List<com.drew.themoviedatabase.data.model.Reviews?>?>(null) }
    var isLoading by remember{ mutableStateOf(false) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            moviesViewModel.getReviews(id)
        }
    }

    moviesViewModel.reviews.observe(lifecycleOwner) {
        reviews = it
    }

    if (reviews?.isNotEmpty() == true) {
        isLoading = false
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            MovieTopAppBar(
                title = "Reviews",
                canNavigateBack = canNavigateBack ,
                navigateUp = onNavigateBack
                )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            if (isLoading) {
                LoadingSpinner(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    UserReviewList(
                        reviews = reviews
                    )
                }

           }
       }

    }
}

