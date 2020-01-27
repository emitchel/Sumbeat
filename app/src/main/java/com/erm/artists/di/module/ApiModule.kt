package com.erm.artists.di.module

import com.erm.artists.BuildConfig
import com.erm.artists.data.api.BandsInTownApi
import com.erm.artists.data.api.interceptor.BandsInTownInterceptor
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
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
    fun providesNetworkFlipperPlugin() = NetworkFlipperPlugin()

    @Provides
    @Singleton
    fun providesFlipperOkHttpInterceptor(networkFlipper: NetworkFlipperPlugin): FlipperOkhttpInterceptor =
        FlipperOkhttpInterceptor(networkFlipper)

    @Provides
    @Singleton
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor().apply {
        level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.NONE else HttpLoggingInterceptor.Level.BODY
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        bandsInTownInterceptor: BandsInTownInterceptor,
        flipperInterceptor: FlipperOkhttpInterceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(bandsInTownInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(flipperInterceptor)
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
        return retrofit.build()
    }

    @Singleton
    @Provides
    fun providesBandsInTownApi(retrofit: Retrofit): BandsInTownApi =
        retrofit.create(BandsInTownApi::class.java)
}