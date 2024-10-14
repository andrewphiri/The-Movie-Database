package com.drew.themoviedatabase.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.drew.themoviedatabase.data.model.MyFavoriteMovies
import com.drew.themoviedatabase.data.model.MyFavoriteTVShows
import com.drew.themoviedatabase.data.model.MyRatedMovies
import com.drew.themoviedatabase.data.model.MyRatedTVShows
import com.drew.themoviedatabase.data.model.MyWatchlistMovies
import com.drew.themoviedatabase.data.model.MyWatchlistTVShows
import com.drew.themoviedatabase.data.model.UserDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface MoviesShowsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(userDetails: UserDetails)

    @Delete
    suspend fun deleteUser(userDetails: UserDetails)

    @Update
    suspend fun updateUser(userDetails: UserDetails)

    @Query("SELECT * FROM user_details")
    fun getUser() : Flow<UserDetails>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteMovie(movie: MyFavoriteMovies)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteMovies(movie: List<MyFavoriteMovies>)

    @Query("DELETE FROM favorite_movies WHERE movieId = :movieId")
    suspend fun deleteFavoriteMovie(movieId: Int)

    @Update
    suspend fun updateFavoriteMovie(movie: MyFavoriteMovies)

    @Query("SELECT * FROM favorite_movies")
    fun getFavoriteMovies() : Flow<List<MyFavoriteMovies>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlistMovie(movie: MyWatchlistMovies)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlistMovies(movie: List<MyWatchlistMovies>)

    @Query("DELETE FROM watchlist_movies WHERE movieId = :movieId")
    suspend fun deleteWatchlistMovie(movieId: Int)

    @Update
    suspend fun updateWatchlistMovie(movie: MyWatchlistMovies)

    @Query("SELECT * FROM watchlist_movies")
    fun getWatchlistMovies() : Flow<List<MyWatchlistMovies>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRatedMovie(movie: MyRatedMovies)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRatedMovies(movie: List<MyRatedMovies>)

    @Query("DELETE FROM rated_movies WHERE movieId = :movieId")
    suspend fun deleteRatedMovie(movieId: Int)

    @Update
    suspend fun updateRatedMovie(movie: MyRatedMovies)

    @Query("SELECT * FROM rated_movies")
    fun getRatedMovies() : Flow<List<MyRatedMovies>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteTVShow(movie: MyFavoriteTVShows)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteTVShows(movie: List<MyFavoriteTVShows>)

    @Query("DELETE FROM favorite_tvshows WHERE seriesId = :seriesId")
    suspend fun deleteFavoriteTVShow(seriesId: Int)

    @Update
    suspend fun updateFavoriteTVShow(movie: MyFavoriteTVShows)

    @Query("SELECT * FROM favorite_tvshows")
    fun getFavoriteTVShows() : Flow<List<MyFavoriteTVShows>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlistTVShow(movie: MyWatchlistTVShows)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlistTVShows(movie: List<MyWatchlistTVShows>)

    @Query("DELETE FROM watchlist_tvshows WHERE seriesId = :seriesId")
    suspend fun deleteWatchlistTVShow(seriesId: Int)

    @Update
    suspend fun updateWatchlistTVShow(movie: MyWatchlistTVShows)

    @Query("SELECT * FROM watchlist_tvshows")
    fun getWatchlistTVShows() : Flow<List<MyWatchlistTVShows>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRatedTVShow(movie: MyRatedTVShows)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRatedTVShows(movie: List<MyRatedTVShows>)

    @Query("DELETE FROM rated_tvshows WHERE seriesId = :seriesId")
    suspend fun deleteRatedTVShow(seriesId: Int)

    @Update
    suspend fun updateRatedTVShow(movie: MyRatedTVShows)

    @Query("SELECT * FROM rated_tvshows")
    fun getRatedTVShows() : Flow<List<MyRatedTVShows>>
}