package com.drew.themoviedatabase.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import com.drew.themoviedatabase.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<UserPreferences>
) {
    val isDataInserted : Flow<Boolean> = dataStore
        .data
        .map {
            it.dataInserted
        }
        .catch { exception ->
            if(exception is IOException) {
                emit(UserPreferences.getDefaultInstance().dataInserted)
            } else {
                throw exception
            }
        }

    suspend fun updateIsDataInserted(isDataInserted : Boolean) {
        dataStore.updateData {
            it.toBuilder().setDataInserted(isDataInserted).build()
        }
    }
}