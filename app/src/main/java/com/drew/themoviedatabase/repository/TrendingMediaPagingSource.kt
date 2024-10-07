package com.drew.themoviedatabase.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.drew.themoviedatabase.Network.MultiSearchResult
import com.drew.themoviedatabase.repository.Movies.MovieRepository
import java.io.IOException
import javax.inject.Inject

data class TrendingMediaPagingSource @Inject constructor(
    private val movieRepository: MovieRepository,
    private val query: String
) : PagingSource<Int, MultiSearchResult>() {
    override fun getRefreshKey(state: PagingState<Int, MultiSearchResult>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MultiSearchResult> {
        return try {
            // Key may be null during a refresh, if no explicit key is passed into Pager
            // construction. Use 1 as default, because our API is indexed started at index 1
            val pageNumber = params.key ?: 1

            val searchResult = movieRepository.multiSearchTrendingMedia(pageNumber)
            val data = searchResult?.filterNotNull() ?: emptyList()

            // Since 1 is the lowest page number, return null to signify no more pages should
            // be loaded before it.
            val prevKey = if(pageNumber  > 1) pageNumber - 1 else null
            val nextKey = if (data.isNotEmpty()) pageNumber + 1 else null

            LoadResult.Page(
                data = data,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}

