package com.erm.artists.data.repository.helpers

import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import com.erm.artists.data.model.entity.base.ReflectsApiResponse
import com.erm.artists.data.repository.base.Resource
import com.erm.artists.data.repository.helpers.DataFetchHelper.DataFetchStyle
import com.erm.artists.data.repository.helpers.DataFetchHelper.DataFetchStyle.*
import com.erm.artists.util.RepositoryUtil
import com.erm.artists.util.onMainThread
import kotlinx.coroutines.*
import retrofit2.Response
import timber.log.Timber

/**
 * This helper class encapsulates some of the common data fetch styles (defined here [DataFetchStyle])
 *
 * Depending on the style chosen, some or all methods may be required for the functionality to work correctly
 *
 * [DataFetchHelper.fetchDataAsync] returns a deferred async which should be consumed at the view model layer
 *
 * @param T The data type this class is fetching
 * @property tag String A name for the fetcher for logging reasons
 * @property dataFetchStyle DataFetchStyle? The Style this fetcher should respect
 * @property sharedPreferences SharedPreferences? Required for local cache styles
 * @property cacheKey CacheKey? A key to describe your cache in memory, only for local cache styles
 * @property cacheDescriptor String? A description of the key (e.g. not all artists should share the same cache,
 * so use the artist's name as a descriptor)
 * @property cacheLengthSeconds Long How long the cache should be stored, only for local cache styles
 *
 */
abstract class DataFetchHelper<T>(
    val tag: String,
    private val dataFetchStyle: DataFetchStyle? = DataFetchStyle.LOCAL_FIRST_NETWORK_REFRESH_ALWAYS,
    private val sharedPreferences: SharedPreferences? = null,
    private val cacheKey: String? = null,
    private val cacheDescriptor: String? = "",
    private val cacheLengthSeconds: Long = 0L
) {

    /**
     * This describes a specific data call's approach to fetching data
     * Resulting [Resource] will contain a corresponding [Result]
     * which describes the fetch result
     */
    enum class DataFetchStyle {


        /**
         * DEFAULT
         *
         * Fetch from local storage,
         * but always follow up with network call to store fresh data
         *
         * Pros: Depends on local data, always refreshes in the background
         * Cons: Maximizing network calls, network call must finish before local data is returned
         * TODO return local data immediately, asynchronously refresh
         */
        LOCAL_FIRST_NETWORK_REFRESH_ALWAYS,

        /**
         * Fetch from local storage until it's stale (e.g. cache expired) then pull from network to restore
         *
         * Pros: Cache flexible allowing you to get minimize network calls,
         * should be instantaneous most of the time, fresh network calls failover
         * to local storage.
         * Cons: Fresh data isn't obvious to end users
         */
        LOCAL_FIRST_UNTIL_STALE,

        /**
         *
         * Attempt network first, if poor/no network, failover to local storage
         *
         * Pros: Dependable failover, Always fresh data when possible
         * Cons: Maximizing network calls per request
         */
        NETWORK_FIRST_LOCAL_FAILOVER,

        /**
         * Only fetch from network
         *
         * Pros: No local storage required, Always fresh data when possible
         * Cons: No failover mechanism, Maximizing network calls per request
         */
        NETWORK_ONLY,

        /**
         * Only fetch from local storage (db, sharedprefs, etc.)
         *
         * Pros: No network required, virtually instantaneous
         * Cons: No ability to refresh against remote (May not be a problem)
         */
        LOCAL_ONLY;

        /**
         * This describes how the data was fetched, in correspondence to the call's
         * fetch style see [DataFetchStyle]
         */
        enum class Result {
            /**
             * DEFAULT
             *
             * No data was fetched due to either network failure, local failure, or both.
             *
             * This can also be used when [DataFetchStyle.LOCAL_ONLY] is specified
             * and there's no local data
             */
            NO_FETCH,

            /**
             * Fetched from local because network failed
             * [DataFetchStyle.NETWORK_FIRST_LOCAL_FAILOVER]
             */
            LOCAL_DATA_NETWORK_FAIL,

            /**
             * Fetched from fresh local
             * [DataFetchStyle.LOCAL_FIRST_UNTIL_STALE]
             */
            LOCAL_DATA_FRESH,

            /**
             * Fetched from local
             * [DataFetchStyle.LOCAL_FIRST_NETWORK_REFRESH_ALWAYS]
             */
            LOCAL_DATA_FIRST,

            /**
             * Fetched from local because that's the only source it can pull from
             * [DataFetchStyle.LOCAL_ONLY]
             */
            LOCAL_DATA_ONLY,

            /**
             * Fetched from network because that's the only source it can pull from
             * [DataFetchStyle.NETWORK_ONLY]
             */
            NETWORK_DATA_ONLY,

            /**
             * Fetched from network because a connection was active
             * [DataFetchStyle.NETWORK_FIRST_LOCAL_FAILOVER]
             */
            NETWORK_DATA_FIRST,

            /**
             * Fetched from network because local was stale
             * [DataFetchStyle.LOCAL_FIRST_UNTIL_STALE]
             */
            NETWORK_DATA_LOCAL_STALE,

            /**
             * Fetched from network because local hasn't been populated yet
             */
            NETWORK_DATA_LOCAL_MISSING
        }
    }

    /**
     * Inner classes to compliment specific styles
     */
    abstract class LocalOnly<S>(
        tag: String
    ) : DataFetchHelper<S>(
        tag,
        LOCAL_ONLY
    ) {
        abstract override suspend fun getDataFromLocal(): S
    }

    abstract class NetworkOnly<S>(
        tag: String
    ) : DataFetchHelper<S>(
        tag,
        NETWORK_ONLY
    ) {
        abstract override suspend fun getDataFromNetwork(): Response<out Any?>
        abstract override suspend fun convertApiResponseToData(response: Response<out Any?>): S
    }

    abstract class LocalFirstNetworkRefreshAlways<S>(
        tag: String,
        sharedPreferences: SharedPreferences,
        cacheKey: String,
        cacheDescriptor: String,
        cacheLengthSeconds: Long
    ) : DataFetchHelper<S>(
        tag,
        LOCAL_FIRST_NETWORK_REFRESH_ALWAYS,
        sharedPreferences,
        cacheKey,
        cacheDescriptor,
        cacheLengthSeconds
    ) {
        abstract override suspend fun getDataFromLocal(): S?
        abstract override suspend fun getDataFromNetwork(): Response<out Any?>
        abstract override suspend fun convertApiResponseToData(response: Response<out Any?>): S
        abstract override suspend fun storeFreshDataToLocal(data: S): Boolean
    }

    abstract class LocalFirstUntilStale<S>(
        tag: String,
        sharedPreferences: SharedPreferences,
        cacheKey: String,
        cacheDescriptor: String,
        cacheLengthSeconds: Long
    ) : DataFetchHelper<S>(
        tag,
        LOCAL_FIRST_UNTIL_STALE,
        sharedPreferences,
        cacheKey,
        cacheDescriptor,
        cacheLengthSeconds
    ) {
        abstract override suspend fun getDataFromLocal(): S?
        abstract override suspend fun getDataFromNetwork(): Response<out Any?>
        abstract override suspend fun convertApiResponseToData(response: Response<out Any?>): S
        abstract override suspend fun storeFreshDataToLocal(data: S): Boolean
    }

    abstract class NetworkFirstLocalFailover<S>(
        tag: String
    ) : DataFetchHelper<S>(
        tag,
        NETWORK_FIRST_LOCAL_FAILOVER
    ) {
        abstract override suspend fun getDataFromLocal(): S
        abstract override suspend fun getDataFromNetwork(): Response<out Any?>
        abstract override suspend fun convertApiResponseToData(response: Response<out Any?>): S
        abstract override suspend fun storeFreshDataToLocal(data: S): Boolean
    }

    /**
     * Fetch data from a local resource
     * @return T?
     */
    @WorkerThread
    open suspend fun getDataFromLocal(): T? {
        throw NotImplementedError("getDataFromLocal should be implemented to support $dataFetchStyle")
    }

    /**
     * @return Response<out Any> - Since the model we're fetching may not match the api response, out Any
     * see: [ReflectsApiResponse]
     */
    @WorkerThread
    open suspend fun getDataFromNetwork(): Response<out Any?> {
        throw NotImplementedError("getDataFromNetwork should be implemented to support $dataFetchStyle")
    }

    /**
     * Optionally Provide a conversion from an API response to the required type
     *
     * see: [ReflectsApiResponse]
     * @param response Response<Any>
     * @return T
     */
    open suspend fun convertApiResponseToData(response: Response<out Any?>): T {
        try {
            return response.body() as T
        } catch (e: Exception) {
            throw ClassCastException(
                "$e - Cannot convert ${response.body()!!::class.java.simpleName} to Data Fetch type, " +
                        "override this method to provide conversion."
            )
        }
    }

    /**
     * Store the data for future retrieval
     * @param data T
     * @return If data was stored or not
     */
    open suspend fun storeFreshDataToLocal(data: T): Boolean {
        throw NotImplementedError("storeFreshDataToLocal should be implemented to support $dataFetchStyle")
    }

    /**
     * Perform an operation after the data has been fetched
     * e.g. Update historical record after successful fetch
     */
    open suspend fun operateOnDataPostFetch(data: T) {
        //Optional
    }

    /**
     * Fetch resource immediately, under a managed coroutine outside of this class
     *
     * Must be on a worker thread (IO Dispatcher) if a network and/or local data transaction takes place
     * @return Resource<T>
     */
    suspend fun fetchData(): Resource<T> {
        return fetchDataByStyle(sharedPreferences, cacheKey)
    }

    /**
     * Receive a deferred value, guarantees to not be on main thread
     * @return Deferred<Resource<T>>
     */
    suspend fun fetchDataIOAsync(): Deferred<Resource<T>> = withContext(Dispatchers.IO) {
        async { fetchDataByStyle(sharedPreferences, cacheKey) }
    }

    private suspend fun fetchDataByStyle(
        sharedPreferences: SharedPreferences?,
        cacheKey: String?
    ): Resource<T> {
        val resource = Resource<T>()
        resource.dataFetchStyleResult = Result.NO_FETCH
        resource.dataFetchStyle = dataFetchStyle ?: NETWORK_FIRST_LOCAL_FAILOVER

        if (onMainThread()) {
            throw IllegalThreadStateException("Cannot perform Network nor Local storage transactions on main thread!")
        }

        when (dataFetchStyle) {
            NETWORK_FIRST_LOCAL_FAILOVER -> {
                resource.data = refreshDataFromNetwork(resource, NETWORK_FIRST_LOCAL_FAILOVER)
                if (resource.data == null) {
                    log("Unable to get data from network, failing over to local")
                    resource.data = getDataFromLocal()
                    resource.fresh = false
                    resource.dataFetchStyleResult = Result.LOCAL_DATA_NETWORK_FAIL
                } else {
                    resource.fresh = true
                    resource.dataFetchStyleResult = Result.NETWORK_DATA_FIRST
                }
            }
            NETWORK_ONLY -> {
                resource.data = refreshDataFromNetwork(resource, NETWORK_ONLY)
                resource.fresh = true
                resource.dataFetchStyleResult = Result.NETWORK_DATA_ONLY
            }
            LOCAL_ONLY -> {
                resource.data = getDataFromLocal()
                resource.fresh = true
                resource.dataFetchStyleResult = Result.LOCAL_DATA_ONLY
            }
            LOCAL_FIRST_NETWORK_REFRESH_ALWAYS -> {
                resource.data = getDataFromLocal()
                //TODO:
                //always refreshing following it
                val dataFromNetwork =
                    refreshDataFromNetwork(resource, LOCAL_FIRST_NETWORK_REFRESH_ALWAYS)
                if (resource.data == null) {
                    log("Local data was empty, so returning data from network first")
                    resource.data = dataFromNetwork
                    resource.dataFetchStyleResult = Result.NETWORK_DATA_LOCAL_MISSING
                } else {
                    log("Returning local data first, refreshing in background")
                    resource.dataFetchStyleResult = Result.LOCAL_DATA_FIRST
                }
                resource.fresh = true
            }
            LOCAL_FIRST_UNTIL_STALE -> {
                if (cacheKey == null || sharedPreferences == null) {
                    throw IllegalArgumentException("Cache key and shared preferences required for caching capabilities")
                }
                if (RepositoryUtil.isCacheStale(sharedPreferences, cacheKey, cacheDescriptor, cacheLengthSeconds)) {
                    log("Cache is stale")
                    resource.data = refreshDataFromNetwork(resource, LOCAL_FIRST_UNTIL_STALE)
                    if (resource.data == null) {
                        log("Unsuccessfully stored fresh data from network, getting stale data from local")
                        resource.data = getDataFromLocal()
                        resource.fresh = false
                        resource.dataFetchStyleResult = Result.LOCAL_DATA_NETWORK_FAIL
                    } else {
                        log("Successfully stored fresh data from network")
                        resource.fresh = true
                        resource.dataFetchStyleResult = Result.NETWORK_DATA_LOCAL_STALE
                        RepositoryUtil.resetCache(sharedPreferences, cacheKey, cacheDescriptor)
                    }
                } else {
                    log("Cache isn't stale")
                    resource.data = getDataFromLocal()
                    resource.fresh = true
                    resource.dataFetchStyleResult = Result.LOCAL_DATA_FRESH
                }
            }
        }
        resource.data?.let {
            operateOnDataPostFetch(it)
        }
        return resource
    }

    /**
     * Get data from network, attempt to convert it for storage, then store it locally
     * @param resource the resource to manipulate given certain circumstances
     * @param dataFetchStyle the data fetch style that this resource corresponds to
     * @return T? - Newly stored data
     */
    private suspend fun refreshDataFromNetwork(resource: Resource<T>, dataFetchStyle: DataFetchStyle): T? {
        //Some styles depend on data getting stored locally, gently throw a log error to let them know
        val forceStoreLocally = arrayListOf(
            NETWORK_FIRST_LOCAL_FAILOVER,
            LOCAL_FIRST_NETWORK_REFRESH_ALWAYS,
            LOCAL_FIRST_UNTIL_STALE
        ).contains(dataFetchStyle)

        val response: Response<out Any?>
        var convertedToData: T? = null
        var storedFreshData = false
        try {
            response = getDataFromNetwork() //this can throw an exception IOException, Timeouts,
            resource.response = response
            log("Got data from network")
            if (resource.response?.body() == null) {
                resource.errorMessage =
                        "Response body was null! Verify correct response object was used for service call"
                Timber.e(resource.errorMessage)
                //if body was null, no point in attempting to convert it or store it
                return null
            }

            convertedToData = convertApiResponseToData(response)
            log("Converted data for storage")
            storedFreshData = storeFreshDataToLocal(convertedToData)
        } catch (e: Exception) {
            //IOExceptions, conversion exceptions, or local storage exception
            resource.throwable = e
            resource.errorMessage = "Unable to refresh data for request type $tag, due to exception $e"
            Timber.e(resource.errorMessage)
        } finally {
            if (forceStoreLocally && !storedFreshData) {
                Timber.e("$dataFetchStyle requires data to be stored locally for the style to work correctly!")
            }
        }
        return convertedToData
    }

    private fun log(message: String) {
        Timber.i("$tag - $message")
    }
}
