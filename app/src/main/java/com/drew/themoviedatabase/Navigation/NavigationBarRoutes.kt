package com.drew.themoviedatabase.Navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.ui.graphics.vector.ImageVector
import com.drew.themoviedatabase.screens.Home.HomeScreen
import com.drew.themoviedatabase.screens.Profile.ProfileScreen
import com.drew.themoviedatabase.screens.Search.SearchScreen
import com.drew.themoviedatabase.screens.Videos.VideosScreen
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class NavigationBarRoutes<T : Any>(
    val name: String,
    val route: T,
    @Contextual val icon: ImageVector,
)



