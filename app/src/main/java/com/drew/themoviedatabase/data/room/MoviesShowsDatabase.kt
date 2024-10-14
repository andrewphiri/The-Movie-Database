package com.drew.themoviedatabase.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.drew.themoviedatabase.data.model.MyFavoriteMovies
import com.drew.themoviedatabase.data.model.MyFavoriteTVShows
import com.drew.themoviedatabase.data.model.MyRatedMovies
import com.drew.themoviedatabase.data.model.MyRatedTVShows
import com.drew.themoviedatabase.data.model.MyWatchlistMovies
import com.drew.themoviedatabase.data.model.MyWatchlistTVShows
import com.drew.themoviedatabase.data.model.UserDetails

@Database(entities = [UserDetails::class,MyFavoriteMovies::class,
    MyFavoriteTVShows::class, MyRatedMovies::class,
    MyRatedTVShows::class,MyWatchlistMovies::class,
    MyWatchlistTVShows::class], version = 1, exportSchema = false)
abstract class MoviesShowsDatabase : RoomDatabase() {
    abstract fun moviesShowsDao(): MoviesShowsDao
}