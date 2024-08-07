package com.drew.themoviedatabase.screens.Home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drew.themoviedatabase.Network.API_KEY
import com.drew.themoviedatabase.Network.MovieApiService
import com.drew.themoviedatabase.Network.MovieResponse
import com.drew.themoviedatabase.Network.NetworkClient
import com.drew.themoviedatabase.Network.TrailersResponse
import com.drew.themoviedatabase.POJO.CastMembers
import com.drew.themoviedatabase.POJO.Movie
import com.drew.themoviedatabase.POJO.MovieDetails
import com.drew.themoviedatabase.POJO.MovieDetailsReleaseData
import com.drew.themoviedatabase.POJO.Trailers
import com.drew.themoviedatabase.data.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _upcomingMovies = MutableLiveData<MovieDetailsReleaseData?>()
    val upcomingMovies: LiveData<MovieDetailsReleaseData?> get()  = _upcomingMovies

    private val _popularMovies = MutableLiveData<MovieDetailsReleaseData?>()
    val popularMovies: LiveData<MovieDetailsReleaseData?> get()  = _popularMovies

    private val _nowPlaying = MutableLiveData<MovieDetailsReleaseData?>()
    val nowPlayingMovies: LiveData<MovieDetailsReleaseData?> get()  = _nowPlaying

    private val _topRatedMovies = MutableLiveData<MovieDetailsReleaseData?>()
    val topRatedMovies: LiveData<MovieDetailsReleaseData?> get()  = _topRatedMovies

    private val _trendingMovies = MutableLiveData<MovieDetailsReleaseData?>()
    val trendingMovies: LiveData<MovieDetailsReleaseData?> get()  = _trendingMovies

    private val _movieDetails = MutableLiveData<MovieDetails?>()
    val movieDetails: LiveData<MovieDetails?> get() = _movieDetails

    private val _cast = MutableLiveData<List<CastMembers?>>()
    val cast: LiveData<List<CastMembers?>> get() = _cast



    init {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                async { fetchPopularMovies() }.await()
                async { fetchTopRatedMovies() }.await()
                async { fetchNowPlayingMovies() }.await()
                async { fetchUpcomingMovies() }.await()
                async { fetchTrendingMovies() }.await()
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }


    fun fetchPopularMovies() {
        repository.fetchPopularMovieDetails(3) { fetchedMovies ->
            if (fetchedMovies != null) {
                _popularMovies.value = fetchedMovies
            }
        }
    }

    fun fetchTopRatedMovies() {
        repository.fetchTopRatedMovieDetails(3) { fetchedMovies ->
            if (fetchedMovies != null) {
                _topRatedMovies.value = fetchedMovies
            }
        }
    }

    fun fetchNowPlayingMovies() {
        repository.fetchNowPlayingMovieDetails(3) { fetchedMovies ->
            if (fetchedMovies != null) {
                _nowPlaying.value = fetchedMovies
            }
        }
    }


    fun fetchUpcomingMovies() {
        repository.fetchUpcomingMovieDetails(1) { fetchedMovies ->
            if (fetchedMovies != null) {
                _upcomingMovies.value = fetchedMovies
            }
        }
    }

    fun fetchTrendingMovies() {
        repository.fetchTrendingMovieDetails(1) { fetchedMovies ->
            if (fetchedMovies != null) {
                _trendingMovies.value = fetchedMovies
            }
        }
    }

    fun fetchTrailer(movieId: Int, callback: (List<Trailers>) -> Unit) {
        repository.fetchTrailers(movieId) {
            callback(it.body()?.getResults()?.map {
                Trailers(
                    name = it.name,
                    type = it.type,
                    site = it.site,
                    key = it.key
                )
            }
                ?: emptyList())
        }
    }

    fun fetchMovieDetails(movieId: Int) {
        repository.fetchMovieDetails(movieId) { movieDetails ->
            if (movieDetails.isSuccessful) {
                _movieDetails.value = movieDetails.body()
            }
        }
    }

    fun fetchCast(movieId: Int) {
        repository.fetchCast(movieId) { castResponse ->
            if (castResponse.isSuccessful) {
                _cast.value = castResponse.body()?.getCast()
            }
        }

    }
}