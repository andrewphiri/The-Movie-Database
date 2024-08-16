package com.drew.themoviedatabase.dependencyInjection

import com.drew.themoviedatabase.Network.BASE_URL
import com.drew.themoviedatabase.Network.CastApiService
import com.drew.themoviedatabase.Network.MovieApiService
import com.drew.themoviedatabase.Network.TVShowApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideMovieApiService(retrofit: Retrofit): MovieApiService {
        return retrofit.create(MovieApiService::class.java)
    }

    @Provides
    fun provideTVShowApiService(retrofit: Retrofit): TVShowApiService {
        return retrofit.create(TVShowApiService::class.java)
    }

    @Provides
    fun provideCastApiService(retrofit: Retrofit): CastApiService {
        return retrofit.create(CastApiService::class.java)
    }

}