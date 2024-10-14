package com.drew.themoviedatabase.screens.Details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drew.themoviedatabase.data.model.MyFavoriteMovies
import com.drew.themoviedatabase.data.model.MyFavoriteTVShows
import com.drew.themoviedatabase.data.model.MyRatedMovies
import com.drew.themoviedatabase.data.model.MyRatedTVShows
import com.drew.themoviedatabase.data.model.MyWatchlistMovies
import com.drew.themoviedatabase.data.model.MyWatchlistTVShows
import com.drew.themoviedatabase.data.repository.MoviesShowsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesShowsViewModel @Inject constructor(
    private val moviesShowsRepository: MoviesShowsRepository
) : ViewModel() {

    val getFavoriteMovies: StateFlow<List<MyFavoriteMovies>> =
        moviesShowsRepository.getFavoriteMovies().map {
            it
        }.stateIn(
            viewModelScope,
            WhileSubscribed(5000L),
            emptyList()
        )

    val getWatchlistMovies : StateFlow<List<MyWatchlistMovies>> =
    moviesShowsRepository.getWatchlistMovies().map {
        it
    }.stateIn(
    viewModelScope,
    WhileSubscribed(5000L),
    emptyList())

    val getRatedMovies: StateFlow<List<MyRatedMovies>> =
        moviesShowsRepository.getRatedMovies().map {
            it
        }.stateIn(
            viewModelScope,
            WhileSubscribed(5000L),
            emptyList()
        )

    val getFavoriteTVShows: StateFlow<List<MyFavoriteTVShows>> =
        moviesShowsRepository.getFavoriteTVShows().map {
            it
        }.stateIn(
            viewModelScope,
            WhileSubscribed(5000L),
            emptyList()
        )

    val getWatchlistTVShows: StateFlow<List<MyWatchlistTVShows>> =
        moviesShowsRepository.getWatchlistTVShows().map {
            it
        }.stateIn(
            viewModelScope,
            WhileSubscribed(5000L),
            emptyList()
        )

    val getRatedTVShows: StateFlow<List<MyRatedTVShows>> =
        moviesShowsRepository.getRatedTVShows().map {
            it
        }.stateIn(
            viewModelScope,
            WhileSubscribed(5000L),
            emptyList()
        )

    fun insertFavoriteMovie(movie: MyFavoriteMovies) {
        viewModelScope.launch {
            moviesShowsRepository.insertFavoriteMovie(movie)
        }
    }

    fun insertFavoriteMovies(movie: List<MyFavoriteMovies>) {
        viewModelScope.launch {
            moviesShowsRepository.insertFavoriteMovies(movie)
        }
    }

    fun deleteFavoriteMovie(movieId: Int) {
        viewModelScope.launch {
            moviesShowsRepository.deleteFavoriteMovie(movieId)
        }
    }

    fun updateFavoriteMovie(movie: MyFavoriteMovies) {
        viewModelScope.launch {
            moviesShowsRepository.updateFavoriteMovie(movie)
        }
    }

    fun insertWatchlistMovie(movie: MyWatchlistMovies) {
        viewModelScope.launch {
            moviesShowsRepository.insertWatchlistMovie(movie)
        }
    }

    fun insertWatchlistMovies(movie: List<MyWatchlistMovies>) {
        viewModelScope.launch {
            moviesShowsRepository.insertWatchlistMovies(movie)
        }
    }

    fun deleteWatchlistMovie(movieId: Int) {
        viewModelScope.launch {
            moviesShowsRepository.deleteWatchlistMovie(movieId)
        }
    }

    fun updateWatchlistMovie(movie: MyWatchlistMovies) {
        viewModelScope.launch {
            moviesShowsRepository.updateWatchlistMovie(movie)
        }
    }

    fun insertRatedMovie(movie: MyRatedMovies) {
        viewModelScope.launch {
            moviesShowsRepository.insertRatedMovie(movie)
        }
    }

    fun insertRatedMovies(movie: List<MyRatedMovies>) {
        viewModelScope.launch {
            moviesShowsRepository.insertRatedMovies(movie)
        }
    }

    fun deleteRatedMovie(movieId: Int) {
        viewModelScope.launch {
            moviesShowsRepository.deleteRatedMovie(movieId)
        }
    }

    fun updateRatedMovie(movie: MyRatedMovies) {
        viewModelScope.launch {
            moviesShowsRepository.updateRatedMovie(movie)
        }
    }



    fun insertFavoriteTVShow(movie: MyFavoriteTVShows) {
        viewModelScope.launch {
            moviesShowsRepository.insertFavoriteTVShow(movie)
        }
    }

    fun insertFavoriteTVShows(movie: List<MyFavoriteTVShows>) {
        viewModelScope.launch {
            moviesShowsRepository.insertFavoriteTVShows(movie)
        }
    }

    fun deleteFavoriteTVShow(seriesId: Int) {
        viewModelScope.launch {
            moviesShowsRepository.deleteFavoriteTVShow(seriesId)
        }
    }

    fun updateFavoriteTVShow(movie: MyFavoriteTVShows) {
        viewModelScope.launch {
            moviesShowsRepository.updateFavoriteTVShow(movie)
        }
    }

    fun insertWatchlistTVShow(movie: MyWatchlistTVShows) {
        viewModelScope.launch {
            moviesShowsRepository.insertWatchlistTVShow(movie)
        }
    }

    fun insertWatchlistTVShows(movie: List<MyWatchlistTVShows>) {
        viewModelScope.launch {
            moviesShowsRepository.insertWatchlistTVShows(movie)
        }
    }

    fun deleteWatchlistTVShow(seriesId: Int) {
        viewModelScope.launch {
            moviesShowsRepository.deleteWatchlistTVShow(seriesId)
        }
    }

    fun updateWatchlistTVShow(movie: MyWatchlistTVShows) {
        viewModelScope.launch {
            moviesShowsRepository.updateWatchlistTVShow(movie)
        }
    }

    fun insertRatedTVShow(movie: MyRatedTVShows) {
        viewModelScope.launch {
            moviesShowsRepository.insertRatedTVShow(movie)
        }
    }

    fun insertRatedTVShows(movie: List<MyRatedTVShows>) {
        viewModelScope.launch {
            moviesShowsRepository.insertRatedTVShows(movie)
        }
    }

    fun deleteRatedTVShow(seriesId: Int) {
        viewModelScope.launch {
            moviesShowsRepository.deleteRatedTVShow(seriesId)
        }
    }

    fun updateRatedTVShow(movie: MyRatedTVShows) {
        viewModelScope.launch {
            moviesShowsRepository.updateRatedTVShow(movie)
        }
    }


}