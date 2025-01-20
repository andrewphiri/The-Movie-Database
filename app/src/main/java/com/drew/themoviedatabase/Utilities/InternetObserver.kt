package com.drew.themoviedatabase.Utilities

import kotlinx.coroutines.flow.Flow

interface InternetObserver {
    val isConnected: Flow<Boolean>
}