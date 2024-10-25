package com.drew.themoviedatabase.screens.Videos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drew.themoviedatabase.data.repository.YoutubeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class VideosViewModel @Inject constructor(
    private val youtubeRepository: YoutubeRepository
) : ViewModel() {

    private val _playlistID = MutableLiveData<String?>()
    val playlistID: LiveData<String?> get() = _playlistID

    private val _playlistItems = MutableStateFlow(mutableMapOf<String,List<String?>?>())
    val playlistItems = _playlistItems.asStateFlow()

    private val _myPlaylistItems = MutableStateFlow(mutableMapOf<String,List<String?>?>())
    val myPlaylistItems = _myPlaylistItems.asStateFlow()

    private val _channels = MutableLiveData<MutableMap<String, Pair<String, Boolean>>>()
    val channels: LiveData<MutableMap<String, Pair<String, Boolean>>> get() = _channels

    private val _queuedItems = MutableLiveData<List<String>?>()
    val queuedItems: LiveData<List<String>?> get() = _queuedItems

    suspend fun getPlaylistID(channelID: String) : String? {
         return coroutineScope {
             withContext(Dispatchers.IO) {
                 try {
                      youtubeRepository.getPlaylistID(id = channelID)?.items
                         ?.firstOrNull()?.contentDetails?.relatedPlaylists?.uploads
                 } catch (e: Exception) {
                     e.printStackTrace()
                     null
                 }
             }
        }
    }

    fun setQueuedItems(items: List<String>?) {
        _queuedItems.postValue(items)
    }

    fun clearPlaylistItems() {
        _playlistItems.value.clear()
    }

    fun setMyPlaylistItems(myList: MutableMap<String,List<String>?>) {
            _myPlaylistItems.update { currentList ->
                currentList.apply {
                    myList.toList().forEach { (key, value) ->
                        set(key, value?.shuffled())
                    }
                   // Log.d("VideosViewModel", currentList.toString())
                }
            }
        //Log.d("VideosViewModel", "Playlist items: ${_myPlaylistItems.value}")
    }

    fun removeFromMyPlaylistItems(channelName: String) {
        viewModelScope.launch {
            _myPlaylistItems.update { currentList ->
                currentList.apply {
                    remove(channelName)
                }
            }
        }
                           // Log.d("VideosViewModel_REMOVED", myPlaylistItems.value.toString())
    }

    fun setChannels(channels: MutableMap<String, Pair<String, Boolean>>) {
        _channels.value = channels
    }

    fun setChannels() {
        _channels.value = mutableMapOf(
            "Netflix" to Pair("UCWOA1ZGywLbqmigxE4Qlvuw", true),
            "Warner BrosPictures" to Pair("UCjmJDM5pRKbUlVIzDYYWb6g" , true) ,
            "Walt Disney" to Pair("UCuaFvcY4MhZY3U43mMt1dYQ", true),
            "HBO" to Pair("UCVTQuK2CaWaTgSsoNkn5AiQ", false),
            "Movieclips" to Pair("UC3gNmTGu-TTbFPpfSs5kNkg", false),
            "Rotten Tomatoes Trailers" to Pair("UCi8e0iOVk1fEOogdfu4YgfA", false),
            "Rotten Tomatoes TV" to Pair("UCz1GPotHecuLngiLuY739QQ",false)
        )
    }
//    fun updateChannels(key: String, value: Boolean) {
//        _channels.value?.set(key, value)
//    }

    fun getPlaylistItems(channelName: String, playlistId: String) {
        viewModelScope.launch {
            _playlistItems.value = mutableMapOf(channelName to youtubeRepository.getPlaylistItems(playlistId = playlistId)?.items
                ?.filter { it.status.privacyStatus == "public" }?.map { it.videoItem.videoId })
        }
    }

    fun setPlaylistItems(playListItems: MutableMap<String, List<String?>?>) {
        viewModelScope.launch {
            clearPlaylistItems()
            _playlistItems.updateAndGet {
               playListItems.toMutableMap()
            }
        }
        //Log.d("VideosScreen", "Playlist items: ${playlistItems?.value?.toList()}")

    }

    suspend fun getTheMyPlaylistItems(channelName: String, playlistId: String) :  MutableMap<String,List<String>?> {
       return coroutineScope {
            withContext(Dispatchers.IO) {
                try {
                    val myPlaylistItems =
                         mutableMapOf(channelName to youtubeRepository.getPlaylistItems(playlistId = playlistId)?.items
                            ?.filter { it.status.privacyStatus == "public" }
                            ?.map { it.videoItem.videoId }?.shuffled())
                    myPlaylistItems
                } catch (e: Exception) {
                    e.printStackTrace()
                    mutableMapOf()
                }

            }
        }
    }
}