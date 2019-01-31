package com.nerdery.artists.ui.base

import androidx.annotation.StringRes
import com.nerdery.artists.data.repository.base.Resource

/**
 * Wrapper class for easily emitting state information surrounding a resource
 * e.g. LiveData<StatefulResource<Artist>>
 */
class StatefulResource<T> {
    /**
     * Various states the resource could be in
     */
    enum class State {
        LOADING,
        SUCCESS, //doesn't guarantee hasData!
        ERROR,
        ERROR_API,
        ERROR_NETWORK
    }

    /**
     * Current state
     */
    var state = State.SUCCESS
        private set

    fun setState(state: State): StatefulResource<T> {
        this.state = state
        return this
    }

    fun isSuccessful() = state == State.SUCCESS

    fun isLoading() = state == State.LOADING

    /**
     * The corresponding resource
     */
    var resource: Resource<T>? = null

    fun getData(): T? = resource?.data

    fun hasData(): Boolean = resource?.hasData() ?: false

    /**
     * Custom message resource
     */
    @StringRes
    var message: Int? = null
        private set

    fun setMessage(@StringRes message: Int): StatefulResource<T> {
        this.message = message
        return this
    }

    companion object {
        fun <S : Any?> with(state: State, resource: Resource<S>? = null): StatefulResource<S> {
            return StatefulResource<S>().apply {
                setState(state)
                this.resource = resource
            }
        }

        fun <S : Any?> success(resource: Resource<S>? = null): StatefulResource<S> {
            return StatefulResource<S>().apply {
                setState(State.SUCCESS)
                this.resource = resource
            }
        }

        fun <S : Any?> loading(): StatefulResource<S> {
            return StatefulResource<S>().apply {
                setState(State.LOADING)
            }
        }
    }
}