package com.drew.themoviedatabase.screens.Home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.drew.themoviedatabase.Network.CertificationsResponse
import com.drew.themoviedatabase.Network.MovieDetailsResponse
import com.drew.themoviedatabase.Network.MovieImagesResponse
import com.drew.themoviedatabase.Network.MultiSearchResult
import com.drew.themoviedatabase.POJO.CastMembers
import com.drew.themoviedatabase.POJO.Certifications
import com.drew.themoviedatabase.POJO.MovieDetails
import com.drew.themoviedatabase.POJO.MovieDetailsReleaseData
import com.drew.themoviedatabase.POJO.Photos
import com.drew.themoviedatabase.POJO.Reviews
import com.drew.themoviedatabase.POJO.Trailers
import com.drew.themoviedatabase.repository.Movies.MoviePhotosPagingSource
import com.drew.themoviedatabase.repository.Movies.MovieRepository
import com.drew.themoviedatabase.repository.Movies.MoviesReviewsPagingSource
import com.drew.themoviedatabase.repository.Movies.NowPlayingMoviesPagingSource
import com.drew.themoviedatabase.repository.Movies.PopularMoviesPagingSource
import com.drew.themoviedatabase.repository.Movies.SimilarMoviesPagingSource
import com.drew.themoviedatabase.repository.Movies.TopRatedMoviesPagingSource
import com.drew.themoviedatabase.repository.Movies.TrendingMoviesPagingSource
import com.drew.themoviedatabase.repository.Movies.UpcomingMoviesPagingSource
import com.drew.themoviedatabase.repository.MultiSearchPagingSource
import com.drew.themoviedatabase.repository.TrendingMediaPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val repository: MovieRepository,
    ) : ViewModel() {

    val moviesPopular = Pager(
        config = PagingConfig(pageSize = 10),
        pagingSourceFactory = { PopularMoviesPagingSource(repository) }
    ).flow.cachedIn(viewModelScope)

    val moviesTopRated = Pager(
        config = PagingConfig(pageSize = 10),
        pagingSourceFactory = { TopRatedMoviesPagingSource(repository) }
    ).flow.cachedIn(viewModelScope)

    val moviesUpcoming = Pager(
        config = PagingConfig(pageSize = 40, initialLoadSize = 40),
        pagingSourceFactory = { UpcomingMoviesPagingSource(repository) }
    ).flow.cachedIn(viewModelScope)

    val moviesNowPlaying = Pager(
        config = PagingConfig(pageSize = 10),
        pagingSourceFactory = { NowPlayingMoviesPagingSource(repository) }
    ).flow.cachedIn(viewModelScope)

    val moviesTrending = Pager(
        config = PagingConfig(pageSize = 10),
        pagingSourceFactory = { TrendingMoviesPagingSource(repository) }
    ).flow.cachedIn(viewModelScope)
    val trendingMedia = Pager(
        config = PagingConfig(pageSize = 10),
        pagingSourceFactory = { TrendingMediaPagingSource(repository, "") }
    ).flow.cachedIn(viewModelScope)

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

    private val _movieDetails = MutableLiveData<MovieDetails?>()
    val movieDetails: LiveData<MovieDetails?> get() = _movieDetails

    private val _cast = MutableLiveData<List<CastMembers?>>()
    val cast: LiveData<List<CastMembers?>> get() = _cast

    private val _trailers = MutableLiveData<List<Trailers?>>()
    val trailers: LiveData<List<Trailers?>> get() = _trailers

    private val _movieDetailsWithCastAndVideos = MutableLiveData<MovieDetailsResponse?>()
    val movieDetailsWithCastAndVideos: LiveData<MovieDetailsResponse?> get() = _movieDetailsWithCastAndVideos

    private val _reviews = MutableLiveData<List<Reviews?>?>()
    val reviews: LiveData<List<Reviews?>?> get()  = _reviews

    private val _certifications = MutableLiveData<Certifications?>()
    val certifications: LiveData<Certifications?> get()  = _certifications

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

    fun getPhotos(movieId: Int) : Flow<PagingData<Photos>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { MoviePhotosPagingSource(repository, movieId) }
        ).flow.cachedIn(viewModelScope)
    }

    fun setRefreshing(isRefreshing: Boolean) {
        _isRefreshing.value = isRefreshing
    }

//    suspend fun fetchPopularMovies(pages: Int) {
//        repository.fetchPopularMovieDetails(pages) { fetchedMovies ->
//            if (fetchedMovies != null) {
//                _popularMovies.value = fetchedMovies
//            }
//        }
//    }
//
//    suspend fun fetchTopRatedMovies(pages: Int) {
//        repository.fetchTopRatedMovieDetails(pages) { fetchedMovies ->
//            if (fetchedMovies != null) {
//                _topRatedMovies.value = fetchedMovies
//            }
//        }
//    }

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

    fun getReviews(movieId: Int) :  Flow<PagingData<Reviews>> {
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