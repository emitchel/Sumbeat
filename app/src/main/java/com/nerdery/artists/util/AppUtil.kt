package com.nerdery.artists.util

import android.os.Looper

fun onMainThread() =  Looper.myLooper() == Looper.getMainLooper()