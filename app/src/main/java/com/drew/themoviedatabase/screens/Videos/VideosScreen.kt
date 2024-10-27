package com.drew.themoviedatabase.screens.Videos

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drew.themoviedatabase.data.model.YoutubeChannels
import com.drew.themoviedatabase.screens.commonComposeUi.LoadingSpinner
import com.drew.themoviedatabase.screens.commonComposeUi.VideosPager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

@Serializable
object VideosScreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun VideosScreen(
    viewModel: VideosViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    //val playlistID by viewModel.playlistID.observeAsState()
    val playlistItems by viewModel.playlistItems.collectAsState()
    val myPlayListItems by  viewModel.myPlaylistItems.collectAsState()
    //val myPlayListItemsStateFlow by viewModel.myPlaylistItems.collectAsState(listOf())
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val channels by viewModel.getChannels.collectAsState()
//    var myChannels: MutableMap<String, Pair<String,Boolean>>? = remember { mutableStateMapOf() }
//    //myChannels = channels
//
//    //val myPlayListItemsStateFlow by viewModel.myPlaylistItems.collectAsState(listOf())
//    var myItems by rememberSaveable { mutableStateOf(listOf<String?>()) }
    val myQueuedItems by viewModel.queuedItems.observeAsState()

    var isLoading by remember { mutableStateOf(true) }

//    LaunchedEffect(key1 = Unit) {
//        viewModel.setChannels()
//    }


//    myItems = playlistItems.values.flatMap { list ->
//        list?.filterNotNull()
//            .orEmpty()
//    }

//   LaunchedEffect(playlistItems) {
//       viewModel.setQueuedItems(playlistItems.values.flatMap { list ->
//           list?.filterNotNull()
//               .orEmpty()
//       })
//   }

   //Log.d("VideosScreen_OUT_OF_LOOP", "Viewmodel; items_: ${myItems}")
   //Log.d("QUEUED_OUT_OF_LOOP", "Viewmodel; items_: ${myQueuedItems}")
   // Log.d("VideosScreen_OUT_OF_LOOP", "Viewmodel; items_: ${playlistItems}")
    LaunchedEffect(key1 = channels) {
       // Log.d("VideosScreen_OUT_OF_LOOP", "Viewmodel; items_: ${playlistItems}")
        try {
            if (channels != null) {
                for (item in channels) {
                    if (item.isChannelEnabled) {
                            val playlistID = async { viewModel.getPlaylistID(item.channelID) }.await()
                       // Log.d("VideosScreen", "Playlist ID: $playlistID")
                            if (playlistID != null) {
                                val myList =  async { viewModel.getTheMyPlaylistItems(channelName = item.channelName,playlistId = playlistID) }.await()
                                if (myList != null) {
                                    viewModel.setMyPlaylistItems(myList = myList)
                                        //Log.d("VideosScreen", "Playlist items: ${myPlayListItems}")
                                }
                            }

                    } else {
                        viewModel.removeFromMyPlaylistItems(item.channelName)
                    }
                }

                viewModel.setPlaylistItems(myPlayListItems)
                coroutineScope.launch {
                    viewModel.setQueuedItems(playlistItems.values.flatMap { list ->
                        list?.filterNotNull()
                            .orEmpty()
                    })
                }
              // Log.d("VideosScreen_OUT_OF_LOOP", "Viewmode; items_: ${playlistItems}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

   // Log.d("VideosScreen", "Playlist items: ${myPlayListItems}")

    if (myQueuedItems?.isNotEmpty() == true) {
        isLoading = false
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                LoadingSpinner()
            }

            if (channels.all { !it.isChannelEnabled }) {
                Text(
                    text = "No channels selected",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent)
                ) {
                    IconButton(
                        modifier = Modifier
                            .align(Alignment.TopEnd),
                        onClick = {
                            isExpanded = !isExpanded
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More"
                        )
                    }
                    channels.let { theChannels ->
                        YoutubeChannelsList(
                            modifier = Modifier.align(Alignment.TopEnd),
                            onClickEnable = { channel ->
                                try {
                                    //myChannels[key] = Pair(myChannels[key]!!.first, enabled)
                                    //Log.d("VideosScreen", "Channels: $enabled")
                                    // Log.d("VideosScreen", "Channels: $theChannels")
                                    //viewModel.setChannels(myChannels)

                                    coroutineScope.launch {

                                        viewModel.updateChannel(channel)

                                        if (channel.isChannelEnabled) {

                                            val playlistID = async {
                                                    viewModel.getPlaylistID(
                                                        channel.channelID
                                                    )
                                            }.await()
                                            // Log.d("VideosScreen", "Playlist ID: $playlistID")
                                            if (playlistID != null) {
                                                val myList = async {
                                                    viewModel.getTheMyPlaylistItems(
                                                        channelName = channel.channelName,
                                                        playlistId = playlistID
                                                    )
                                                }.await()

                                                if (myList != null) {
                                                    viewModel.setMyPlaylistItems(myList = myList)
                                                }
                                                isLoading = true

                                                isLoading = playlistItems.isEmpty()
                                                viewModel.setPlaylistItems(myPlayListItems)
                                            }
                                            withContext(Dispatchers.Main) {
                                                viewModel.setQueuedItems(playlistItems.values.flatMap { list ->
                                                    list?.filterNotNull()
                                                        .orEmpty()
                                                })
                                            }


                                        } else {
                                            // Log.d("VideosScreen", "Playlist ID: $playlistID")

                                            viewModel.removeFromMyPlaylistItems(channel.channelName)
                                            isLoading = true
                                            //viewModel.clearPlaylistItems()
                                            isLoading = playlistItems.isEmpty()
                                            // Log.d("VideosScreen", "Playlist items: ${playlistItems?.toList()}")
                                            //delay(3000)
                                            viewModel.setPlaylistItems(myPlayListItems)
                                            withContext(Dispatchers.Main) {
                                                viewModel.setQueuedItems(playlistItems.values.flatMap { list ->
                                                    list?.filterNotNull()
                                                        .orEmpty()
                                                })
                                            }

                                            // Log.d("VideosScreen", "Playlist items: ${playlistItems?.toList()}")


                                            //Log.d("VideosViewModel_REMOVED", playlistItems.toString())

                                            //viewModel.removeFromMyPlaylistItems(myPlayListItems)
                //                                            Log.d("VideosScreen", "MYPlaylist items: ${myPlayListItems.toList()}")
                //                                            Log.d("VideosScreen", "Playlist items: ${playlistItems?.toList()}")

                                        }
                                        //isLoading = false
                                        //Log.d("VideosScreen", "Playlist items: ${myPlayListItems.toList()}")
                                        // viewModel.setPlaylistItems(myPlayListItems.shuffled())
                                        // Log.d("VideosScreen_OUT_OF_LOOP", "Viewmode; items_: ${playlistItems}"
                                    }

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            },
                            isExpanded = isExpanded,
                            myChannels = theChannels,
                            onDismissRequest = {
                                isExpanded = false
                            }
                        )
                    }
                }


                myQueuedItems?.let {
                    VideosPager(
                        trailers = it,
                        videoIdsString = it.joinToString(separator = ",")
                    )
                }

            }
        }
    }
}

@Composable
fun YoutubeChannelsList(
    modifier: Modifier = Modifier,
    myChannels: List<YoutubeChannels>,
    onClickEnable: (YoutubeChannels) -> Unit,
    onDismissRequest: () -> Unit,
    isExpanded: Boolean
) {

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = onDismissRequest
        ) {
                for (channel in myChannels) {
                    //enabled = myChannels.values.elementAt(index)
                    //Log.d("VideosScreen_1", "Channels: ${myChannels.values.elementAt(index)}")
                    YouTubeChannelsItem(
                        youtubeChannel = channel,
                        onCheckedChange = onClickEnable
                    )
                }
        }
    }
}

@Composable
fun YoutubeChannelsList(
    modifier: Modifier = Modifier,
    myChannels: Map<String, Boolean>,
) {
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        items(myChannels.size) { index ->
            val channelName = myChannels.keys.elementAt(index)
            val checked = myChannels.values.elementAt(index)
        }
    }
}

@Composable
fun YouTubeChannelsItem(
    modifier: Modifier = Modifier,
    youtubeChannel: YoutubeChannels,
    onCheckedChange: (YoutubeChannels) -> Unit
) {
    var checked by rememberSaveable { mutableStateOf(youtubeChannel.isChannelEnabled) }
    DropdownMenuItem(
        modifier = modifier,
        text = { Text(text = youtubeChannel.channelName) },
        onClick = {},
        trailingIcon = {
            Switch(
                checked = checked,
                onCheckedChange = {
                    checked = it
                    onCheckedChange(youtubeChannel.copy(isChannelEnabled = it))
                }
            )
        }
    )
}

fun listSaver(): Saver<MutableList<String>, List<String>> {
    return Saver(
        save = { it.toList() }, // Save as a regular List<String>
        restore = { it.toMutableList() } // Restore as a mutable list
    )
}