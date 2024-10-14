package com.drew.themoviedatabase.data.repository

import com.drew.themoviedatabase.data.model.MyFavoriteMovies
import com.drew.themoviedatabase.data.model.MyFavoriteTVShows
import com.drew.themoviedatabase.data.model.MyRatedMovies
import com.drew.themoviedatabase.data.model.MyRatedTVShows
import com.drew.themoviedatabase.data.model.MyWatchlistMovies
import com.drew.themoviedatabase.data.model.MyWatchlistTVShows
import com.drew.themoviedatabase.data.room.MoviesShowsDao
import javax.inject.Inject

data class MoviesShowsRepository @Inject constructor(
    private val moviesShowsDao: MoviesShowsDao
) {
    suspend fun insertFavoriteMovie(movie: MyFavoriteMovies) {
        moviesShowsDao.insertFavoriteMovie(movie)
    }

    suspend fun insertFavoriteMovies(movie: List<MyFavoriteMovies>) {
        moviesShowsDao.insertFavoriteMovies(movie)
    }

    suspend fun deleteFavoriteMovie(movieId: Int) {
        moviesShowsDao.deleteFavoriteMovie(movieId)
    }
    suspend fun updateFavoriteMovie(movie: MyFavoriteMovies) {
        moviesShowsDao.updateFavoriteMovie(movie)
    }
    fun getFavoriteMovies() = moviesShowsDao.getFavoriteMovies()

    suspend fun insertWatchlistMovie(movie: MyWatchlistMovies) {
        moviesShowsDao.insertWatchlistMovie(movie)
    }

    suspend fun insertWatchlistMovies(movie: List<MyWatchlistMovies>) {
        moviesShowsDao.insertWatchlistMovies(movie)
    }

    suspend fun deleteWatchlistMovie(movieId: Int) {
        moviesShowsDao.deleteWatchlistMovie(movieId)
    }

    suspend fun updateWatchlistMovie(movie: MyWatchlistMovies) {
        moviesShowsDao.updateWatchlistMovie(movie)
    }

    fun getWatchlistMovies() = moviesShowsDao.getWatchlistMovies()

    suspend fun insertRatedMovie(movie: MyRatedMovies) {
        moviesShowsDao.insertRatedMovie(movie)
    }

    suspend fun insertRatedMovies(movie: List<MyRatedMovies>) {
        moviesShowsDao.insertRatedMovies(movie)
    }

    suspend fun deleteRatedMovie(movieId: Int) {
        moviesShowsDao.deleteRatedMovie(movieId)
    }

    suspend fun updateRatedMovie(movie: MyRatedMovies) {
        moviesShowsDao.updateRatedMovie(movie)
    }

    fun getRatedMovies() = moviesShowsDao.getRatedMovies()

    suspend fun insertFavoriteTVShow(movie: MyFavoriteTVShows) {
        moviesShowsDao.insertFavoriteTVShow(movie)
    }

    suspend fun insertFavoriteTVShows(movie: List<MyFavoriteTVShows>) {
        moviesShowsDao.insertFavoriteTVShows(movie)
    }

    suspend fun deleteFavoriteTVShow(seriesId: Int) {
        moviesShowsDao.deleteFavoriteTVShow(seriesId)
    }

    suspend fun updateFavoriteTVShow(movie: MyFavoriteTVShows) {
        moviesShowsDao.updateFavoriteTVShow(movie)
    }

    fun getFavoriteTVShows() = moviesShowsDao.getFavoriteTVShows()

    suspend fun insertWatchlistTVShow(movie: MyWatchlistTVShows) {
        moviesShowsDao.insertWatchlistTVShow(movie)
    }

    suspend fun insertWatchlistTVShows(movie: List<MyWatchlistTVShows>) {
        moviesShowsDao.insertWatchlistTVShows(movie)
    }

    suspend fun deleteWatchlistTVShow(seriesId: Int) {
        moviesShowsDao.deleteWatchlistTVShow(seriesId)
    }

    suspend fun updateWatchlistTVShow(movie: MyWatchlistTVShows) {
        moviesShowsDao.updateWatchlistTVShow(movie)
    }

    fun getWatchlistTVShows() = moviesShowsDao.getWatchlistTVShows()

    suspend fun insertRatedTVShow(movie: MyRatedTVShows) {
        moviesShowsDao.insertRatedTVShow(movie)
    }

    suspend fun insertRatedTVShows(movie: List<MyRatedTVShows>) {
        moviesShowsDao.insertRatedTVShows(movie)
    }

    suspend fun deleteRatedTVShow(seriesId: Int) {
        moviesShowsDao.deleteRatedTVShow(seriesId)
    }

    suspend fun updateRatedTVShow(movie: MyRatedTVShows) {
        moviesShowsDao.updateRatedTVShow(movie)
    }

    fun getRatedTVShows() = moviesShowsDao.getRatedTVShows()

}

