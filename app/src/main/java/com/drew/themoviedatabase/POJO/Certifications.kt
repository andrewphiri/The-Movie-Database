package com.drew.themoviedatabase.POJO

data class Certifications(
    val certifications: Map<String, List<Certification>>
)

data class Certification(
    val certification: String,
    val meaning: String,
    val order: Int
)