package com.drew.themoviedatabase.data.repository.TVShows

import com.drew.themoviedatabase.Utilities.defaultLocale
import com.drew.themoviedatabase.Utilities.imageLanguage
import com.drew.themoviedatabase.data.remote.MovieImagesResponse
import com.drew.themoviedatabase.data.remote.ReviewsResponse
import com.drew.themoviedatabase.data.remote.TVShowApiService
import com.drew.themoviedatabase.data.remote.TVShowDetailsWithCastAndVideos
import com.drew.themoviedatabase.data.remote.TVShowsResponse
import com.drew.themoviedatabase.data.remote.TotalPages
import com.drew.themoviedatabase.data.remote.TrailersResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class TVShowsRepository@Inject constructor(
    val tvShowApiService: TVShowApiService
) {
    private fun fetchTVShows(
        pages: Int,
        apiCall: suspend (Int) -> Response<TVShowsResponse?>?,
        callback: (List<com.drew.themoviedatabase.data.model.TVShow>?) -> Unit
    ) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val allTVShows = mutableListOf<com.drew.themoviedatabase.data.model.TVShow>()
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

        ) : List<com.drew.themoviedatabase.data.model.TVShow?>? {
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
    ) : List<com.drew.themoviedatabase.data.model.TVShowDetails?>? {
        return try {
            val shows = fetchTVShows(pages = pages, apiCall = apiCall)
            if (shows != null) {
                coroutineScope {
                    val tvshow = shows.map { movie ->
                        async(Dispatchers.IO) {
                            try {
                                val response = tvShowApiService.getTVShowDetailsWithContentRatings(
                                    movie?.id, apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale())?.execute()

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
        callback: (List<com.drew.themoviedatabase.data.model.TVShowDetails?>?) -> Unit,
    ) {
        try {
            fetchTVShows(pages, apiCall) { tvShows ->
                if (tvShows != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val detailedTVShows = mutableListOf<com.drew.themoviedatabase.data.model.TVShowDetails?>()
                        val jobs = tvShows.map { tvShow ->
                            async {
                                try {
                                    val response = tvShowApiService.getTVShowDetailsWithContentRatings(tvShow.id, apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale())?.execute()

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

    fun fetchPopularTVShowsDetails(pages: Int, callback: (List<com.drew.themoviedatabase.data.model.TVShowDetails?>?) -> Unit) {
        try {
            fetchTVShowDetails(
                pages = pages,
                apiCall = { page -> tvShowApiService.getPopularTVShows(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale(), page = page)?.execute() },
                callback = callback,
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun fetchPopularTVShowsDetails(pages: Int) : List<com.drew.themoviedatabase.data.model.TVShowDetails?>? {
       return try {
            fetchTVShowDetails(
                pages = pages,
                apiCall = { page -> tvShowApiService.getPopularTVShows(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale(), page = page)?.execute() },
            )
        } catch (e: Exception) {
            e.printStackTrace()
           null
        }
    }

    fun fetchTopTVShowDetails(pages: Int, callback: (List<com.drew.themoviedatabase.data.model.TVShowDetails?>?) -> Unit) {
        try {
            fetchTVShowDetails(
                pages = pages,
                apiCall = { page -> tvShowApiService.getTopRatedTVShows(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale(), page =  page)?.execute() },
                callback = callback
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun fetchTopTVShowDetails(pages: Int) : List<com.drew.themoviedatabase.data.model.TVShowDetails?>? {
       return try {
            fetchTVShowDetails(
                pages = pages,
                apiCall = { page -> tvShowApiService.getTopRatedTVShows(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale(), page =  page)?.execute() },
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun fetchOnTheAirTVShows(pages: Int, callback: (List<com.drew.themoviedatabase.data.model.TVShowDetails?>?) -> Unit) {
        try {
            fetchTVShowDetails(
                pages = pages,
                apiCall = { page -> tvShowApiService.getTVShowsOnTheAir(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale(), page =  page)?.execute() },
                callback = callback,
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    suspend fun fetchOnTheAirTVShows(pages: Int) : List<com.drew.themoviedatabase.data.model.TVShowDetails?>? {
        return try {
            fetchTVShowDetails(
                pages = pages,
                apiCall = { page -> tvShowApiService.getTVShowsOnTheAir(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale(), page =  page)?.execute() },
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun fetchAiringTodayTVShows(pages: Int, callback: (List<com.drew.themoviedatabase.data.model.TVShowDetails?>?) -> Unit) {
        try {
            fetchTVShowDetails(
                pages = pages,
                apiCall = { page -> tvShowApiService.getTVShowsAiringToday(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale(), page =  page)?.execute() },
                callback = callback,
            )
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    suspend fun fetchAiringTodayTVShows(pages: Int) : List<com.drew.themoviedatabase.data.model.TVShowDetails?>? {
       return try {
            fetchTVShowDetails(
                pages = pages,
                apiCall = { page -> tvShowApiService.getTVShowsAiringToday(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale(), page =  page)?.execute() },
            )
        } catch (e : Exception) {
            e.printStackTrace()
           null
        }
    }

    fun fetchSimilarTVShows(seriesId: Int, pages: Int, callback: (List<com.drew.themoviedatabase.data.model.TVShowDetails?>?) -> Unit) {
        try {
            fetchTVShowDetails(
                pages = pages,
                apiCall = { page -> tvShowApiService.getSimilarTVShows(seriesId = seriesId, apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale(), page =  page)?.execute() },
                callback = callback)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun fetchSimilarTVShows(seriesId: Int, pages: Int) : List<com.drew.themoviedatabase.data.model.TVShowDetails?>?{
        return try {
            fetchTVShowDetails(
                pages = pages,
                apiCall = { page -> tvShowApiService.getSimilarTVShows(seriesId = seriesId, apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale(), page =  page)?.execute() },)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getTVShowReviews(seriesId: Int, callback: (Response<ReviewsResponse?>) -> Unit) {
            try {
                tvShowApiService.getTVShowReviews(seriesId, apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale(), page = 1)?.enqueue(object : Callback<ReviewsResponse?> {
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
                apiKey = com.drew.themoviedatabase.data.remote.API_KEY,
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
    suspend fun getShowPhotos(seriesId: Int) : List<com.drew.themoviedatabase.data.model.Photos?>? {
        return try {
            coroutineScope {
                withContext(Dispatchers.IO) {
                    try {
                        val response = tvShowApiService.getTVShowImages(
                            movieId = seriesId,
                            apiKey = com.drew.themoviedatabase.data.remote.API_KEY,
                            imageLanguage = imageLanguage,
                            language = defaultLocale()
                        )?.execute()
                        if (response?.isSuccessful == true) {
                            response.body()?.getAllImages()
                        } else {
                            emptyList()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        emptyList()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun fetchCertifications(callback: (Response<com.drew.themoviedatabase.data.model.Certifications?>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                tvShowApiService.getCertifications(apiKey = com.drew.themoviedatabase.data.remote.API_KEY)?.enqueue(object : Callback<com.drew.themoviedatabase.data.model.Certifications?> {
                    override fun onResponse(
                        p0: Call<com.drew.themoviedatabase.data.model.Certifications?>,
                        p1: Response<com.drew.themoviedatabase.data.model.Certifications?>
                    ) {
                        if (p1.isSuccessful) {
                            callback(p1)
                        }
                    }
                    override fun onFailure(p0: Call<com.drew.themoviedatabase.data.model.Certifications?>, p1: Throwable) {
                        callback(Response.success(null))
                    }
                })
            } catch (e: Exception) {
                callback(Response.success(null))
                e.printStackTrace()
            }
        }
    }

    fun fetchTrailers(seriesId: Int, callback: (Response<TrailersResponse?>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                tvShowApiService.getTVShowTrailer(seriesId, apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale())?.enqueue(object :
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

//    suspend fun fetchTrailers(movieId: Int) : List<Trailers?>? {
//        return try {
//            coroutineScope {
//                withContext(Dispatchers.IO) {
//                    val response = tvShowApiService.getTVShowTrailer(movieId, apiKey = API_KEY, language = defaultLocale())?.execute()
//
//                    if (response?.isSuccessful == true){
//                        response.body()?.getResults()
//                    } else {
//                        emptyList()
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }

    suspend fun fetchReviews(movieId: Int, page: Int) : List<com.drew.themoviedatabase.data.model.Reviews?>? {
        return try {
            coroutineScope {
                withContext(Dispatchers.IO) {
                    val response = tvShowApiService.getTVShowReviews(movieId, apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale(), page = page)?.execute()
                    if (response?.isSuccessful == true){
                        response.body()?.getResults()
                    } else {
                        emptyList()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }



    fun fetchTVShowsDetailsWithCastAndVideos(seriesId: Int, callback: (Response<TVShowDetailsWithCastAndVideos?>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                tvShowApiService.getTVShowDetailsWithCastAndVideos(seriesId, apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale())
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
                    tvShowApiService.getTotalPagesPopular(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale())?.execute()
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
                    tvShowApiService.getTotalPagesPopular(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale())?.execute()
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
                    tvShowApiService.getTotalPagesPopular(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale())?.execute()
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
                    tvShowApiService.getTotalPagesPopular(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale())?.execute()
                }
                pages
            } catch (e: Exception) {
                e.printStackTrace()
                1
            }
        }
    }
}