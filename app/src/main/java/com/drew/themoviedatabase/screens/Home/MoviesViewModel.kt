package com.drew.themoviedatabase.screens.Home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drew.themoviedatabase.Network.MovieDetailsResponse
import com.drew.themoviedatabase.POJO.CastMembers
import com.drew.themoviedatabase.POJO.MovieDetails
import com.drew.themoviedatabase.POJO.MovieDetailsReleaseData
import com.drew.themoviedatabase.POJO.Trailers
import com.drew.themoviedatabase.data.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _upcomingMovies = MutableLiveData<List<MovieDetailsReleaseData?>?>()
    val upcomingMovies: LiveData<List<MovieDetailsReleaseData?>?> get()  = _upcomingMovies

    private val _popularMovies = MutableLiveData<List<MovieDetailsReleaseData?>?>()
    val popularMovies: LiveData<List<MovieDetailsReleaseData?>?> get()  = _popularMovies

    private val _nowPlaying = MutableLiveData<List<MovieDetailsReleaseData?>?>()
    val nowPlayingMovies: LiveData<List<MovieDetailsReleaseData?>?> get()  = _nowPlaying

    private val _topRatedMovies = MutableLiveData<List<MovieDetailsReleaseData?>?>()
    val topRatedMovies: LiveData<List<MovieDetailsReleaseData?>?> get()  = _topRatedMovies

    private val _trendingMovies = MutableLiveData<List<MovieDetailsReleaseData?>?>()
    val trendingMovies: LiveData<List<MovieDetailsReleaseData?>?> get()  = _trendingMovies

    private val _movieDetails = MutableLiveData<MovieDetails?>()
    val movieDetails: LiveData<MovieDetails?> get() = _movieDetails

    private val _cast = MutableLiveData<List<CastMembers?>>()
    val cast: LiveData<List<CastMembers?>> get() = _cast

    private val _trailers = MutableLiveData<List<Trailers?>>()
    val trailers: LiveData<List<Trailers?>> get() = _trailers

    private val _movieDetailsWithCastAndVideos = MutableLiveData<MovieDetailsResponse?>()
    val movieDetailsWithCastAndVideos: LiveData<MovieDetailsResponse?> get() = _movieDetailsWithCastAndVideos

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isRefreshing = MutableLiveData<Boolean>(false)
    val isRefreshing: LiveData<Boolean> get() = _isRefreshing

    private val _totalPages = MutableLiveData<Int>()
    val totalPages: LiveData<Int> get() = _totalPages



    init {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                _isLoading.postValue(true)
                async { fetchPopularMovies() }.await()
                async { fetchTopRatedMovies() }.await()
                async { fetchNowPlayingMovies() }.await()
                async {
                    fetchUpcomingMovies()
                }.await()
                async { fetchTrendingMovies() }.await()
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    fun setRefreshing(isRefreshing: Boolean) {
        _isRefreshing.value = isRefreshing
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


    suspend fun fetchUpcomingMovies() {
        val pages = getTotalPages()
        val pageToFetch = if(pages > 10) 10 else 5
        //Log.d("MoviesViewModel", "Total pages1: ${pages}")
        repository.fetchUpcomingMovieDetails(pageToFetch) { fetchedMovies ->
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

    fun fetchMovieDetailsWithCastAndVideos(movieId: Int) {
        repository.fetchMovieDetailsWithCastAndVideos(movieId) { movieDetailsResponse ->
            if (movieDetailsResponse.isSuccessful) {
                _movieDetailsWithCastAndVideos.value = movieDetailsResponse.body()
            }
        }
    }
    suspend fun getTotalPages() : Int  {
        return repository.getTotalPagesUpcoming()
    }
}