package com.erm.artists.di.module

import com.erm.artists.BuildConfig
import com.erm.artists.data.api.BandsInTownApi
import com.erm.artists.data.api.interceptor.BandsInTownInterceptor
import com.erm.artists.data.api.interceptor.HostInterceptor
import com.erm.artists.data.api.provider.*
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
    fun providesEnvironmentProvider(): EnvironmentProvider = EnvironmentProviderImpl()

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

    /* Shared OkHttpClient */
    @Singleton
    @Provides
    fun provideOkHttpClient(
        flipperInterceptor: FlipperOkhttpInterceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(flipperInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder().build()
    }


    @Singleton
    @Provides
    fun providesNetworkFlipperPlugin() = NetworkFlipperPlugin()



    @Singleton
    @Provides
    fun providesBandsInTownRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi,
        environmentProvider: EnvironmentProvider
    ): Retrofit {

        // Customize shared okhttp client
        var client: OkHttpClient =  okHttpClient.newBuilder()
            .addInterceptor(BandsInTownInterceptor())
            .addInterceptor(
                HostInterceptor(
                    HostSettingProviderImpl(
                        HostSetting("https", "rest.bandsintown.com"), // DEV
                        HostSetting("https", "rest.bandsintown.com"), // STG
                        HostSetting("https", "rest.bandsintown.com"), // LIVE
                        environmentProvider
                    )
                )
            )
            .build()

        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.BandsInTownUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))

        return retrofit.build()
    }
    @Singleton
    @Provides
    fun providesBandsInTownApi(retrofit: Retrofit): BandsInTownApi =
        retrofit.create(BandsInTownApi::class.java)
}