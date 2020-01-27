package com.erm.artists.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
    fun launch(blockScope: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(block = blockScope)

    fun async(blockScope: suspend CoroutineScope.() -> Unit) =
        viewModelScope.async(block = blockScope)
}