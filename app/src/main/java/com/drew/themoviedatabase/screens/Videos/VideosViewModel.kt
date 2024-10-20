package com.drew.themoviedatabase.screens.Videos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drew.themoviedatabase.data.repository.YoutubeRepository
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideosViewModel @Inject constructor(
    private val youtubeRepository: YoutubeRepository
) : ViewModel() {

    private val _playlistID = MutableLiveData<String?>()
    val playlistID: LiveData<String?> get() = _playlistID
    private val _playlistItems = MutableLiveData<List<String?>?>()
    val playlistItems: LiveData<List<String?>?> get() = _playlistItems

    fun getPlaylistID(channelId: String) {
        viewModelScope.launch {
            _playlistID.value  = youtubeRepository.getPlaylistID(id = channelId)?.items?.firstOrNull()?.contentDetails?.relatedPlaylists?.uploads
        }
    }

    fun getPlaylistItems(playlistId: String) {
        viewModelScope.launch {
            _playlistItems.value = youtubeRepository.getPlaylistItems(playlistId = playlistId)?.items?.filter { it.status.privacyStatus == "public" }?.map { it.videoItem.videoId }
        }
    }
}