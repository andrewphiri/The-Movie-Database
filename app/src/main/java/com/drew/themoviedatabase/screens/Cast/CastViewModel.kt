package com.drew.themoviedatabase.screens.Cast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.drew.themoviedatabase.data.remote.CombinedCreditsResponse
import com.drew.themoviedatabase.data.remote.PersonPhotosResponse
import com.drew.themoviedatabase.data.repository.CastPhotosPagingSource
import com.drew.themoviedatabase.data.repository.PersonRepository
import com.drew.themoviedatabase.data.repository.PopularPeoplePagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class CastViewModel @Inject constructor(
    private val repository: PersonRepository,
) : ViewModel(){
    private val _personDetails = MutableLiveData<com.drew.themoviedatabase.data.model.PersonDetails?>()
    val personDetails: LiveData<com.drew.themoviedatabase.data.model.PersonDetails?> get() = _personDetails

    private val _personImages = MutableLiveData<PersonPhotosResponse?>()
    val personImages: LiveData<PersonPhotosResponse?> get() = _personImages

    private val _combinedCredits = MutableLiveData<CombinedCreditsResponse?>()
    val combinedCredits: LiveData<CombinedCreditsResponse?> get() = _combinedCredits

    val popularPeople = Pager(
        config = PagingConfig(pageSize = 10),
        pagingSourceFactory = { PopularPeoplePagingSource(repository) }
    ).flow.cachedIn(viewModelScope)

    fun getPersonDetails(personId: Int) {
        repository.getPersonDetails(personId) { response ->
            if (response.isSuccessful) {
                _personDetails.value = response.body()
            }
        }
    }

    fun getPersonImages(personId: Int) : Flow<PagingData<com.drew.themoviedatabase.data.model.Photos>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { CastPhotosPagingSource(repository, personId) }
        ).flow.cachedIn(viewModelScope)
    }

    fun getCombinedCredits(personId: Int) {
        repository.getCombinedCredits(personId) { response ->
            if (response.isSuccessful) {
                _combinedCredits.value = response.body()
            }
        }
    }
}