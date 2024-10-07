package com.drew.themoviedatabase.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.drew.themoviedatabase.POJO.UserDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userDetails: UserDetails)

    @Delete
    suspend fun delete(userDetails: UserDetails)

    @Update
    suspend fun update(userDetails: UserDetails)

    @Query("SELECT * FROM user_details")
    fun getUser() : Flow<UserDetails>
}