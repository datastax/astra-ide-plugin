package com.datastax.astra.stargate_rest_v2.infrastructure

import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import retrofit2.Response

@Throws(JsonParseException::class)
inline fun <reified T> Response<*>.getErrorResponse(serializerBuilder: GsonBuilder = Serializer.gsonBuilder): T? {
    val serializer = serializerBuilder.create()
    val reader = errorBody()?.charStream()
    if (reader != null) {
        return serializer.fromJson(reader, T::class.java)
    }
    return null
}
