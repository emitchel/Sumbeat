package com.nerdery.artists.data.repository.base

import com.nerdery.artists.data.repository.helpers.DataFetchHelper
import retrofit2.Response
import java.io.IOException

/**
 * A flexible resource wrapper to gather and propagate properties associated to it
 * @param T The type of data is wraps
 */
class Resource<T> {
    /**
     * The data corresponding to the resource
     */
    var data: T? = null
    /**
     * A response to carry to the consumer
     */
    var response: Response<out Any?>? = null

    /**
     * An exception that can be consumed
     */
    var throwable: Throwable? = null

    /**
     * Describes how this recourse was fetched
     */
    var dataFetchStyle =
        DataFetchHelper.DataFetchStyle.NETWORK_FIRST_LOCAL_FAILOVER

    /**
     * Describes the resulting fetch style
     * Allows consumer to specify that the data is not fresh because of network, etc.
     */
    var dataFetchStyleResult = DataFetchHelper.DataFetchStyle.Result.NO_FETCH

    /**
     * If the resource contains data
     */
    fun hasData() = data != null

    /**
     * If this resource is considered fresh (in correspondence to [DataFetchHelper.DataFetchStyle])
     */
    var fresh: Boolean = false

    /**
     * Non locale safe error message to propagate
     */
    var errorMessage: String? = null

    /**
     * If a network error happened during the fetch
     * Doesn't necessarily mean "fresh" data wasn't received, see [DataFetchHelper.DataFetchStyle.LOCAL_FIRST_NETWORK_REFRESH_ALWAYS]
     */
    fun isNetworkIssue(): Boolean = throwable is IOException

    /**
     * If an api issue happened during the fetch
     * Again, doesn't necessarily mean "fresh" data wasn't received
     * e.g. 404 isNotFound(), 5XX Service Error, etc.
     */
    fun isApiIssue(): Boolean = !(response?.isSuccessful ?: true)

    /**
     * Copy the resource to a new data type (or the same)
     * @param newData S
     * @return Resource<S>
     */
    fun <S : Any?> copy(newData: S): Resource<S> {
        return Resource<S>().apply {
            data = newData
            response = this@Resource.response
            throwable = this@Resource.throwable
            dataFetchStyle = this@Resource.dataFetchStyle
            dataFetchStyleResult = this@Resource.dataFetchStyleResult
            fresh = this@Resource.fresh
            errorMessage = this@Resource.errorMessage
        }
    }
}