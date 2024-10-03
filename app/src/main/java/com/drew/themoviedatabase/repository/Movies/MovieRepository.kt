package com.drew.themoviedatabase.repository.Movies

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.drew.themoviedatabase.Network.API_KEY
import com.drew.themoviedatabase.Network.CastResponse
import com.drew.themoviedatabase.Network.CertificationsResponse
import com.drew.themoviedatabase.Network.MovieApiService
import com.drew.themoviedatabase.Network.MovieDetailsResponse
import com.drew.themoviedatabase.Network.MovieImagesResponse
import com.drew.themoviedatabase.Network.MovieReleaseData
import com.drew.themoviedatabase.Network.MovieResponse
import com.drew.themoviedatabase.Network.MultiSearchResult
import com.drew.themoviedatabase.Network.ReviewsResponse
import com.drew.themoviedatabase.Network.TotalPages
import com.drew.themoviedatabase.Network.TrailersResponse
import com.drew.themoviedatabase.POJO.Certifications
import com.drew.themoviedatabase.POJO.Movie
import com.drew.themoviedatabase.POJO.MovieDetails
import com.drew.themoviedatabase.POJO.MovieDetailsReleaseData
import com.drew.themoviedatabase.POJO.Photos
import com.drew.themoviedatabase.POJO.Reviews
import com.drew.themoviedatabase.POJO.Trailers
import com.drew.themoviedatabase.Utilities.defaultLocale
import com.drew.themoviedatabase.Utilities.imageLanguage
import com.drew.themoviedatabase.repository.MultiSearchPagingSource
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

class MovieRepository @Inject constructor(
    private  val movieApiService: MovieApiService,
) {

    fun getPopularMovies() : Flow<PagingData<MovieDetailsReleaseData>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { PopularMoviesPagingSource(this) }
        ).flow
    }

    fun getTopRatedMovies() : Flow<PagingData<MovieDetailsReleaseData>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { TopRatedMoviesPagingSource(this) }
        ).flow
    }

    fun getUpcomingMovies() : Flow<PagingData<MovieDetailsReleaseData>> {
        return Pager(
            config = PagingConfig(pageSize = 40, initialLoadSize = 40),
            pagingSourceFactory = { UpcomingMoviesPagingSource(this) }
        ).flow
    }

    fun getNowPlayingMovies() : Flow<PagingData<MovieDetailsReleaseData>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { NowPlayingMoviesPagingSource(this) }
        ).flow
    }

    fun getTrendingMovies() : Flow<PagingData<MovieDetailsReleaseData>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { TrendingMoviesPagingSource(this) }
        ).flow
    }

    fun getSimilarMovies(movieId: Int) : Flow<PagingData<MovieDetailsReleaseData>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { SimilarMoviesPagingSource(this, movieId) }
        ).flow
    }

    fun getReviews(movieId: Int) : Flow<PagingData<Reviews>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { MoviesReviewsPagingSource(this, movieId) }
        ).flow
    }

    fun fetchMultiSearch(query: String) : Flow<PagingData<MultiSearchResult>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { MultiSearchPagingSource(this, query) }
        ).flow
    }

    fun getMovieImages(movieId: Int) : Flow<PagingData<Photos>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { MoviePhotosPagingSource(this, movieId) }
        ).flow
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

    private suspend fun fetchMovies(
        pages: Int,
        apiCall: suspend (Int) -> Response<MovieResponse?>?,

    ) : List<Movie?>? {
        return try {
            coroutineScope {
                withContext(Dispatchers.IO) {
                    try {
                        val response = apiCall(pages)
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
                                    val response = movieApiService.getMovieDetailsWithCertifications(movie.id, apiKey = API_KEY, language = defaultLocale())?.execute()

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
        pages: Int,
        apiCall: suspend (Int) -> Response<MovieResponse?>?
    ) : List<MovieDetailsReleaseData?>? {
        return try {
            val movies = fetchMovies(pages = pages, apiCall = apiCall)
            if (movies != null) {
                coroutineScope {
                    val detailedMovies = movies.map { movie ->
                        async(Dispatchers.IO) {
                            try {
                                val response = movieApiService.getMovieDetailsWithCertifications(movie?.id, apiKey = API_KEY, language = defaultLocale())?.execute()

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
               apiCall = { page -> movieApiService.getPopularMovies(apiKey = API_KEY, language = defaultLocale(), page =  page)?.execute() },
               callback = callback,
           )
       } catch (e: Exception) {
           e.printStackTrace()
       }
    }

    suspend fun fetchPopularMovieDetails(pages: Int) : List<MovieDetailsReleaseData?>? {
       return try {
            fetchMovieDetails(
                pages = pages,
                apiCall = { page -> movieApiService.getPopularMovies(apiKey = API_KEY, language = defaultLocale(), page =  page)?.execute() },
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
                apiCall = { page -> movieApiService.getTopRatedMovies(apiKey = API_KEY, language = defaultLocale(), page =  page)?.execute() },
                callback = callback
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    suspend fun fetchTopRatedMovieDetails(pages: Int) : List<MovieDetailsReleaseData?>? {
        return try {
            fetchMovieDetails(
                pages = pages,
                apiCall = { page -> movieApiService.getTopRatedMovies(apiKey = API_KEY, language = defaultLocale(), page =  page)?.execute() },
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
                apiCall = { page -> movieApiService.getNowPlayingMovies(apiKey = API_KEY, language = defaultLocale(), page =  page)?.execute() },
                callback = callback,
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun fetchNowPlayingMovieDetails(pages: Int): List<MovieDetailsReleaseData?>? {
        return try {
            fetchMovieDetails(
                pages = pages,
                apiCall = { page -> movieApiService.getNowPlayingMovies(apiKey = API_KEY, language = defaultLocale(), page =  page)?.execute() },
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
                apiCall = { page -> movieApiService.getSimilarMovies(movieId = movieId, apiKey = API_KEY, language = defaultLocale(), page =  page)?.execute() },
                callback = callback,
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    suspend fun fetchSimilarMovieDetails(movieId: Int, pages: Int) : List<MovieDetailsReleaseData?>? {
        return try {
            fetchMovieDetails(
                pages = pages,
                apiCall = { page -> movieApiService.getSimilarMovies(movieId = movieId, apiKey = API_KEY, language = defaultLocale(), page =  page)?.execute() },
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
                apiCall = { page -> movieApiService.getUpcomingMovies(apiKey = API_KEY, language = defaultLocale(), page =  page)?.execute() },
                callback = callback,
            )
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    suspend fun fetchUpcomingMovieDetails(pages: Int): List<MovieDetailsReleaseData?>? {
      return try {
            fetchMovieDetails(
                pages = pages,
                apiCall = { page -> movieApiService.getUpcomingMovies(apiKey = API_KEY, language = defaultLocale(), page =  page)?.execute() },
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
                apiCall = { page -> movieApiService.getTrendingMovies(apiKey = API_KEY, language = defaultLocale())?.execute() },
                callback = callback
            )
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    suspend fun fetchTrendingMovieDetails(pages: Int) :List<MovieDetailsReleaseData?>? {
       return try {
            fetchMovieDetails(
                pages = pages,
                apiCall = { page -> movieApiService.getTrendingMovies(apiKey = API_KEY, language = defaultLocale(), page =  page)?.execute() }
            )
        } catch (e : Exception) {
            e.printStackTrace()
           null
        }
    }

    fun fetchTrailers(movieId: Int, callback: (Response<TrailersResponse?>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                movieApiService.getTrailer(movieId, apiKey = API_KEY, language = defaultLocale())?.enqueue(object :
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

    suspend fun fetchReviews(movieId: Int, page: Int) : List<Reviews?>? {
        return try {
            coroutineScope {
                withContext(Dispatchers.IO) {
                    val response = movieApiService.getReviews(movieId, apiKey = API_KEY, language = defaultLocale(), page = page)?.execute()
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

    fun fetchReleaseDates(movieId: Int, callback: (Response<MovieReleaseData?>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                movieApiService.getReleaseDateAndCertification(movieId, apiKey = API_KEY, language = defaultLocale())?.enqueue(object : Callback<MovieReleaseData?> {
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
                    movieApiService.getMovieDetails(movieId, apiKey = API_KEY, language = defaultLocale())?.enqueue(object : Callback<MovieDetails?> {
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
               movieApiService.getCast(movieId, apiKey = API_KEY, language = defaultLocale())?.enqueue(object : Callback<CastResponse?> {
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
                movieApiService.getMovieDetailsWithCastAndVideos(movieId, apiKey = API_KEY, language = defaultLocale()).enqueue(object : Callback<MovieDetailsResponse?> {
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
                movieApiService.getReviews(movieId, apiKey = API_KEY, language = defaultLocale())?.enqueue(object : Callback<ReviewsResponse?> {
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

    fun fetchCertifications(callback: (Response<Certifications?>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                movieApiService.getCertifications(apiKey = API_KEY)?.enqueue(object : Callback<Certifications?> {
                    override fun onResponse(
                        p0: Call<Certifications?>,
                        p1: Response<Certifications?>
                    ) {
                        if (p1.isSuccessful) {
                            callback(p1)
                        }
                    }
                    override fun onFailure(p0: Call<Certifications?>, p1: Throwable) {
                        callback(Response.success(null))
                    }
                })
                } catch (e: Exception) {
                    callback(Response.success(null))
                e.printStackTrace()
            }
        }
    }

    suspend fun getMoviePhotos(movieId: Int) : List<Photos?>? {
        return try {
            coroutineScope {
                withContext(Dispatchers.IO) {
                    try {
                      val response = movieApiService.getMovieImages(
                            movieId = movieId,
                            apiKey = API_KEY,
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
                    movieApiService.getTotalPagesUpcoming(apiKey = API_KEY, language = defaultLocale())?.execute()
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
                        apiKey = API_KEY,
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
                        apiKey = API_KEY,
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
                        apiKey = API_KEY,
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
                    val response = movieApiService.getMultiSearch(apiKey = API_KEY, language = defaultLocale(), query = searchQuery, page = page)?.execute()
                    response?.body()?.results ?: emptyList()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
