package com.drew.themoviedatabase.screens.Cast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drew.themoviedatabase.Network.CombinedCreditsResponse
import com.drew.themoviedatabase.Network.PersonPhotosResponse
import com.drew.themoviedatabase.POJO.PersonDetails
import com.drew.themoviedatabase.data.PersonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CastViewModel @Inject constructor(
    private val repository: PersonRepository,
) : ViewModel(){
    private val _personDetails = MutableLiveData<PersonDetails?>()
    val personDetails: LiveData<PersonDetails?> get() = _personDetails

    private val _personImages = MutableLiveData<PersonPhotosResponse?>()
    val personImages: LiveData<PersonPhotosResponse?> get() = _personImages

    private val _combinedCredits = MutableLiveData<CombinedCreditsResponse?>()
    val combinedCredits: LiveData<CombinedCreditsResponse?> get() = _combinedCredits

    fun getPersonDetails(personId: Int) {
        repository.getPersonDetails(personId) { response ->
            if (response.isSuccessful) {
                _personDetails.value = response.body()
            }
        }
    }

    fun getPersonImages(personId: Int) {
        repository.getPersonImages(personId) { response ->
            if (response.isSuccessful) {
                _personImages.value = response.body()
            }
        }
    }

    fun getCombinedCredits(personId: Int) {
        repository.getCombinedCredits(personId) { response ->
            if (response.isSuccessful) {
                _combinedCredits.value = response.body()
            }
        }
    }
}