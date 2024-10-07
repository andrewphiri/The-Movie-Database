package com.drew.themoviedatabase.repository.room

import com.drew.themoviedatabase.POJO.UserDetails
import com.drew.themoviedatabase.data.UserDao
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