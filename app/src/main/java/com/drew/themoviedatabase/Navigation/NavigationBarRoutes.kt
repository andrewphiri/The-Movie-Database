package com.drew.themoviedatabase.Navigation

import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class NavigationBarRoutes<T : Any>(
    val name: String,
    val route: T,
    @Contextual val icon: ImageVector,
)



