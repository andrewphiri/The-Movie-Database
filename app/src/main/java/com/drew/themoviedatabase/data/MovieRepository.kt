package com.drew.themoviedatabase.data

import com.drew.themoviedatabase.Network.API_KEY
import com.drew.themoviedatabase.Network.CastResponse
import com.drew.themoviedatabase.Network.MovieApiService
import com.drew.themoviedatabase.Network.MovieDetailsResponse
import com.drew.themoviedatabase.Network.MovieReleaseData
import com.drew.themoviedatabase.Network.MovieResponse
import com.drew.themoviedatabase.Network.TotalPages
import com.drew.themoviedatabase.Network.TrailersResponse
import com.drew.themoviedatabase.POJO.Movie
import com.drew.themoviedatabase.POJO.MovieDetails
import com.drew.themoviedatabase.POJO.MovieDetailsReleaseData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private  val movieApiService: MovieApiService,
    private val defaultLocale: String
) {
    //val defaultLocale = Locale("${ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]}").toLanguageTag() ?: "en-US"


    fun fetchMovies(pages: Int, callback: (List<MovieDetails?>?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val allMovies: MutableList<MovieDetails?>? = mutableListOf()
            var successful = true
            for (page in 1..pages) {
                if (successful) {
                    val response = movieApiService.getPopularMovies(apiKey = API_KEY, language = defaultLocale, page =  page)?.execute()
                    if (response?.isSuccessful == true) {
//                        response.body()?.let { movieResponse ->
//                            movieResponse.getResults()?.let { allMovies.addAll(it) }
//                        }
                        val detailedMovies = response.body()?.getResults()?.map { movie ->
                            movieApiService.getMovieDetails(movie.id, apiKey = API_KEY, language = defaultLocale)?.execute()?.body()
                        }
                        if (detailedMovies != null) {
                            allMovies?.addAll(detailedMovies)
                        }

                    } else {
                        successful = false
                    }
                }
            }
            withContext(Dispatchers.Main) {
                callback(if (successful) allMovies else null)
            }
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
                                    val response = movieApiService.getMovieDetailsWithCertifications(movie.id, apiKey = API_KEY, language = defaultLocale)?.execute()

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

    fun fetchPopularMovieDetails(pages: Int, callback: (List<MovieDetailsReleaseData?>?) -> Unit) {
       try {
           fetchMovieDetails(
               pages = pages,
               apiCall = { page -> movieApiService.getPopularMovies(apiKey = API_KEY, language = defaultLocale, page =  page)?.execute() },
               callback = callback,
           )
       } catch (e: Exception) {
           e.printStackTrace()
       }
    }

    fun fetchTopRatedMovieDetails(pages: Int, callback: (List<MovieDetailsReleaseData?>?) -> Unit) {
        try {
            fetchMovieDetails(
                pages = pages,
                apiCall = { page -> movieApiService.getTopRatedMovies(apiKey = API_KEY, language = defaultLocale, page =  page)?.execute() },
                callback = callback
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun fetchNowPlayingMovieDetails(pages: Int, callback: (List<MovieDetailsReleaseData?>?) -> Unit) {
        try {
            fetchMovieDetails(
                pages = pages,
                apiCall = { page -> movieApiService.getNowPlayingMovies(apiKey = API_KEY, language = defaultLocale, page =  page)?.execute() },
                callback = callback,
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun fetchUpcomingMovieDetails(pages: Int, callback: (List<MovieDetailsReleaseData?>?) -> Unit) {
        try {
            fetchMovieDetails(
                pages = pages,
                apiCall = { page -> movieApiService.getUpcomingMovies(apiKey = API_KEY, language = defaultLocale, page =  page)?.execute() },
                callback = callback,
            )
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    fun fetchTrendingMovieDetails(pages: Int, callback: (List<MovieDetailsReleaseData?>?) -> Unit) {
        try {
            fetchMovieDetails(
                pages = pages,
                apiCall = { page -> movieApiService.getTrendingMovies(apiKey = API_KEY, language = defaultLocale)?.execute() },
                callback = callback
            )
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }


    private fun fetchMovies(
        pages: Int,
        apiCall: suspend (Int) -> Response<MovieResponse?>?,
        callback: (List<Movie>?) -> Unit
    ) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val allMovies = mutableListOf<Movie>()
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

    fun fetchTrailers(movieId: Int, callback: (Response<TrailersResponse?>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                movieApiService.getTrailer(movieId, apiKey = API_KEY, language = defaultLocale)?.enqueue(object : Callback<TrailersResponse?> {
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

    fun fetchReleaseDates(movieId: Int, callback: (Response<MovieReleaseData?>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                movieApiService.getReleaseDateAndCertification(movieId, apiKey = API_KEY, language = defaultLocale)?.enqueue(object : Callback<MovieReleaseData?> {
                    override fun onResponse(
                        p0: Call<MovieReleaseData?>,
                        p1: Response<MovieReleaseData?>
                    ) {
                        callback(p1)
                    }

                    override fun onFailure(p0: Call<MovieReleaseData?>, p1: Throwable) {
                        callback(Response.success(null))
                    }
                })
            } catch (e: Exception) {
                callback(Response.success(null))
            }
        }
    }

    fun fetchMovieDetails(movieId: Int, callback: (Response<MovieDetails?>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
                try {
                    movieApiService.getMovieDetails(movieId, apiKey = API_KEY, language = defaultLocale)?.enqueue(object : Callback<MovieDetails?> {
                        override fun onResponse(
                            p0: Call<MovieDetails?>,
                            p1: Response<MovieDetails?>
                        ) {
                            if (p1.isSuccessful) {
                                callback(p1)
                            }
                        }
                        override fun onFailure(p0: Call<MovieDetails?>, p1: Throwable) {
                            callback(Response.success(null))
                        }
                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
        }
    }

   fun fetchCast(movieId: Int, callback: (Response<CastResponse?>) -> Unit) {
       CoroutineScope(Dispatchers.IO).launch {
           try {
               movieApiService.getCast(movieId, apiKey = API_KEY, language = defaultLocale)?.enqueue(object : Callback<CastResponse?> {
                   override fun onResponse(p0: Call<CastResponse?>, p1: Response<CastResponse?>) {
                       if (p1.isSuccessful) {
                           callback(p1)
                       }
                   }

                   override fun onFailure(p0: Call<CastResponse?>, p1: Throwable) {
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
                movieApiService.getMovieDetailsWithCastAndVideos(movieId, apiKey = API_KEY, language = defaultLocale).enqueue(object : Callback<MovieDetailsResponse?> {
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

    fun getTotalPagesUpcoming(callback: (Int) -> Unit)  {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                movieApiService.getTotalPagesUpcoming(apiKey = API_KEY, language = defaultLocale)?.enqueue(object : Callback<TotalPages?> {
                    override fun onResponse(p0: Call<TotalPages?>, p1: Response<TotalPages?>) {
                        if (p1.isSuccessful) {
                            callback(p1.body()?.getTotalPages() ?: 1)
                        }
                    }

                    override fun onFailure(p0: Call<TotalPages?>, p1: Throwable) {
                        callback(1)
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    suspend fun getTotalPagesUpcoming(): Int {
        return withContext(Dispatchers.IO) {
            try {
                val response = movieApiService.getTotalPagesUpcoming(apiKey = API_KEY, language = defaultLocale)?.execute()
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

}
