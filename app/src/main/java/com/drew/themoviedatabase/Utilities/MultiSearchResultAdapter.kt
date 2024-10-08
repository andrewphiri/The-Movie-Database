package com.drew.themoviedatabase.Utilities

import com.drew.themoviedatabase.data.remote.MultiSearchResult
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class MultiSearchResultAdapter: TypeAdapter<MultiSearchResult>() {
    override fun write(out: JsonWriter?, value: MultiSearchResult?) {

    }

    override fun read(reader: JsonReader?): MultiSearchResult? {
        val jsonObject = JsonParser.parseReader(reader).asJsonObject

        return when(val mediaType = jsonObject.get("media_type").asString) {
            "movie" -> Gson().fromJson(jsonObject, MultiSearchResult.Movie::class.java)
            "tv" -> Gson().fromJson(jsonObject, MultiSearchResult.TV::class.java)
            "person" -> Gson().fromJson(jsonObject, MultiSearchResult.Person::class.java)
            else -> throw IllegalArgumentException("Unknown media type: $mediaType")

        }
    }
}