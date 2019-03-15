# MVVM Architecture Using Coroutines 
This app is built with Kotlin, using MVVM architecture & coroutines. This showcase app is meant to explore and define use cases for recently released components.

## Initial Setup
This example app consumes the [BandsInTown API](https://app.swaggerhub.com/apis/Bandsintown/PublicAPI/3.0.0)

An API key is provided, but it is recommended to use a developer's key. Once an API key is obtained via SwaggerHub then visiting Profile -> Settings -> API keys, place the key under `/Users/<username>/.gradle/gradle.properties` as `AndroidBaseKotlinMVVM_BandsInTownApiKey="<key>"` or in the project's gradle.properties

## TODO
- MVVM test suite
- Single activity architecture using Android's Navigation Library
- Explore Motion Layout
- WorkerManager
