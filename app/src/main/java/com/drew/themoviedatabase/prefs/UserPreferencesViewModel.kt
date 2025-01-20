package com.drew.themoviedatabase.prefs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class UserPreferencesViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {

    val isDataInserted = userPreferencesRepository
        .isDataInserted
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            runBlocking { userPreferencesRepository.isDataInserted.first() })

    fun updateIsDataInserted(isDataInserted: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateIsDataInserted(isDataInserted)
        }
    }
}