package com.drew.themoviedatabase.screens.Profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.drew.themoviedatabase.data.model.TVShowDetails
import com.drew.themoviedatabase.data.remote.AddedToListResponse
import com.drew.themoviedatabase.data.repository.Movies.MovieDetailsReleaseData
import com.drew.themoviedatabase.data.repository.mymoviestv.FavoriteMoviesPagingSource
import com.drew.themoviedatabase.data.repository.mymoviestv.FavoriteTvPagingSource
import com.drew.themoviedatabase.data.repository.mymoviestv.MoviesWatchlistPagingSource
import com.drew.themoviedatabase.data.repository.mymoviestv.MyMoviesTvRepository
import com.drew.themoviedatabase.data.repository.mymoviestv.RatedMoviesPagingSource
import com.drew.themoviedatabase.data.repository.mymoviestv.RatedTvPagingSource
import com.drew.themoviedatabase.data.repository.mymoviestv.TvWatchlistPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MyMoviesTVsViewModel @Inject constructor(
    private val myMoviesTvRepository: MyMoviesTvRepository
) : ViewModel() {

    private val _favMovies = MutableLiveData<List<MovieDetailsReleaseData?>?>()
    val favMovies: LiveData<List<MovieDetailsReleaseData?>?> get()  = _favMovies

    private val _favTVShows = MutableLiveData<List<TVShowDetails?>?>()
    val favTVShows: LiveData<List<TVShowDetails?>?> get()  = _favTVShows

    private val _ratedMovies = MutableLiveData<List<MovieDetailsReleaseData?>?>()
    val ratedMovies: LiveData<List<MovieDetailsReleaseData?>?> get()  = _ratedMovies

    private val _ratedTVShows = MutableLiveData<List<TVShowDetails?>?>()
    val ratedTVShows: LiveData<List<TVShowDetails?>?> get()  = _ratedTVShows

    private val _watchlistMovies = MutableLiveData<List<MovieDetailsReleaseData?>?>()
    val watchlistMovies: LiveData<List<MovieDetailsReleaseData?>?> get()  = _watchlistMovies

    private val _watchlistTVShows = MutableLiveData<List<TVShowDetails?>?>()
    val watchlistTVShows: LiveData<List<TVShowDetails?>?> get()  = _watchlistTVShows

    private val _recommendedMovies = MutableLiveData<List<MovieDetailsReleaseData?>?>()
    val recommendedMovies: LiveData<List<MovieDetailsReleaseData?>?> get()  = _recommendedMovies

    fun getMyRatedMovies(accountId: Int, sessionId: String?) : Flow<PagingData<MovieDetailsReleaseData>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { RatedMoviesPagingSource(myMoviesTvRepository, accountId, sessionId) }
        ).flow.cachedIn(viewModelScope)
    }
    fun getMyRatedTVShows(accountId: Int, sessionId: String?) : Flow<PagingData<com.drew.themoviedatabase.data.model.TVShowDetails>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { RatedTvPagingSource(myMoviesTvRepository, accountId, sessionId) }
        ).flow.cachedIn(viewModelScope)
    }

    fun getMyFavoriteMovies(accountId: Int, sessionId: String?) : Flow<PagingData<MovieDetailsReleaseData>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { FavoriteMoviesPagingSource(myMoviesTvRepository, accountId, sessionId) }
        ).flow.cachedIn(viewModelScope)
    }

    fun getMyFavoriteTVShows(accountId: Int, sessionId: String?) : Flow<PagingData<com.drew.themoviedatabase.data.model.TVShowDetails>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { FavoriteTvPagingSource(myMoviesTvRepository, accountId, sessionId) }
        ).flow.cachedIn(viewModelScope)
    }

    fun getMyWatchlistMovies(accountId: Int, sessionId: String?) : Flow<PagingData<MovieDetailsReleaseData>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { MoviesWatchlistPagingSource(myMoviesTvRepository, accountId, sessionId) }
        ).flow.cachedIn(viewModelScope)
    }

    fun getMyWatchlistTVShows(accountId: Int, sessionId: String?) : Flow<PagingData<com.drew.themoviedatabase.data.model.TVShowDetails>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { TvWatchlistPagingSource(myMoviesTvRepository, accountId, sessionId) }
        ).flow.cachedIn(viewModelScope)
    }

    suspend fun addToFavoriteOrWatchlist(
        mediaType: String,
        mediaID: Int,
        listType: String,
        addToList: Boolean,
        accountId: Int,
        sessionId: String?
    ): AddedToListResponse? {
        return withContext(Dispatchers.IO) { // Use Dispatchers.IO for network or database operations
            myMoviesTvRepository.addToFavoriteOrWatchlist(
                mediaType = mediaType,
                mediaID = mediaID,
                listType = listType,
                accountId = accountId,
                sessionId = sessionId,
                addToList = addToList
            )
        }
    }

    //Fetch movies/tv shows without pager
    fun fetchMyFavoriteMovies(accountId: Int, sessionId: String?) {
       viewModelScope.launch {
           val pageToFetch = async {  myMoviesTvRepository.getTotalPagesFavoriteMovies(accountId, sessionId) }.await()
          // Log.d("MyMoviesTVsViewModel", "fetchMyFavoriteMovies: $pageToFetch")
           myMoviesTvRepository.fetchFavoriteMovies(pages = pageToFetch , accountId = accountId, sessionId = sessionId) { fetchedMovies ->
               if (fetchedMovies != null) {
                   _favMovies.value = fetchedMovies
               }
           }
       }
    }

     fun fetchMyFavoriteShows(accountId: Int, sessionId: String?) {
       viewModelScope.launch {
           val pageToFetch = async { myMoviesTvRepository.getTotalPagesFavoriteTVShows(accountId, sessionId) }.await()
           myMoviesTvRepository.fetchFavoriteTVShows(pages = pageToFetch , accountId = accountId, sessionId = sessionId) { fetchedMovies ->
               if (fetchedMovies != null) {
                   _favTVShows.value = fetchedMovies
               }
           }
       }
    }

     fun fetchMyRatedMovies(accountId: Int, sessionId: String?) {
       viewModelScope.launch {
           val pageToFetch = async { myMoviesTvRepository.getTotalPagesRatedMovies(accountId, sessionId) }.await()
           myMoviesTvRepository.fetchRatedMovies(pages = pageToFetch , accountId = accountId, sessionId = sessionId) { fetchedMovies ->
               if (fetchedMovies != null) {
                   _ratedMovies.value = fetchedMovies
               }
           }
       }
    }

     fun fetchMyRatedTVShows(accountId: Int, sessionId: String?) {
        viewModelScope.launch {
            val pageToFetch = async { myMoviesTvRepository.getTotalPagesRatedTVShows(accountId, sessionId)}.await()
            myMoviesTvRepository.fetchRatedTVShows(pages = pageToFetch , accountId = accountId, sessionId = sessionId) { fetchedMovies ->
                if (fetchedMovies != null) {
                    _ratedTVShows.value = fetchedMovies
                }
            }
        }
    }

     fun fetchMyWatchlistMovies(accountId: Int, sessionId: String?) {
       viewModelScope.launch {
           val pageToFetch = async { myMoviesTvRepository.getTotalPagesWatchlistMovies(accountId, sessionId) }.await()
           myMoviesTvRepository.fetchWatchlistMovies(pages = pageToFetch , accountId = accountId, sessionId = sessionId) { fetchedMovies ->
               if (fetchedMovies != null) {
                   _watchlistMovies.value = fetchedMovies
               }
           }
       }
    }

    fun fetchMyWatchlistTVShows(accountId: Int, sessionId: String?) {
       viewModelScope.launch {
           val pageToFetch = async { myMoviesTvRepository.getTotalPagesWatchlistTVShows(accountId, sessionId) }.await()
           myMoviesTvRepository.fetchWatchlistTVShows(pages = pageToFetch , accountId = accountId, sessionId = sessionId) { fetchedMovies ->
               if (fetchedMovies != null) {
                   _watchlistTVShows.value = fetchedMovies
               }
           }
       }
    }

    suspend fun fetchRatedTVShows(pages: Int, accountId: Int, sessionId: String?) : List<com.drew.themoviedatabase.data.model.TVShowDetails?>? {
        return myMoviesTvRepository.fetchRatedTVShows(pages, accountId, sessionId)
    }
    suspend fun fetchFavoriteMovieDetails(pages: Int, accountId: Int, sessionId: String?): List<MovieDetailsReleaseData?>? {
        return myMoviesTvRepository.fetchFavoriteMovieDetails(pages, accountId, sessionId)
    }
}