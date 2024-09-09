package com.drew.themoviedatabase.repository.TVShows

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.drew.themoviedatabase.Network.API_KEY
import com.drew.themoviedatabase.Network.MovieImagesResponse
import com.drew.themoviedatabase.Network.ReviewsResponse
import com.drew.themoviedatabase.Network.TVShowApiService
import com.drew.themoviedatabase.Network.TVShowDetailsWithCastAndVideos
import com.drew.themoviedatabase.Network.TVShowsResponse
import com.drew.themoviedatabase.Network.TotalPages
import com.drew.themoviedatabase.Network.TrailersResponse
import com.drew.themoviedatabase.POJO.TVShow
import com.drew.themoviedatabase.POJO.TVShowDetails
import com.drew.themoviedatabase.Utilities.defaultLocale
import com.drew.themoviedatabase.Utilities.imageLanguage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class TVShowsRepository@Inject constructor(
    val tvShowApiService: TVShowApiService) {

    fun getTopRatedTVShows() : Flow<PagingData<TVShowDetails>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { TopShowsPagingSource(this) }
        ).flow
    }

    fun getPopularTVShows() : Flow<PagingData<TVShowDetails>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { PopularShowsPagingSource(this) }
        ).flow
    }

    fun getOnTheAirTVShows() : Flow<PagingData<TVShowDetails>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { OnTheAirShowsPagingSource(this) }
        ).flow
    }

    fun getAiringTodayTVShows() : Flow<PagingData<TVShowDetails>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { AiringTodayPagingSource(this) }
        ).flow
    }


    private fun fetchTVShows(
        pages: Int,
        apiCall: suspend (Int) -> Response<TVShowsResponse?>?,
        callback: (List<TVShow>?) -> Unit
    ) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val allTVShows = mutableListOf<TVShow>()
                val jobs = (1..pages).map { page ->
                    async {
                        try {
                            val response = apiCall(page)
                            if (response?.isSuccessful == true) {
                                response.body()?.let { tvResponse ->
                                    tvResponse.getTVShows()?.let { allTVShows.addAll(it) }
                                }
                            }
                        } catch (e : Exception) {
                            e.printStackTrace()
                        }

                    }
                }
                // Await all jobs to complete
                jobs.awaitAll()
                // Switch to the main thread to invoke the callback
                withContext(Dispatchers.Main) {
                    callback(allTVShows)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            callback(null)
        }
    }

    private suspend fun fetchTVShows(
        pages: Int,
        apiCall: suspend (Int) -> Response<TVShowsResponse?>?,

        ) : List<TVShow?>? {
        return try {
            coroutineScope {
                withContext(Dispatchers.IO) {
                    try {
                        val response = apiCall(pages)
                        if (response?.isSuccessful == true) {
                            response.body()?.getTVShows()
                        } else {
                            null
                        }
                    } catch (e : Exception) {
                        e.printStackTrace()
                        null
                    }
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun fetchTVShowDetails(
        pages: Int,
        apiCall: suspend (Int) -> Response<TVShowsResponse?>?
    ) : List<TVShowDetails?>? {
        return try {
            val shows = fetchTVShows(pages = pages, apiCall = apiCall)
            if (shows != null) {
                coroutineScope {
                    val tvshow = shows.map { movie ->
                        async(Dispatchers.IO) {
                            try {
                                val response = tvShowApiService.getTVShowDetailsWithContentRatings(
                                    movie?.id, apiKey = API_KEY, language = defaultLocale())?.execute()

                                if (response?.isSuccessful == true) {
                                    response.body()
                                } else {
                                    null
                                }
                            } catch (e:Exception) {
                                e.printStackTrace()
                                null
                            }
                        }
                    }
                    tvshow.awaitAll()
                }
            } else {
                null
            }

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun fetchTVShowDetails(
        pages: Int,
        apiCall: suspend (Int) -> Response<TVShowsResponse?>?,
        callback: (List<TVShowDetails?>?) -> Unit,
    ) {
        try {
            fetchTVShows(pages, apiCall) { tvShows ->
                if (tvShows != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val detailedTVShows = mutableListOf<TVShowDetails?>()
                        val jobs = tvShows.map { tvShow ->
                            async {
                                try {
                                    val response = tvShowApiService.getTVShowDetailsWithContentRatings(tvShow.id, apiKey = API_KEY, language = defaultLocale())?.execute()

                                    if (response?.isSuccessful == true) {
                                        response.body()
                                    } else {
                                        null
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    null
                                }

                            }
                        }

                        detailedTVShows.addAll(jobs.awaitAll())
                        withContext(Dispatchers.Main) {
                            callback(detailedTVShows)
                        }
                    }
                } else {
                    callback(null)
                }
            }
        } catch (e: Exception) {
            callback(null)
            e.printStackTrace()
        }
    }

    fun fetchPopularTVShowsDetails(pages: Int, callback: (List<TVShowDetails?>?) -> Unit) {
        try {
            fetchTVShowDetails(
                pages = pages,
                apiCall = { page -> tvShowApiService.getPopularTVShows(apiKey = API_KEY, language = defaultLocale(), page = page)?.execute() },
                callback = callback,
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun fetchPopularTVShowsDetails(pages: Int) : List<TVShowDetails?>? {
       return try {
            fetchTVShowDetails(
                pages = pages,
                apiCall = { page -> tvShowApiService.getPopularTVShows(apiKey = API_KEY, language = defaultLocale(), page = page)?.execute() },
            )
        } catch (e: Exception) {
            e.printStackTrace()
           null
        }
    }

    fun fetchTopTVShowDetails(pages: Int, callback: (List<TVShowDetails?>?) -> Unit) {
        try {
            fetchTVShowDetails(
                pages = pages,
                apiCall = { page -> tvShowApiService.getTopRatedTVShows(apiKey = API_KEY, language = defaultLocale(), page =  page)?.execute() },
                callback = callback
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun fetchTopTVShowDetails(pages: Int) : List<TVShowDetails?>? {
       return try {
            fetchTVShowDetails(
                pages = pages,
                apiCall = { page -> tvShowApiService.getTopRatedTVShows(apiKey = API_KEY, language = defaultLocale(), page =  page)?.execute() },
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun fetchOnTheAirTVShows(pages: Int, callback: (List<TVShowDetails?>?) -> Unit) {
        try {
            fetchTVShowDetails(
                pages = pages,
                apiCall = { page -> tvShowApiService.getTVShowsOnTheAir(apiKey = API_KEY, language = defaultLocale(), page =  page)?.execute() },
                callback = callback,
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    suspend fun fetchOnTheAirTVShows(pages: Int) : List<TVShowDetails?>? {
        return try {
            fetchTVShowDetails(
                pages = pages,
                apiCall = { page -> tvShowApiService.getTVShowsOnTheAir(apiKey = API_KEY, language = defaultLocale(), page =  page)?.execute() },
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun fetchAiringTodayTVShows(pages: Int, callback: (List<TVShowDetails?>?) -> Unit) {
        try {
            fetchTVShowDetails(
                pages = pages,
                apiCall = { page -> tvShowApiService.getTVShowsAiringToday(apiKey = API_KEY, language = defaultLocale(), page =  page)?.execute() },
                callback = callback,
            )
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    suspend fun fetchAiringTodayTVShows(pages: Int) : List<TVShowDetails?>? {
       return try {
            fetchTVShowDetails(
                pages = pages,
                apiCall = { page -> tvShowApiService.getTVShowsAiringToday(apiKey = API_KEY, language = defaultLocale(), page =  page)?.execute() },
            )
        } catch (e : Exception) {
            e.printStackTrace()
           null
        }
    }

    fun fetchSimilarTVShows(seriesId: Int, pages: Int, callback: (List<TVShowDetails?>?) -> Unit) {
        try {
            fetchTVShowDetails(
                pages = pages,
                apiCall = { page -> tvShowApiService.getSimilarTVShows(seriesId = seriesId, apiKey = API_KEY, language = defaultLocale(), page =  page)?.execute() },
                callback = callback)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun fetchSimilarTVShows(seriesId: Int, pages: Int) : List<TVShowDetails?>?{
        return try {
            fetchTVShowDetails(
                pages = pages,
                apiCall = { page -> tvShowApiService.getSimilarTVShows(seriesId = seriesId, apiKey = API_KEY, language = defaultLocale(), page =  page)?.execute() },)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getTVShowReviews(seriesId: Int, callback: (Response<ReviewsResponse?>) -> Unit) {
            try {
                tvShowApiService.getTVShowReviews(seriesId, apiKey = API_KEY, language = defaultLocale())?.enqueue(object : Callback<ReviewsResponse?> {
                    override fun onResponse(
                        p0: Call<ReviewsResponse?>,
                        p1: Response<ReviewsResponse?>
                    ) {
                        if (p1.isSuccessful) {
                            callback(p1)
                        }
                    }

                    override fun onFailure(p0: Call<ReviewsResponse?>, p1: Throwable) {
                        callback(Response.success(null))
                    }
                })

            } catch (e: Exception) {
                e.printStackTrace()
                callback(Response.success(null))
            }
    }

    fun getTvShowPhotos(movieId: Int, callback: (Response<MovieImagesResponse?>) -> Unit) {
        try {
            tvShowApiService.getTVShowImages(
                movieId = movieId,
                apiKey = API_KEY,
                imageLanguage = imageLanguage,
                language = defaultLocale())?.enqueue(object : Callback<MovieImagesResponse?> {
                override fun onResponse(
                    p0: Call<MovieImagesResponse?>,
                    p1: Response<MovieImagesResponse?>
                ) {
                    if (p1.isSuccessful) {
                        callback(p1)
                    }
                }

                override fun onFailure(p0: Call<MovieImagesResponse?>, p1: Throwable) {
                    callback(Response.success(null))
                }
            })
        } catch (e:Exception) {
            e.printStackTrace()
            callback(Response.success(null))
        }
    }


    fun fetchTrailers(seriesId: Int, callback: (Response<TrailersResponse?>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                tvShowApiService.getTVShowTrailer(seriesId, apiKey = API_KEY, language = defaultLocale())?.enqueue(object :
                    Callback<TrailersResponse?> {
                    override fun onResponse(
                        p0: Call<TrailersResponse?>,
                        p1: Response<TrailersResponse?>
                    ) {
                        if (p1.isSuccessful){
                            callback(p1)
                        }
                    }

                    override fun onFailure(p0: Call<TrailersResponse?>, p1: Throwable) {
                        callback(Response.success(null))
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
                callback(Response.success(null))
            }
        }
    }


    fun fetchTVShowsDetailsWithCastAndVideos(seriesId: Int, callback: (Response<TVShowDetailsWithCastAndVideos?>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                tvShowApiService.getTVShowDetailsWithCastAndVideos(seriesId, apiKey = API_KEY, language = defaultLocale())
                    ?.enqueue(object : Callback<TVShowDetailsWithCastAndVideos?> {
                        override fun onResponse(
                            p0: Call<TVShowDetailsWithCastAndVideos?>,
                            p1: Response<TVShowDetailsWithCastAndVideos?>
                        ) {
                            if (p1.isSuccessful) {
                                callback(p1)
                            }
                        }

                        override fun onFailure(
                            p0: Call<TVShowDetailsWithCastAndVideos?>,
                            p1: Throwable
                        ) {
                            callback(Response.success(null))
                        }
                    })
            } catch (e: Exception) {
                callback(Response.success(null))
                e.printStackTrace()
            }
        }
    }
    suspend fun getTotalPages(apiCall: suspend () -> Response<TotalPages?>?) : Int {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiCall()
                if (response?.isSuccessful == true) {
                    response.body()?.getTotalPages() ?: 1
                } else {
                    1
                }
            } catch (e: Exception) {
                e.printStackTrace()
                1
            }
        }
    }

    suspend fun getTotalPagesPopular(): Int {
        return withContext(Dispatchers.IO) {
            try {
                val pages = getTotalPages {
                    tvShowApiService.getTotalPagesPopular(apiKey = API_KEY, language = defaultLocale())?.execute()
                }
                pages
            } catch (e: Exception) {
                e.printStackTrace()
                1
            }
        }
    }

    suspend fun getTotalPagesTopRated(): Int {
        return withContext(Dispatchers.IO) {
            try {
                val pages = getTotalPages {
                    tvShowApiService.getTotalPagesPopular(apiKey = API_KEY, language = defaultLocale())?.execute()
                }
                pages
            } catch (e: Exception) {
                e.printStackTrace()
                1
            }
        }
    }

    suspend fun getTotalPagesOnTheAir(): Int {
        return withContext(Dispatchers.IO) {
            try {
                val pages = getTotalPages {
                    tvShowApiService.getTotalPagesPopular(apiKey = API_KEY, language = defaultLocale())?.execute()
                }
                pages
            } catch (e: Exception) {
                e.printStackTrace()
                1
            }
        }
    }

    suspend fun getTotalPagesAiringToday(): Int {
        return withContext(Dispatchers.IO) {
            try {
                val pages = getTotalPages {
                    tvShowApiService.getTotalPagesPopular(apiKey = API_KEY, language = defaultLocale())?.execute()
                }
                pages
            } catch (e: Exception) {
                e.printStackTrace()
                1
            }
        }
    }
}