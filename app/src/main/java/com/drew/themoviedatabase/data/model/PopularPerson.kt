package com.drew.themoviedatabase.data.model

data class PopularPerson(
    val id: Int,
    val name: String,
    val original_name: String,
    val known_for_department: String,
    val profile_path: String?,
    val popularity: Double
)