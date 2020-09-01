package com.erm.artists.data.api.provider

interface EnvironmentProvider {

    var environment: Environment

    enum class Environment { DEV, STG, LIVE }

}