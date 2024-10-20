package com.drew.themoviedatabase.data.repository.TVShows

import androidx.paging.PagingSource
import androidx.paging.PagingState
import java.io.IOException

data class TVShowsByGenrePagingSource(
    private val tvShowsRepository: TVShowsRepository,
    val genreId: Int
) : PagingSource<Int, com.drew.themoviedatabase.data.model.TVShowDetails>() {

    override fun getRefreshKey(state: PagingState<Int, com.drew.themoviedatabase.data.model.TVShowDetails>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, com.drew.themoviedatabase.data.model.TVShowDetails> {
        return try {
            // Key may be null during a refresh, if no explicit key is passed into Pager
            // construction. Use 1 as default, because our API is indexed started at index 1
            val pageNumber = params.key ?: 1

            val shows = tvShowsRepository.fetchTVShowByGenre(pageNumber, genreId)
            val data = shows?.filterNotNull() ?: emptyList()

            // Since 1 is the lowest page number, return null to signify no more pages should
            // be loaded before it.
            val prevKey = if(pageNumber  > 1) pageNumber - 1 else null
            val nextKey = if (data.isNotEmpty()) pageNumber + 1 else null

            if (shows != null) {
                LoadResult.Page(
                    data = data,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            } else {
                LoadResult.Error(throw Exception("Failed to load data"))
            }
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}