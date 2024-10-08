package com.drew.themoviedatabase.screens.Home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.drew.themoviedatabase.data.repository.Movies.MovieRepository
import com.drew.themoviedatabase.data.repository.Movies.NowPlayingMoviesPagingSource
import com.drew.themoviedatabase.data.repository.Movies.PopularMoviesPagingSource
import com.drew.themoviedatabase.data.repository.Movies.TopRatedMoviesPagingSource
import com.drew.themoviedatabase.data.repository.Movies.TrendingMoviesPagingSource
import com.drew.themoviedatabase.data.repository.Movies.UpcomingMoviesPagingSource
import com.drew.themoviedatabase.data.repository.TrendingMediaPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeMoviesViewModel @Inject constructor(
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

    private val _isRefreshing = MutableLiveData<Boolean>(false)
    val isRefreshing: LiveData<Boolean> get() = _isRefreshing

    fun setRefreshing(isRefreshing: Boolean) {
        _isRefreshing.value = isRefreshing
    }
}