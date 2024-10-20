package com.drew.themoviedatabase.screens.Genre

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.drew.themoviedatabase.data.model.TVShowDetails
import com.drew.themoviedatabase.data.repository.Movies.MovieDetailsReleaseData
import com.drew.themoviedatabase.data.repository.Movies.MovieRepository
import com.drew.themoviedatabase.data.repository.Movies.MoviesByGenrePagingSource
import com.drew.themoviedatabase.data.repository.TVShows.TVShowsByGenrePagingSource
import com.drew.themoviedatabase.data.repository.TVShows.TVShowsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
data class GenreViewModel @Inject constructor(
    val movieRepository: MovieRepository,
    val tvShowsRepository: TVShowsRepository
) : ViewModel() {

    fun getMoviesByGenre(genreId: Int): Flow<PagingData<MovieDetailsReleaseData>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { MoviesByGenrePagingSource(movieRepository = movieRepository, genreId = genreId) }
        ).flow.cachedIn(viewModelScope)
    }

    fun getTVShowsByGenre(genreId: Int): Flow<PagingData<TVShowDetails>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { TVShowsByGenrePagingSource(tvShowsRepository = tvShowsRepository, genreId = genreId) }
        ).flow.cachedIn(viewModelScope)
    }

}
