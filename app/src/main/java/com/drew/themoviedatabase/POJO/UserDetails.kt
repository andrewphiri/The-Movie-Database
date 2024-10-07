package com.drew.themoviedatabase.POJO

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_details")
data class UserDetails(
    @PrimaryKey
    val id: Int = 0,
    val accountId: Int,
    val sessionId: String?,
    val username: String?,
    val name: String?,
    val avatar: String?,
    val hash: String? = null,
    val avatar_path: String? = null,
    val iso_639_1: String? = null,
    val iso_3166_1: String? = null,
    val include_adult: Boolean? =  false,
)
