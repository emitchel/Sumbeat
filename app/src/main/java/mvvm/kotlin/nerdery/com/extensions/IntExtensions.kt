package mvvm.kotlin.nerdery.com.extensions

import android.view.KeyEvent

fun Int.isEnterKeyCode(): Boolean {
    return this == KeyEvent.KEYCODE_ENTER
}

fun Int.inBetween(inclusive: Int, exclusive: Int): Boolean {
    return this in inclusive..(exclusive - 1)
}