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
import com.drew.themoviedatabase.data.remote.MovieImagesResponse
import com.drew.themoviedatabase.data.remote.TVShowDetailsWithCastAndVideos
import com.drew.themoviedatabase.data.repository.TVShows.SimilarTVShowsPagingSource
import com.drew.themoviedatabase.data.repository.TVShows.TVReviewsPagingSource
import com.drew.themoviedatabase.data.repository.TVShows.TVShowPhotosPagingSource
import com.drew.themoviedatabase.data.repository.TVShows.TVShowsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class TVShowsViewModel @Inject constructor(
    private val repository: TVShowsRepository,
) : ViewModel() {

    private val _onTheAirTVShows = MutableLiveData<List<com.drew.themoviedatabase.data.model.TVShowDetails?>?>()
    val onTheAirTVShows: LiveData<List<com.drew.themoviedatabase.data.model.TVShowDetails?>?> get()  = _onTheAirTVShows

    private val _popularTVShows = MutableLiveData<List<com.drew.themoviedatabase.data.model.TVShowDetails?>?>()
    val popularTVShows: LiveData<List<com.drew.themoviedatabase.data.model.TVShowDetails?>?> get()  = _popularTVShows

    private val _topRatedTVShows = MutableLiveData<List<com.drew.themoviedatabase.data.model.TVShowDetails?>?>()
    val topRatedTVShows: LiveData<List<com.drew.themoviedatabase.data.model.TVShowDetails?>?> get()  = _topRatedTVShows

    private val _airingTodayTVShows = MutableLiveData<List<com.drew.themoviedatabase.data.model.TVShowDetails?>?>()
    val airingTodayTVShows: LiveData<List<com.drew.themoviedatabase.data.model.TVShowDetails?>?> get()  = _airingTodayTVShows

    private val _similarTVShows = MutableLiveData<List<com.drew.themoviedatabase.data.model.TVShowDetails?>?>()
    val similarTVShows: LiveData<List<com.drew.themoviedatabase.data.model.TVShowDetails?>?> get()  = _similarTVShows

    private val _reviews = MutableLiveData<List<com.drew.themoviedatabase.data.model.Reviews?>?>()
    val reviews: LiveData<List<com.drew.themoviedatabase.data.model.Reviews?>?> get()  = _reviews

    private val _trailers = MutableLiveData<List<Trailers?>>()
    val trailers: LiveData<List<Trailers?>> get() = _trailers

    private val _tvShowsWithCastAndVideos = MutableLiveData<TVShowDetailsWithCastAndVideos?>()
    val tvShowsWithCastAndVideos: LiveData<TVShowDetailsWithCastAndVideos?> get()  = _tvShowsWithCastAndVideos

    private val _tvShowImages = MutableLiveData<MovieImagesResponse?>()
    val tvShowImages: LiveData<MovieImagesResponse?> get() = _tvShowImages

    private val _certifications = MutableLiveData<com.drew.themoviedatabase.data.model.Certifications?>()
    val certifications: LiveData<com.drew.themoviedatabase.data.model.Certifications?> get()  = _certifications

    fun getSimilarTVShows(seriesId: Int) : Flow<PagingData<com.drew.themoviedatabase.data.model.TVShowDetails>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { SimilarTVShowsPagingSource(repository, seriesId) }
        ).flow.cachedIn(viewModelScope)
    }

    fun fetchPopularTVShows(pages: Int) {
        repository.fetchPopularTVShowsDetails(pages) { fetchedMovies ->
            if (fetchedMovies != null) {
                _popularTVShows.value = fetchedMovies
            }
        }
    }

    fun fetchTopRatedTVShows(pages: Int) {
        repository.fetchTopTVShowDetails(pages) { fetchedMovies ->
            if (fetchedMovies != null) {
                _topRatedTVShows.value = fetchedMovies
            }
        }
    }

    fun fetchOnTheAirTVShows(pages: Int) {
        repository.fetchOnTheAirTVShows(pages) { fetchedMovies ->
            if (fetchedMovies != null) {
                _onTheAirTVShows.value = fetchedMovies
            }
        }
    }


    fun fetchAiringTodayTVShows(pages: Int) {
        repository.fetchAiringTodayTVShows(pages) { fetchedMovies ->
            if (fetchedMovies != null) {
                _airingTodayTVShows.value = fetchedMovies
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


    fun fetchTrailer(seriesId: Int) {
        repository.fetchTrailers(seriesId) { trailersResponse ->
            if (trailersResponse.isSuccessful) {
                _trailers.value = trailersResponse.body()?.getResults()
            }
        }
    }


    fun fetchTVDetailsWithCastAndVideos(seriesId: Int) {
        repository.fetchTVShowsDetailsWithCastAndVideos(seriesId) { tvDetailsResponse ->
            if (tvDetailsResponse.isSuccessful) {
                _tvShowsWithCastAndVideos.value = tvDetailsResponse.body()
            }
        }
    }

    fun fetchSimilarTVShows(seriesId: Int, pages: Int) {
        repository.fetchSimilarTVShows(seriesId, pages) { fetchedMovies ->
            if (fetchedMovies != null) {
                _similarTVShows.value = fetchedMovies
            }
        }
    }

    fun fetchReviews(seriesId: Int) {
        repository.getTVShowReviews(seriesId) { reviewsResponse ->
            if (reviewsResponse.isSuccessful) {
                _reviews.value = reviewsResponse.body()?.getResults()
            }
        }
    }

    fun getPhotos(seriesId: Int) : Flow<PagingData<com.drew.themoviedatabase.data.model.Photos>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { TVShowPhotosPagingSource(repository, seriesId) }
        ).flow.cachedIn(viewModelScope)
    }

    fun getReviews(seriesId: Int): Flow<PagingData<com.drew.themoviedatabase.data.model.Reviews>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { TVReviewsPagingSource(repository, seriesId) }
        ).flow.cachedIn(viewModelScope)
    }

    suspend fun getTotalPagesPopular() : Int  {
        return repository.getTotalPagesPopular()
    }

    suspend fun getTotalPagesTopRated() : Int  {
        return repository.getTotalPagesTopRated()
    }

    suspend fun getTotalPagesOnTheAir() : Int  {
        return repository.getTotalPagesOnTheAir()
    }

    suspend fun getTotalPagesAiringToday() : Int  {
        return repository.getTotalPagesAiringToday()
    }
}