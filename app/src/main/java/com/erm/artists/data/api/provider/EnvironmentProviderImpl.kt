package com.erm.artists.data.api.provider


class EnvironmentProviderImpl(
    override var environment: EnvironmentProvider.Environment = EnvironmentProvider.Environment.LIVE
) : EnvironmentProvider {



}