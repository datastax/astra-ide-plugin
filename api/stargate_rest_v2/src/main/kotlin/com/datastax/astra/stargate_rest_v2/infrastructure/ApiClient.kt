package com.datastax.astra.stargate_rest_v2.infrastructure

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class ApiClient(
    private var baseUrl: String = defaultBasePath,
    private val okHttpClientBuilder: OkHttpClient.Builder? = null,
    private val serializerBuilder: GsonBuilder = Serializer.gsonBuilder,
    private val okHttpClient: OkHttpClient? = null
) {
    private val apiAuthorizations = mutableMapOf<String, Interceptor>()
    var logger: ((String) -> Unit)? = null

    private val retrofitBuilder: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(serializerBuilder.create()))
    }

    private val clientBuilder: OkHttpClient.Builder by lazy {
        okHttpClientBuilder ?: defaultClientBuilder
    }

    private val defaultClientBuilder: OkHttpClient.Builder by lazy {
        OkHttpClient()
            .newBuilder()
            .addInterceptor(
                HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                    override fun log(message: String) {
                        logger?.invoke(message)
                    }
                }).apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
    }

    init {
        normalizeBaseUrl()
    }

    /**
     * Adds an authorization to be used by the client
     * @param authName Authentication name
     * @param authorization Authorization interceptor
     * @return ApiClient
     */
    fun addAuthorization(authName: String, authorization: Interceptor): ApiClient {
        if (apiAuthorizations.containsKey(authName)) {
            throw RuntimeException("auth name $authName already in api authorizations")
        }
        apiAuthorizations[authName] = authorization
        clientBuilder.addInterceptor(authorization)
        return this
    }

    fun setLogger(logger: (String) -> Unit): ApiClient {
        this.logger = logger
        return this
    }

    fun <S> createService(serviceClass: Class<S>): S {
        val usedClient = this.okHttpClient ?: clientBuilder.build()
        return retrofitBuilder.client(usedClient).build().create(serviceClass)
    }

    private fun normalizeBaseUrl() {
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/"
        }
    }

    private inline fun <T, reified U> Iterable<T>.runOnFirst(callback: U.() -> Unit) {
        for (element in this) {
            if (element is U) {
                callback.invoke(element)
                break
            }
        }
    }

    companion object {
        @JvmStatic
        val defaultBasePath: String by lazy {
            System.getProperties().getProperty("com.datastax.astra.stargate_rest_v2.baseUrl", "https://-.apps.astra.datastax.com")
        }
    }
}
