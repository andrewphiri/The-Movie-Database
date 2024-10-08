package com.drew.themoviedatabase.data.model

data class Certifications(
    val certifications: Map<String, List<com.drew.themoviedatabase.data.model.Certification>>
)

data class Certification(
    val certification: String,
    val meaning: String,
    val order: Int
)