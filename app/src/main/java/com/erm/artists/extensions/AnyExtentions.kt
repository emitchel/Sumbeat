package com.erm.artists.extensions

fun <T : Any> List<T?>.whenAllNotNull(block: (List<T>) -> Unit) {
    if (this.all { it != null }) {
        block(this.filterNotNull())
    }
}