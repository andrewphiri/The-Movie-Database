package com.drew.themoviedatabase.screens.Videos

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.drew.themoviedatabase.screens.commonComposeUi.VideosPager
import com.google.gson.JsonObject
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object VideosScreen
@Composable
fun VideosScreen(
    viewModel: VideosViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val playlistID by viewModel.playlistID.observeAsState()
    val playlistItems by viewModel.playlistItems.observeAsState()
    var playlist by remember { mutableStateOf<JsonObject?>(null) }

    LaunchedEffect(key1 = playlistID) {
        if (playlistID == null) {
            viewModel.getPlaylistID("UCjmJDM5pRKbUlVIzDYYWb6g")
            Log.d("VideosScreen", "Playlist ID: $playlistID")
        } else {
            viewModel.getPlaylistItems(playlistId = playlistID!!)
            Log.d("VideosScreen", "Playlist items: $playlistItems")
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

//        Text(
//            modifier = Modifier.clickable {
//                coroutineScope.launch {
//
//                    //val playlistItems = async { viewModel.getPlaylistItems(playlistId = "UUjmJDM5pRKbUlVIzDYYWb6g")  }.await()
//                    Log.d("VideosScreen", "Playlist items: $playlistItems")
//                }
//
//            },
//            text = playlistID.toString()
//        )

        VideosPager(
            trailers = playlistItems
        )
    }
}