package com.drew.themoviedatabase.prefs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserPreferencesViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {

    val isDataInserted = userPreferencesRepository.isDataInserted

    fun updateIsDataInserted(isDataInserted: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateIsDataInserted(isDataInserted)
        }
    }
}