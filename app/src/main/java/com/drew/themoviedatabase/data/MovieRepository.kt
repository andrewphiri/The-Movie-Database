package com.drew.themoviedatabase.data

import android.util.Log
import com.drew.themoviedatabase.Network.API_KEY
import com.drew.themoviedatabase.Network.CastResponse
import com.drew.themoviedatabase.Network.MovieApiService
import com.drew.themoviedatabase.Network.MovieReleaseData
import com.drew.themoviedatabase.Network.MovieResponse
import com.drew.themoviedatabase.Network.TrailersResponse
import com.drew.themoviedatabase.POJO.Movie
import com.drew.themoviedatabase.POJO.MovieDetails
import com.drew.themoviedatabase.POJO.MovieDetailsReleaseData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private  val movieApiService: MovieApiService
) {

    fun fetchMovies(pages: Int, callback: (List<MovieDetails?>?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val allMovies: MutableList<MovieDetails?>? = mutableListOf()
            var successful = true
            for (page in 1..pages) {
                if (successful) {
                    val response = movieApiService.getPopularMovies(API_KEY, page)?.execute()
                    if (response?.isSuccessful == true) {
//                        response.body()?.let { movieResponse ->
//                            movieResponse.getResults()?.let { allMovies.addAll(it) }
//                        }
                        val detailedMovies = response.body()?.getResults()?.map { movie ->
                            movieApiService.getMovieDetails(movie.id, API_KEY)?.execute()?.body()
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

    fun fetchMovies1(pages: Int, callback: (List<MovieDetails?>?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val allMovies = mutableListOf<MovieDetails?>()
            val jobs = mutableListOf<Deferred<Unit>>()

            for (page in 1..pages) {
                jobs.add(async {
                    val response = movieApiService.getPopularMovies(API_KEY, page)?.execute()
                    if (response?.isSuccessful == true) {
                        val movies = response.body()?.getResults()
                        if (movies != null) {
                            val detailedMovies = movies.map { movie ->
                                async {
                                    val detailResponse = movieApiService.getMovieDetails(movie.id, API_KEY)?.execute()
                                    if (detailResponse?.isSuccessful == true) {
                                        detailResponse.body()
                                    } else {
                                        null
                                    }
                                }
                            }
                            allMovies.addAll(detailedMovies.awaitAll())
                        }
                    }
                })
            }

            jobs.awaitAll()

            withContext(Dispatchers.Main) {
                callback(allMovies)
            }
        }
    }



    private fun fetchMovieDetails(
        pages: Int,
        apiCall: suspend (Int) -> Response<MovieResponse?>?,
        callback: (MovieDetailsReleaseData?) -> Unit,
    ) {
        try {
            fetchMovies(pages, apiCall) { movies ->
                if (movies != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val detailedMovies = mutableListOf<MovieDetails?>()
                        var releaseDates = mutableListOf<MovieReleaseData?>()
                        val jobs = movies.map { movie ->
                            async {
                                try {
                                    val response = movieApiService.getMovieDetails(movie.id, API_KEY)?.execute()
                                    val releaseDateResponse = movieApiService.getReleaseDateAndCertification(movie.id, API_KEY)?.execute()
                                    if (response?.isSuccessful == true && releaseDateResponse?.isSuccessful == true) {
                                        releaseDates.add(releaseDateResponse.body())
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
                            callback(
                                MovieDetailsReleaseData(
                                    movieDetails = detailedMovies,
                                    movieReleaseData = releaseDates))
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

    fun fetchPopularMovieDetails(pages: Int, callback: (MovieDetailsReleaseData?) -> Unit) {
       try {
           fetchMovieDetails(
               pages = pages,
               apiCall = { page -> movieApiService.getPopularMovies(API_KEY, page)?.execute() },
               callback = callback,
           )
       } catch (e: Exception) {
           e.printStackTrace()
       }
    }

    fun fetchTopRatedMovieDetails(pages: Int, callback: (MovieDetailsReleaseData?) -> Unit) {
        try {
            fetchMovieDetails(
                pages = pages,
                apiCall = { page -> movieApiService.getTopRatedMovies(API_KEY, page)?.execute() },
                callback = callback
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun fetchNowPlayingMovieDetails(pages: Int, callback: (MovieDetailsReleaseData?) -> Unit) {
        try {
            fetchMovieDetails(
                pages = pages,
                apiCall = { page -> movieApiService.getNowPlayingMovies(API_KEY, page)?.execute() },
                callback = callback,
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun fetchUpcomingMovieDetails(pages: Int, callback: (MovieDetailsReleaseData?) -> Unit) {
        try {
            fetchMovieDetails(
                pages = pages,
                apiCall = { page -> movieApiService.getUpcomingMovies(API_KEY, page)?.execute() },
                callback = callback,
            )
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    fun fetchTrendingMovieDetails(pages: Int, callback: (MovieDetailsReleaseData?) -> Unit) {
        try {
            fetchMovieDetails(
                pages = pages,
                apiCall = { page -> movieApiService.getTrendingMovies(apiKey = API_KEY)?.execute() },
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
                movieApiService.getTrailer(movieId, API_KEY)?.enqueue(object : Callback<TrailersResponse?> {
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
                movieApiService.getReleaseDateAndCertification(movieId, API_KEY)?.enqueue(object : Callback<MovieReleaseData?> {
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
                    movieApiService.getMovieDetails(movieId, API_KEY)?.enqueue(object : Callback<MovieDetails?> {
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
               movieApiService.getCast(movieId, API_KEY)?.enqueue(object : Callback<CastResponse?> {
                   override fun onResponse(p0: Call<CastResponse?>, p1: Response<CastResponse?>) {
                       callback(p1)
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

}
