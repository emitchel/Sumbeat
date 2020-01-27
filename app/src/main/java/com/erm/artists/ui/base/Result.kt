package com.erm.artists.ui.base

import androidx.annotation.StringRes
import com.erm.artists.R
import com.erm.artists.data.repository.helpers.DataFetchHelper
import retrofit2.Response

sealed class Result<out T>(@StringRes var message : Int? = null) {
    object Loading : Result<Nothing>()

    data class NetworkError(
        val throwable: Throwable?,
        val loggableMessage: String? = null
    ) :
        Result<Nothing>(R.string.no_network_connection)

    data class ApiError(
        val throwable: Throwable?,
        val loggableMessage: String? = null
    ) :
        Result<Nothing>(R.string.service_error)

    data class Success<T>(
        val data: T?,
        val response: Response<out Any?>?,
        val freshData: Boolean,
        val dataFetchStyle: DataFetchHelper.DataFetchStyle,
        val dataFetchStyleResult: DataFetchHelper.DataFetchStyle.Result
    ) : Result<T>()
}