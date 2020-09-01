package com.erm.artists.data.api.provider

import androidx.lifecycle.LiveData

class HostSetting(
    var scheme: String,
    var host: String
) {}

interface HostSettingProvider {

    fun setting(): HostSetting

}