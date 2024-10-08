package com.drew.themoviedatabase.screens.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.drew.themoviedatabase.data.repository.TVShows.AiringTodayPagingSource
import com.drew.themoviedatabase.data.repository.TVShows.OnTheAirShowsPagingSource
import com.drew.themoviedatabase.data.repository.TVShows.PopularShowsPagingSource
import com.drew.themoviedatabase.data.repository.TVShows.TVShowsRepository
import com.drew.themoviedatabase.data.repository.TVShows.TopShowsPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeTVViewModel @Inject constructor(
    private val repository: TVShowsRepository,
) : ViewModel() {
    val tvShowsPopular = Pager(
        config = PagingConfig(pageSize = 10),
        pagingSourceFactory = { PopularShowsPagingSource(repository) }
    ).flow.cachedIn(viewModelScope)

    val tvShowsTopRated = Pager(
        config = PagingConfig(pageSize = 10),
        pagingSourceFactory = { TopShowsPagingSource(repository) }
    ).flow.cachedIn(viewModelScope)

    val tvShowsOnTheAir = Pager(
        config = PagingConfig(pageSize = 10),
        pagingSourceFactory = { OnTheAirShowsPagingSource(repository) }
    ).flow.cachedIn(viewModelScope)

    val tvShowsAiringToday = Pager(
        config = PagingConfig(pageSize = 10),
        pagingSourceFactory = { AiringTodayPagingSource(repository) }
    ).flow.cachedIn(viewModelScope)
}