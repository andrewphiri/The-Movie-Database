package com.drew.themoviedatabase.dependencyInjection


import android.content.Context
import com.drew.themoviedatabase.MoviesApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provide for instances of MovieApplication that will be used
     * to inject context in VaccinationViewModel
     */
    @Singleton
    @Provides
    fun provideBaseApplication(@ApplicationContext context: Context): MoviesApplication {
        return context as MoviesApplication
    }
}