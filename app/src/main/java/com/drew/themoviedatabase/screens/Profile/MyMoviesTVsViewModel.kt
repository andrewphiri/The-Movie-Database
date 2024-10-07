package com.drew.themoviedatabase.screens.Profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.drew.themoviedatabase.POJO.AddedToListResponse
import com.drew.themoviedatabase.POJO.MovieDetailsReleaseData
import com.drew.themoviedatabase.POJO.TVShowDetails
import com.drew.themoviedatabase.repository.mymoviestv.FavoriteMoviesPagingSource
import com.drew.themoviedatabase.repository.mymoviestv.FavoriteTvPagingSource
import com.drew.themoviedatabase.repository.mymoviestv.MoviesWatchlistPagingSource
import com.drew.themoviedatabase.repository.mymoviestv.MyMoviesTvRepository
import com.drew.themoviedatabase.repository.mymoviestv.RatedMoviesPagingSource
import com.drew.themoviedatabase.repository.mymoviestv.RatedTvPagingSource
import com.drew.themoviedatabase.repository.mymoviestv.TvWatchlistPagingSource
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MyMoviesTVsViewModel @Inject constructor(
    private val myMoviesTvRepository: MyMoviesTvRepository
) : ViewModel() {


    fun getMyRatedMovies(accountId: Int, sessionId: String?) : Flow<PagingData<MovieDetailsReleaseData>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { RatedMoviesPagingSource(myMoviesTvRepository, accountId, sessionId) }
        ).flow.cachedIn(viewModelScope)
    }
    fun getMyRatedTVShows(accountId: Int, sessionId: String?) : Flow<PagingData<TVShowDetails>> {
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

    fun getMyFavoriteTVShows(accountId: Int, sessionId: String?) : Flow<PagingData<TVShowDetails>> {
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

    fun getMyWatchlistTVShows(accountId: Int, sessionId: String?) : Flow<PagingData<TVShowDetails>> {
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
    suspend fun fetchRatedMovieDetails(pages: Int, accountId: Int, sessionId: String?): List<MovieDetailsReleaseData?>? {
        return myMoviesTvRepository.fetchRatedMovieDetails(pages, accountId, sessionId)
    }

    suspend fun fetchRatedTVShows(pages: Int, accountId: Int, sessionId: String?) : List<TVShowDetails?>? {
        return myMoviesTvRepository.fetchRatedTVShows(pages, accountId, sessionId)
    }
    suspend fun fetchFavoriteMovieDetails(pages: Int, accountId: Int, sessionId: String?): List<MovieDetailsReleaseData?>? {
        return myMoviesTvRepository.fetchFavoriteMovieDetails(pages, accountId, sessionId)
    }
}