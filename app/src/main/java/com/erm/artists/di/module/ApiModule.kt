package com.erm.artists.di.module

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import com.erm.artists.com.BuildConfig
import com.erm.artists.data.api.BandsInTownApi
import com.erm.artists.data.api.interceptor.BandsInTownInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
class ApiModule {

    @Singleton
    @Provides
    fun providesBandsInTownInterceptor(): BandsInTownInterceptor =
        BandsInTownInterceptor()

    @Singleton
    @Provides
    fun provideMoshi(): Moshi {
        val moshi = Moshi.Builder()
        return moshi.build()
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(bandsInTownInterceptor: BandsInTownInterceptor): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(bandsInTownInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun providesBandsInTownRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit {
        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.BandsInTownUrl)
            .addConverterFactory(
                MoshiConverterFactory
                    .create(moshi)
            )
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
        return retrofit.build()
    }

    @Singleton
    @Provides
    fun providesBandsInTownApi(retrofit: Retrofit): BandsInTownApi =
        retrofit.create(BandsInTownApi::class.java)
}