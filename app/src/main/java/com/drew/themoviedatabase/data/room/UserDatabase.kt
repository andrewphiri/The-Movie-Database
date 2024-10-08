package com.drew.themoviedatabase.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.drew.themoviedatabase.data.model.UserDetails

@Database(entities = [UserDetails::class], version = 2, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}