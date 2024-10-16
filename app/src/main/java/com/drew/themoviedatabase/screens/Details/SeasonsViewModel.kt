package com.drew.themoviedatabase.screens.Details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drew.themoviedatabase.data.model.Episode
import com.drew.themoviedatabase.data.remote.SeasonResponse
import com.drew.themoviedatabase.data.repository.Movies.MovieDetailsReleaseData
import com.drew.themoviedatabase.data.repository.TVShows.TVShowsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeasonsViewModel @Inject constructor(
    val tvShowsRepository: TVShowsRepository
) : ViewModel() {

    private val _seasonDetails = MutableLiveData<SeasonResponse>()
    val seasonDetails: LiveData<SeasonResponse?> get()  = _seasonDetails

    fun fetchSeasonDetails(seriesID: Int, seasonNumber: Int) {
        viewModelScope.launch {
            tvShowsRepository.getSeasonsDetails(seriesId = seriesID, seasonNumber = seasonNumber) { details ->
                if (details != null) {
                    _seasonDetails.value = details.body()
                }
            }
        }
    }
}