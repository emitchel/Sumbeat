package mvvm.kotlin.nerdery.com.util

import android.os.Looper

fun onMainThread() =  Looper.myLooper() == Looper.getMainLooper()