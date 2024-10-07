package com.drew.themoviedatabase.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.drew.themoviedatabase.POJO.UserDetails

@Database(entities = [UserDetails::class], version = 2, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}