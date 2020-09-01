package com.erm.artists.data.api.provider

import javax.inject.Inject

class HostSettingProviderImpl(
    var dev: HostSetting,
    var stg: HostSetting,
    var live: HostSetting,
    var environmentProvider: EnvironmentProvider
) : HostSettingProvider {

    override fun setting(): HostSetting {
        return when(environmentProvider.environment) {
            EnvironmentProvider.Environment.DEV -> dev
            EnvironmentProvider.Environment.STG -> stg
            EnvironmentProvider.Environment.LIVE -> live
            else -> live
        }
    }

}