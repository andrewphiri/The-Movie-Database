package com.drew.themoviedatabase.screens.Home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drew.themoviedatabase.Network.TVShowDetailsWithCastAndVideos
import com.drew.themoviedatabase.POJO.MovieDetailsReleaseData
import com.drew.themoviedatabase.POJO.Reviews
import com.drew.themoviedatabase.POJO.TVShowDetails
import com.drew.themoviedatabase.POJO.Trailers
import com.drew.themoviedatabase.data.TVShowsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TVShowsViewModel @Inject constructor(
    private val repository: TVShowsRepository
) : ViewModel() {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            async { fetchPopularTVShows(3) }.await()
            async { fetchTopRatedTVShows(3) }.await()
            async { fetchOnTheAirTVShows(3) }.await()
            async { fetchAiringTodayTVShows(3) }.await()
        }
    }

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

    private val _tvShowsWithCastAndVideos = MutableLiveData<TVShowDetailsWithCastAndVideos?>()
    val tvShowsWithCastAndVideos: LiveData<TVShowDetailsWithCastAndVideos?> get()  = _tvShowsWithCastAndVideos

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