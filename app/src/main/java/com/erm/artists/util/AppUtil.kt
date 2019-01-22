package com.erm.artists.util

import android.os.Looper

fun onMainThread() =  Looper.myLooper() == Looper.getMainLooper()