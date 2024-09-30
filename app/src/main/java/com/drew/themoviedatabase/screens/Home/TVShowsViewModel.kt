package com.drew.themoviedatabase.screens.Home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.drew.themoviedatabase.Network.MovieImagesResponse
import com.drew.themoviedatabase.Network.TVShowDetailsWithCastAndVideos
import com.drew.themoviedatabase.POJO.Certifications
import com.drew.themoviedatabase.POJO.Photos
import com.drew.themoviedatabase.POJO.Reviews
import com.drew.themoviedatabase.POJO.TVShowDetails
import com.drew.themoviedatabase.POJO.Trailers
import com.drew.themoviedatabase.repository.TVShows.TVShowsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class TVShowsViewModel @Inject constructor(
    private val repository: TVShowsRepository,
) : ViewModel() {

    val tvShowsPopular = repository.getPopularTVShows().cachedIn(viewModelScope)
    val tvShowsTopRated = repository.getTopRatedTVShows().cachedIn(viewModelScope)
    val tvShowsOnTheAir = repository.getOnTheAirTVShows().cachedIn(viewModelScope)
    val tvShowsAiringToday = repository.getAiringTodayTVShows().cachedIn(viewModelScope)

    private val _onTheAirTVShows = MutableLiveData<List<TVShowDetails?>?>()
    val onTheAirTVShows: LiveData<List<TVShowDetails?>?> get()  = _onTheAirTVShows

    private val _popularTVShows = MutableLiveData<List<TVShowDetails?>?>()
    val popularTVShows: LiveData<List<TVShowDetails?>?> get()  = _popularTVShows

    private val _topRatedTVShows = MutableLiveData<List<TVShowDetails?>?>()
    val topRatedTVShows: LiveData<List<TVShowDetails?>?> get()  = _topRatedTVShows

    private val _airingTodayTVShows = MutableLiveData<List<TVShowDetails?>?>()
    val airingTodayTVShows: LiveData<List<TVShowDetails?>?> get()  = _airingTodayTVShows

    private val _similarTVShows = MutableLiveData<List<TVShowDetails?>?>()
    val similarTVShows: LiveData<List<TVShowDetails?>?> get()  = _similarTVShows

    private val _reviews = MutableLiveData<List<Reviews?>?>()
    val reviews: LiveData<List<Reviews?>?> get()  = _reviews

    private val _trailers = MutableLiveData<List<Trailers?>>()
    val trailers: LiveData<List<Trailers?>> get() = _trailers

    private val _tvShowsWithCastAndVideos = MutableLiveData<TVShowDetailsWithCastAndVideos?>()
    val tvShowsWithCastAndVideos: LiveData<TVShowDetailsWithCastAndVideos?> get()  = _tvShowsWithCastAndVideos

    private val _tvShowImages = MutableLiveData<MovieImagesResponse?>()
    val tvShowImages: LiveData<MovieImagesResponse?> get() = _tvShowImages

    private val _certifications = MutableLiveData<Certifications?>()
    val certifications: LiveData<Certifications?> get()  = _certifications

    fun getSimilarTVShows(seriesId: Int) : Flow<PagingData<TVShowDetails>> {
        return repository.getSimilarTVShows(seriesId).cachedIn(viewModelScope)
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

    fun getPhotos(seriesId: Int) : Flow<PagingData<Photos>> {
        return repository.getShowImages(seriesId).cachedIn(viewModelScope)
    }

    fun getReviews(movieId: Int): Flow<PagingData<Reviews>> {
        return repository.getReviews(movieId).cachedIn(viewModelScope)
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