package com.erm.artists.data.api.interceptor

import com.erm.artists.data.api.provider.HostSettingProvider
import okhttp3.Interceptor
import okhttp3.Response


class HostInterceptor(
    val hostSettingProvider: HostSettingProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        var setting = hostSettingProvider.setting()

        var request = chain.request()

        var url = request.url.newBuilder()
            .scheme(setting.scheme)
            .host(setting.host)
            .build()

        request = request.newBuilder()
            .url(url)
            .build()

        return chain.proceed(request)
    }

}