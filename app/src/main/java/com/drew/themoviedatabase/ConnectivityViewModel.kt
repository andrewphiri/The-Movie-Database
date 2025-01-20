package com.drew.themoviedatabase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drew.themoviedatabase.Utilities.InternetConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ConnectivityViewModel @Inject constructor(
    internetConnectivityObserver: InternetConnectivityObserver
) : ViewModel() {

    val isConnected = internetConnectivityObserver
        .isConnected
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = true
        )
}