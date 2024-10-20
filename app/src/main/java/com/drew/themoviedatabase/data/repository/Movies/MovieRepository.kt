package com.drew.themoviedatabase.data.repository.Movies

import com.drew.themoviedatabase.Utilities.defaultLocale
import com.drew.themoviedatabase.Utilities.imageLanguage
import com.drew.themoviedatabase.data.remote.API_KEY
import com.drew.themoviedatabase.data.remote.MovieApiService
import com.drew.themoviedatabase.data.remote.MovieDetailsResponse
import com.drew.themoviedatabase.data.remote.MovieResponse
import com.drew.themoviedatabase.data.remote.MultiSearchResult
import com.drew.themoviedatabase.data.remote.ReviewsResponse
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

class MovieRepository @Inject constructor(
    private  val movieApiService: MovieApiService,
) {

    private fun fetchMovies(
        pages: Int,
        apiCall: suspend (Int) -> Response<MovieResponse?>?,
        callback: (List<com.drew.themoviedatabase.data.model.Movie>?) -> Unit
    ) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val allMovies = mutableListOf<com.drew.themoviedatabase.data.model.Movie>()
                val jobs = (1..pages).map { page ->
                    async {
                        try {
                            val response = apiCall(page)
                            if (response?.isSuccessful == true) {
                                response.body()?.let { movieResponse ->
                                    movieResponse.getResults()?.let { allMovies.addAll(it) }
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
                    callback(allMovies)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            callback(null)
        }
    }

    private suspend fun fetchMovies(
        apiCall: suspend () -> Response<MovieResponse?>?,
    ) : List<com.drew.themoviedatabase.data.model.Movie?>? {
        return try {
            coroutineScope {
                withContext(Dispatchers.IO) {
                    try {
                        val response = apiCall()
                        if (response?.isSuccessful == true) {
                            response.body()?.getResults()
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

    private fun fetchMovieDetails(
        pages: Int,
        apiCall: suspend (Int) -> Response<MovieResponse?>?,
        callback: (List<MovieDetailsReleaseData?>?) -> Unit,
    ) {
        try {
            fetchMovies(pages, apiCall) { movies ->
                if (movies != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val detailedMovies = mutableListOf<MovieDetailsReleaseData?>()
                        val jobs = movies.map { movie ->
                            async {
                                try {
                                    val response = movieApiService.getMovieDetailsWithCertifications(movie.id, apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale())?.execute()

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

                        detailedMovies.addAll(jobs.awaitAll())
                        withContext(Dispatchers.Main) {
                            callback(detailedMovies)
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

    private suspend fun fetchMovieDetails(
        apiCall: suspend () -> Response<MovieResponse?>?
    ) : List<MovieDetailsReleaseData?>? {
        return try {
            val movies = fetchMovies(apiCall = apiCall)
            if (movies != null) {
                coroutineScope {
                    val detailedMovies = movies.map { movie ->
                        async(Dispatchers.IO) {
                            try {
                                val response = movieApiService.getMovieDetailsWithCertifications(movie?.id, apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale())?.execute()

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
                    detailedMovies.awaitAll()
                }
            } else {
                null
            }

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }



    fun fetchPopularMovieDetails1(pages: Int, callback: (List<MovieDetailsReleaseData?>?) -> Unit) {
       try {
           fetchMovieDetails(
               pages = pages,
               apiCall = { page -> movieApiService.getPopularMovies(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale(), page =  page)?.execute() },
               callback = callback,
           )
       } catch (e: Exception) {
           e.printStackTrace()
       }
    }

    suspend fun fetchPopularMovieDetails(pages: Int) : List<MovieDetailsReleaseData?>? {
       return try {
            fetchMovieDetails(
                apiCall = { movieApiService.getPopularMovies(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale(), page =  pages)?.execute() },
            )
        } catch (e: Exception) {
            e.printStackTrace()
           null
        }
    }

    fun fetchTopRatedMovieDetails1(pages: Int, callback: (List<MovieDetailsReleaseData?>?) -> Unit) {
        try {
            fetchMovieDetails(
                pages = pages,
                apiCall = { page -> movieApiService.getTopRatedMovies(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale(), page =  page)?.execute() },
                callback = callback
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    suspend fun fetchTopRatedMovieDetails(pages: Int) : List<MovieDetailsReleaseData?>? {
        return try {
            fetchMovieDetails(
                apiCall = { movieApiService.getTopRatedMovies(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale(), page =  pages)?.execute() },
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun fetchNowPlayingMovieDetails(pages: Int, callback: (List<MovieDetailsReleaseData?>?) -> Unit) {
        try {
            fetchMovieDetails(
                pages = pages,
                apiCall = { page -> movieApiService.getNowPlayingMovies(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale(), page =  page)?.execute() },
                callback = callback,
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun fetchNowPlayingMovieDetails(pages: Int): List<MovieDetailsReleaseData?>? {
        return try {
            fetchMovieDetails(
                apiCall = {  movieApiService.getNowPlayingMovies(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale(), page =  pages)?.execute() },
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun fetchMoviesByGenre(pages: Int, genreId: Int) : List<MovieDetailsReleaseData?>? {
        return try {
            fetchMovieDetails(
                apiCall = { movieApiService.getMoviesByGenre(apiKey = API_KEY, language = defaultLocale(), genreId = genreId, page =  pages)?.execute() },
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun fetchSimilarMovieDetails(movieId: Int, pages: Int, callback: (List<MovieDetailsReleaseData?>?) -> Unit) {
        try {
            fetchMovieDetails(
                pages = pages,
                apiCall = { page -> movieApiService.getSimilarMovies(movieId = movieId, apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale(), page =  page)?.execute() },
                callback = callback,
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    suspend fun fetchSimilarMovieDetails(movieId: Int, pages: Int) : List<MovieDetailsReleaseData?>? {
        return try {
            fetchMovieDetails(
                apiCall = { movieApiService.getSimilarMovies(movieId = movieId, apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale(), page =  pages)?.execute() },
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun fetchUpcomingMovieDetails(pages: Int, callback: (List<MovieDetailsReleaseData?>?) -> Unit) {
        try {
            fetchMovieDetails(
                pages = pages,
                apiCall = { page -> movieApiService.getUpcomingMovies(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale(), page =  page)?.execute() },
                callback = callback,
            )
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    suspend fun fetchUpcomingMovieDetails(pages: Int): List<MovieDetailsReleaseData?>? {
      return try {
            fetchMovieDetails(
                apiCall = { movieApiService.getUpcomingMovies(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale(), page =  pages)?.execute() },
            )
        } catch (e : Exception) {
            e.printStackTrace()
          null
        }
    }

    fun fetchTrendingMovieDetails(pages: Int, callback: (List<MovieDetailsReleaseData?>?) -> Unit) {
        try {
            fetchMovieDetails(
                pages = pages,
                apiCall = { page -> movieApiService.getTrendingMovies(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale())?.execute() },
                callback = callback
            )
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    suspend fun fetchTrendingMovieDetails(pages: Int) :List<MovieDetailsReleaseData?>? {
       return try {
            fetchMovieDetails(
                apiCall = { movieApiService.getTrendingMovies(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale(), page =  pages)?.execute() }
            )
        } catch (e : Exception) {
            e.printStackTrace()
           null
        }
    }

    fun fetchTrailers(movieId: Int, callback: (Response<TrailersResponse?>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                movieApiService.getTrailer(movieId, apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale())?.enqueue(object :
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

    suspend fun fetchReviews(movieId: Int, page: Int) : List<com.drew.themoviedatabase.data.model.Reviews?>? {
        return try {
            coroutineScope {
                withContext(Dispatchers.IO) {
                    val response = movieApiService.getReviews(movieId, apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale(), page = page)?.execute()
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

    fun fetchReleaseDates(movieId: Int, callback: (Response<com.drew.themoviedatabase.data.remote.MovieReleaseData?>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                movieApiService.getReleaseDateAndCertification(movieId, apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale())?.enqueue(object : Callback<com.drew.themoviedatabase.data.remote.MovieReleaseData?> {
                    override fun onResponse(
                        p0: Call<com.drew.themoviedatabase.data.remote.MovieReleaseData?>,
                        p1: Response<com.drew.themoviedatabase.data.remote.MovieReleaseData?>
                    ) {
                        callback(p1)
                    }

                    override fun onFailure(p0: Call<com.drew.themoviedatabase.data.remote.MovieReleaseData?>, p1: Throwable) {
                        callback(Response.success(null))
                    }
                })
            } catch (e: Exception) {
                callback(Response.success(null))
            }
        }
    }

    fun fetchMovieDetails(movieId: Int, callback: (Response<com.drew.themoviedatabase.data.model.MovieDetails?>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
                try {
                    movieApiService.getMovieDetails(movieId, apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale())?.enqueue(object : Callback<com.drew.themoviedatabase.data.model.MovieDetails?> {
                        override fun onResponse(
                            p0: Call<com.drew.themoviedatabase.data.model.MovieDetails?>,
                            p1: Response<com.drew.themoviedatabase.data.model.MovieDetails?>
                        ) {
                            if (p1.isSuccessful) {
                                callback(p1)
                            }
                        }
                        override fun onFailure(p0: Call<com.drew.themoviedatabase.data.model.MovieDetails?>, p1: Throwable) {
                            callback(Response.success(null))
                        }
                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
        }
    }

   fun fetchCast(movieId: Int, callback: (Response<com.drew.themoviedatabase.data.remote.CastResponse?>) -> Unit) {
       CoroutineScope(Dispatchers.IO).launch {
           try {
               movieApiService.getCast(movieId, apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale())?.enqueue(object : Callback<com.drew.themoviedatabase.data.remote.CastResponse?> {
                   override fun onResponse(p0: Call<com.drew.themoviedatabase.data.remote.CastResponse?>, p1: Response<com.drew.themoviedatabase.data.remote.CastResponse?>) {
                       if (p1.isSuccessful) {
                           callback(p1)
                       }
                   }

                   override fun onFailure(p0: Call<com.drew.themoviedatabase.data.remote.CastResponse?>, p1: Throwable) {
                       callback(Response.success(null))
                   }

               })
           } catch (e: Exception) {
               callback(Response.success(null))
               e.printStackTrace()
           }
       }
   }

    fun fetchMovieDetailsWithCastAndVideos(movieId: Int, callback: (Response<MovieDetailsResponse?>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                movieApiService.getMovieDetailsWithCastAndVideos(movieId, apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale()).enqueue(object : Callback<MovieDetailsResponse?> {
                    override fun onResponse(p0: Call<MovieDetailsResponse?>, p1: Response<MovieDetailsResponse?>) {
                        if (p1.isSuccessful) {
                            callback(p1)
                        }
                    }

                    override fun onFailure(p0: Call<MovieDetailsResponse?>, p1: Throwable) {
                        callback(Response.success(null))
                    }

                })
            } catch (e: Exception) {
                callback(Response.success(null))
                e.printStackTrace()
            }
        }
    }

    fun getMovieReviews(movieId: Int, callback: (Response<ReviewsResponse?>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                movieApiService.getReviews(movieId, apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale())?.enqueue(object : Callback<ReviewsResponse?> {
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
    }

    fun fetchCertifications(callback: (Response<com.drew.themoviedatabase.data.model.Certifications?>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                movieApiService.getCertifications(apiKey = com.drew.themoviedatabase.data.remote.API_KEY)?.enqueue(object : Callback<com.drew.themoviedatabase.data.model.Certifications?> {
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

    suspend fun getMoviePhotos(movieId: Int) : List<com.drew.themoviedatabase.data.model.Photos?>? {
        return try {
        coroutineScope {
            withContext(Dispatchers.IO) {
                try {
                  val response = movieApiService.getMovieImages(
                        movieId = movieId,
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


    suspend fun getTotalPagesUpcoming(): Int {
        return withContext(Dispatchers.IO) {
            try {
               val pages = getTotalPages {
                    movieApiService.getTotalPagesUpcoming(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale())?.execute()
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
                    movieApiService.getTotalPagesTopRated(
                        apiKey = com.drew.themoviedatabase.data.remote.API_KEY,
                        language = defaultLocale()
                    )?.execute()
                }
                pages
            } catch (e: Exception) {
                e.printStackTrace()
                1
            }
        }
    }

    suspend fun getTotalPagesNowPlaying(): Int {
        return withContext(Dispatchers.IO) {
            try {
                val pages = getTotalPages {
                    movieApiService.getTotalPagesNowPlaying(
                        apiKey = com.drew.themoviedatabase.data.remote.API_KEY,
                        language = defaultLocale()
                    )?.execute()
                }
                pages
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
                    movieApiService.getTotalPagesPopular(
                        apiKey = com.drew.themoviedatabase.data.remote.API_KEY,
                        language = defaultLocale()
                    )?.execute()
                    }
                pages
            } catch (e: Exception) {
                e.printStackTrace()
                1
            }
        }
    }

    suspend fun multiSearch(searchQuery: String, page: Int) :  List<MultiSearchResult?>? {
        return try {
            coroutineScope {
                withContext(Dispatchers.IO) {
                    val response = movieApiService.getMultiSearch(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale(), query = searchQuery, page = page)?.execute()
                    response?.body()?.results ?: emptyList()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun multiSearchTrendingMedia(page: Int) :  List<MultiSearchResult?>? {
        return try {
            coroutineScope {
                withContext(Dispatchers.IO) {
                    val response = movieApiService.getAllTrendingMedia(apiKey = com.drew.themoviedatabase.data.remote.API_KEY, language = defaultLocale(), page = page)?.execute()
                    response?.body()?.results ?: emptyList()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
