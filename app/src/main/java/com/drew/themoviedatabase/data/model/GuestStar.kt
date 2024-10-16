package com.drew.themoviedatabase.data.model

data class GuestStar(
    val character: String,
    val credit_id: String,
    val order: Int,
    val adult: Boolean,
    val gender: Int,
    val id: Int,
    val known_for_department: String,
    val name: String,
    val original_name: String,
    val popularity: Double,
    val profile_path: String?
)
