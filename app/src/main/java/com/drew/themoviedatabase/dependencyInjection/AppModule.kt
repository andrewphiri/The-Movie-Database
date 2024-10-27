package com.drew.themoviedatabase.dependencyInjection


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.drew.themoviedatabase.MoviesApplication
import com.drew.themoviedatabase.UserPreferences
import com.drew.themoviedatabase.prefs.UserPreferencesSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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

    @Provides
    @Singleton
    fun provideDataStoreManager(@ApplicationContext appContext: Context): DataStore<UserPreferences> =
        DataStoreFactory.create(
            serializer = UserPreferencesSerializer,
            produceFile = {
                appContext.dataStoreFile("user_preferences.pb")
            },
            corruptionHandler = null,
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
}