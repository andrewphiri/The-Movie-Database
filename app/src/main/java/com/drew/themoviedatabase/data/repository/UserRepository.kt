package com.drew.themoviedatabase.data.repository

import com.drew.themoviedatabase.data.model.UserDetails
import com.drew.themoviedatabase.data.room.UserDao
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    suspend fun insert(userDetails: UserDetails) {
        userDao.insert(userDetails)
    }
    suspend fun delete(userDetails: UserDetails) {
        userDao.delete(userDetails)
    }
    suspend fun update(userDetails: UserDetails) {
        userDao.update(userDetails)
    }
    fun getUser() = userDao.getUser()
}