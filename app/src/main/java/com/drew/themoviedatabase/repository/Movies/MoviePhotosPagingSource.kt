package com.drew.themoviedatabase.repository.Movies

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.drew.themoviedatabase.POJO.Photos
import java.io.IOException
import javax.inject.Inject

class MoviePhotosPagingSource @Inject constructor(
    private val repository: MovieRepository,
    val movieId: Int
) : PagingSource<Int, Photos>() {
    override fun getRefreshKey(state: PagingState<Int, Photos>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photos> {
        return try {
            // Key may be null during a refresh, if no explicit key is passed into Pager
            // construction. Use 1 as default, because our API is indexed started at index 1
            val pageNumber = params.key ?: 1

            val photos = repository.getMoviePhotos(movieId = movieId)
            val data = photos?.filterNotNull() ?: emptyList()

            // Since 1 is the lowest page number, return null to signify no more pages should
            // be loaded before it.
            val prevKey = if(pageNumber  > 1) pageNumber - 1 else null
            val nextKey = if (data.isNotEmpty()) pageNumber + 1 else null

            LoadResult.Page(
                data = data,
                prevKey = null,
                nextKey = null
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}