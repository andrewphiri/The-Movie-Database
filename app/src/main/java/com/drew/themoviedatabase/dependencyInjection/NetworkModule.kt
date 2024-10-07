package com.drew.themoviedatabase.dependencyInjection

import com.drew.themoviedatabase.Network.BASE_URL
import com.drew.themoviedatabase.Network.CastApiService
import com.drew.themoviedatabase.Network.LoginApiService
import com.drew.themoviedatabase.Network.MovieApiService
import com.drew.themoviedatabase.Network.MultiSearchResult
import com.drew.themoviedatabase.Network.MyAccountApiService
import com.drew.themoviedatabase.Network.TVShowApiService
import com.drew.themoviedatabase.Utilities.MultiSearchResultAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
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
    fun provideGson() : Gson {
        return GsonBuilder()
            .registerTypeAdapter(MultiSearchResult::class.java, MultiSearchResultAdapter())
            .create()
    }

    @Provides
    fun provideRetrofit(gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
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

    @Provides
    fun provideLoginApiService(retrofit: Retrofit) : LoginApiService {
        return retrofit.create(LoginApiService::class.java)
    }

    @Provides
    fun provideMyAccountApiService(retrofit: Retrofit) : MyAccountApiService {
        return retrofit.create(MyAccountApiService::class.java)
    }
}