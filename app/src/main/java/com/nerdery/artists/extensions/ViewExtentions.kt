package com.nerdery.artists.extensions

import android.view.View
import android.view.ViewAnimationUtils
import com.nerdery.artists.constants.Duration

fun View.invisible(): View {
    visibility = View.INVISIBLE
    return this
}

fun View.visible(): View {
    visibility = View.VISIBLE
    return this
}

fun View.gone(): View {
    visibility = View.GONE
    return this
}

fun View.setVisibility(expression:Boolean){
    if(expression) {
        visible()
    } else {
        gone()
    }
}

enum class Orientation { CENTER, LEFT, RIGHT, TOP, BOTTOM }

fun View.circularReveal(
    orientation: List<Orientation> = listOf(Orientation.CENTER),
    duration: Long = Duration.MEDIUM.toLong()
) {

    val x = when {
        orientation.contains(Orientation.LEFT) -> 0
        orientation.contains(Orientation.CENTER) -> right / 2
        else -> right
    }

    val y = when {
        orientation.contains(Orientation.TOP) -> 0
        orientation.contains(Orientation.CENTER) -> bottom / 2
        else -> bottom
    }

    val startRadius = 0
    val endRadius = Math.hypot(
        width.toDouble(),
        height.toDouble()
    ).toInt()

    val anim = ViewAnimationUtils.createCircularReveal(
        this,
        x,
        y,
        startRadius.toFloat(),
        endRadius.toFloat()
    )
    anim.duration = duration
    anim.start()

}