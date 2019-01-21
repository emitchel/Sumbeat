# Android Kotlin MVVM Example

## Initial Setup
This example app consumes the [BandsInTown API](https://app.swaggerhub.com/apis/Bandsintown/PublicAPI/3.0.0)

An API key is provided, but it is recommended to use a developer's key. Once an API key is obtained via logging into SwaggerHub then visiting Profile -> Settings -> API keys, place the key under /Users/<username>/.gradle/gradle.properties as `AndroidBaseKotlinMVVM_BandsInTownApiKey="<key>"` or in the project's gradle.properties

### These phases are meant to show an anti pattern app progress to MVVM with coroutines
The master commit history will show Phases 2 & 3 merged in, whereas Phase 1 is part of the initial implementation and has no PR.

### Phase 1
Develop a skin and bones app that holds no view pattern, consuming a simple but useful API

### Phase 2
Build out the repository layer using Room, Retrofit and Coroutines.

Bind Api Responses to stored models

Create a repository helper (DataFetchHelper) to provide various kinds of data fetching e.g. fetch local until stale, only fetch from network, fetch from network until network fail, etc.

Use deferred API responses with Retrofit

### Phase 3
Create a resource listener to dissect a provided API resource and easily build a UI response from it

Define ViewModels to compliment feature verticals

Use Dagger to inject dependencies to all ViewModels.

Build out rest of UI

#### TODO 
Implement Instrumented testing to compliment MVVM architecture

