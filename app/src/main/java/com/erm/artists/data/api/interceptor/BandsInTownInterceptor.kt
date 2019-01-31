package com.erm.artists.data.api.interceptor

import com.erm.artists.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class BandsInTownInterceptor : Interceptor {
    companion object {
        const val APP_ID = "app_id"
        const val ACCEPT = "Accept"
        const val APPLICATION_JSON = "application/json"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain.request()
            .url()
            .newBuilder()
            .addQueryParameter(APP_ID, BuildConfig.BandsInTownApiKey)
            .build()
        val request = chain.request()
            .newBuilder()
            .addHeader(
                ACCEPT,
                APPLICATION_JSON
            )
            .url(url)
            .build()

        return chain.proceed(request)
    }

}