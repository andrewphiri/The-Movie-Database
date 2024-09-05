package com.drew.themoviedatabase.screens.Reviews

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
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
import com.drew.themoviedatabase.Navigation.MovieTopAppBar
import com.drew.themoviedatabase.POJO.Reviews
import com.drew.themoviedatabase.composeUI.UserReviewList
import com.drew.themoviedatabase.screens.Home.MoviesViewModel
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
    var reviews by remember { mutableStateOf<List<Reviews?>?>(null) }
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
        topBar = {
            MovieTopAppBar(
                title = "Reviews",
                canNavigateBack = canNavigateBack ,
                navigateUp = onNavigateBack
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
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                UserReviewList(
                    reviews = reviews
                )
            }

        }

    }
}

