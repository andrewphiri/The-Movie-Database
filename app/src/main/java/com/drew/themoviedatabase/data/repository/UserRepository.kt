package com.drew.themoviedatabase.data.repository

import com.drew.themoviedatabase.data.model.UserDetails
import com.drew.themoviedatabase.data.room.MoviesShowsDao
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val moviesShowsDao: MoviesShowsDao
) {
    suspend fun insert(userDetails: UserDetails) {
        moviesShowsDao.insertUser(userDetails)
    }
    suspend fun delete(userDetails: UserDetails) {
        moviesShowsDao.deleteUser(userDetails)
    }
    suspend fun update(userDetails: UserDetails) {
        moviesShowsDao.updateUser(userDetails)
    }
    fun getUser() = moviesShowsDao.getUser()
}