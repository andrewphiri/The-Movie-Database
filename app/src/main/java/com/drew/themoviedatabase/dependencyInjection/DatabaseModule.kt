package com.drew.themoviedatabase.dependencyInjection

import android.content.Context
import androidx.room.Room
import com.drew.themoviedatabase.data.room.MoviesShowsDao
import com.drew.themoviedatabase.data.room.MoviesShowsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    fun provideUserDao(moviesShowsDatabase: MoviesShowsDatabase): MoviesShowsDao {
        return moviesShowsDatabase.moviesShowsDao()
    }

    @Provides
    fun provideDatabase(@ApplicationContext context: Context): MoviesShowsDatabase {
        return Room.databaseBuilder(
            context,
            MoviesShowsDatabase::class.java,
            "user_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

}