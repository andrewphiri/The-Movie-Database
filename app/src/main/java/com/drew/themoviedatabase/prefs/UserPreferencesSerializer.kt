package com.drew.themoviedatabase.prefs

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.drew.themoviedatabase.UserPreferences
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object UserPreferencesSerializer : Serializer<UserPreferences> {
    override val defaultValue: UserPreferences = UserPreferences
        .getDefaultInstance()
        .toBuilder()
        .setDataInserted(false)
        .build()

    override suspend fun readFrom(input: InputStream): UserPreferences {
        try {
            return UserPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: UserPreferences, output: OutputStream) {
        if (t == defaultValue) {
            // if t is the default value, do not write anything
            return
        }
        t.writeTo(output)
    }
}