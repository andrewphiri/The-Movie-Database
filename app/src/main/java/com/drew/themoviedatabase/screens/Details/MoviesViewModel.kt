package com.drew.themoviedatabase.screens.Details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.drew.themoviedatabase.data.model.Trailers
import com.drew.themoviedatabase.data.remote.MovieDetailsResponse
import com.drew.themoviedatabase.data.remote.MovieImagesResponse
import com.drew.themoviedatabase.data.remote.MultiSearchResult
import com.drew.themoviedatabase.data.repository.MovieDetailsReleaseData
import com.drew.themoviedatabase.data.repository.Movies.MoviePhotosPagingSource
import com.drew.themoviedatabase.data.repository.Movies.MovieRepository
import com.drew.themoviedatabase.data.repository.Movies.MoviesReviewsPagingSource
import com.drew.themoviedatabase.data.repository.Movies.SimilarMoviesPagingSource
import com.drew.themoviedatabase.data.repository.MultiSearchPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val repository: MovieRepository,
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

    private val _similarMovies = MutableLiveData<List<MovieDetailsReleaseData?>?>()
    val similarMovies: LiveData<List<MovieDetailsReleaseData?>?> get()  = _similarMovies

    private val _recommendedMovies = MutableLiveData<List<MovieDetailsReleaseData?>?>()
    val recommendedMovies: LiveData<List<MovieDetailsReleaseData?>?> get()  = _recommendedMovies

    private val _movieDetails = MutableLiveData<com.drew.themoviedatabase.data.model.MovieDetails?>()
    val movieDetails: LiveData<com.drew.themoviedatabase.data.model.MovieDetails?> get() = _movieDetails

    private val _cast = MutableLiveData<List<com.drew.themoviedatabase.data.model.CastMembers?>>()
    val cast: LiveData<List<com.drew.themoviedatabase.data.model.CastMembers?>> get() = _cast

    private val _trailers = MutableLiveData<List<Trailers?>>()
    val trailers: LiveData<List<Trailers?>> get() = _trailers

    private val _movieDetailsWithCastAndVideos = MutableLiveData<MovieDetailsResponse?>()
    val movieDetailsWithCastAndVideos: LiveData<MovieDetailsResponse?> get() = _movieDetailsWithCastAndVideos

    private val _reviews = MutableLiveData<List<com.drew.themoviedatabase.data.model.Reviews?>?>()
    val reviews: LiveData<List<com.drew.themoviedatabase.data.model.Reviews?>?> get()  = _reviews

    private val _certifications = MutableLiveData<com.drew.themoviedatabase.data.model.Certifications?>()
    val certifications: LiveData<com.drew.themoviedatabase.data.model.Certifications?> get()  = _certifications

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isRefreshing = MutableLiveData<Boolean>(false)
    val isRefreshing: LiveData<Boolean> get() = _isRefreshing

    private val _totalPages = MutableLiveData<Int>()
    val totalPages: LiveData<Int> get() = _totalPages

    private val _movieImages = MutableLiveData<MovieImagesResponse?>()
    val movieImages: LiveData<MovieImagesResponse?> get() = _movieImages


    fun getMultiSearch(query: String) : Flow<PagingData<MultiSearchResult>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { MultiSearchPagingSource(repository, query) }
        ).flow.cachedIn(viewModelScope)
    }

    fun getSimilarMovies(movieId: Int) : Flow<PagingData<MovieDetailsReleaseData>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { SimilarMoviesPagingSource(repository, movieId) }
        ).flow.cachedIn(viewModelScope)
    }

    fun getPhotos(movieId: Int) : Flow<PagingData<com.drew.themoviedatabase.data.model.Photos>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { MoviePhotosPagingSource(repository, movieId) }
        ).flow.cachedIn(viewModelScope)
    }




    fun fetchNowPlayingMovies(pages: Int) {
//        val pageToFetch = getTotalPagesNowPlaying()
//        val pagesToFetch = if(pageToFetch > 10) 10 else pageToFetch
        repository.fetchNowPlayingMovieDetails(pages) { fetchedMovies ->
            if (fetchedMovies != null) {
                _nowPlaying.value = fetchedMovies
            }
        }
    }


    suspend fun fetchUpcomingMovies() {
        val pages = getTotalPagesUpcoming()
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

    fun fetchTrailer(movieId: Int) {
        repository.fetchTrailers(movieId) { trailersResponse ->
            if (trailersResponse.isSuccessful) {
                _trailers.value = trailersResponse.body()?.getResults()
            }
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

    fun fetchCertifications() {
        repository.fetchCertifications { certificationResponse ->
            if (certificationResponse.isSuccessful) {
                _certifications.value = certificationResponse.body()
            }
        }
    }

    fun fetchSimilarMovies(movieId: Int, pages: Int) {
        repository.fetchSimilarMovieDetails(movieId, pages) { fetchedMovies ->
            if (fetchedMovies != null) {
                _similarMovies.value = fetchedMovies
            }
        }
    }

    fun getReviews(movieId: Int) :  Flow<PagingData<com.drew.themoviedatabase.data.model.Reviews>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { MoviesReviewsPagingSource(repository, movieId) }
        ).flow.cachedIn(viewModelScope)
    }


    suspend fun getTotalPagesUpcoming() : Int  {
        return repository.getTotalPagesUpcoming()
    }

    suspend fun getTotalPagesTopRated() : Int  {
        return repository.getTotalPagesTopRated()
    }

    suspend fun getTotalPagesNowPlaying() : Int  {
        return repository.getTotalPagesNowPlaying()
    }

    suspend fun getTotalPagesPopular() : Int  {
        return repository.getTotalPagesPopular()
    }
}