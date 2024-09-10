package com.drew.themoviedatabase.screens.Profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.serialization.Serializable

@Serializable
object ProfileScreen

@Composable
fun ProfileScreen() {
    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {
        Text(text = "Profile Screen")
    }
}