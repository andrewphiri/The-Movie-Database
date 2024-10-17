package com.drew.themoviedatabase.dependencyInjection

import com.drew.themoviedatabase.Utilities.MultiSearchResultAdapter
import com.drew.themoviedatabase.data.remote.BASE_URL
import com.drew.themoviedatabase.data.remote.CastApiService
import com.drew.themoviedatabase.data.remote.LoginApiService
import com.drew.themoviedatabase.data.remote.MovieApiService
import com.drew.themoviedatabase.data.remote.MultiSearchResult
import com.drew.themoviedatabase.data.remote.MyAccountApiService
import com.drew.themoviedatabase.data.remote.TVShowApiService
import com.drew.themoviedatabase.data.remote.YOUTUBE_BASE_URL
import com.drew.themoviedatabase.data.remote.YoutubeApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {
    @Provides
    fun provideGson() : Gson {
        return GsonBuilder()
            .registerTypeAdapter(MultiSearchResult::class.java, MultiSearchResultAdapter())
            .create()
    }

    @Provides
    @Named("TMDBRetrofit")
    fun provideTMDBRetrofit(gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Named("YoutubeRetrofit")
    fun provideYoutubeRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(YOUTUBE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideMovieApiService(@Named("TMDBRetrofit") retrofit: Retrofit): MovieApiService {
        return retrofit.create(MovieApiService::class.java)
    }

    @Provides
    fun provideTVShowApiService(@Named("TMDBRetrofit") retrofit: Retrofit): TVShowApiService {
        return retrofit.create(TVShowApiService::class.java)
    }

    @Provides
    fun provideCastApiService(@Named("TMDBRetrofit") retrofit: Retrofit): CastApiService {
        return retrofit.create(CastApiService::class.java)
    }

    @Provides
    fun provideLoginApiService(@Named("TMDBRetrofit") retrofit: Retrofit) : LoginApiService {
        return retrofit.create(LoginApiService::class.java)
    }

    @Provides
    fun provideMyAccountApiService(@Named("TMDBRetrofit") retrofit: Retrofit) : MyAccountApiService {
        return retrofit.create(MyAccountApiService::class.java)
    }

    @Provides
    fun provideYoutubeApiService(@Named("YoutubeRetrofit") retrofit: Retrofit) : YoutubeApiService {
        return retrofit.create(YoutubeApiService::class.java)

    }
}