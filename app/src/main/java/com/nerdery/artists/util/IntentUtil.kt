package com.nerdery.artists.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import timber.log.Timber

object IntentUtil {
    /**
     * Return true if opened
     */
    fun openWebPageFromUrl(context: Context, url: String): Boolean {
        return try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            true
        } catch (e: Exception) {
            Timber.e(e)
            false
        }
    }

    fun shareString(context: Context, data: String, @StringRes title:Int): Boolean {
        return try {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, data)
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(sendIntent, data))
            true
        } catch (e: Exception) {
            Timber.e(e)
            false
        }
    }
}