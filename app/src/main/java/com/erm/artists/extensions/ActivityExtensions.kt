package com.erm.artists.extensions

import com.erm.artists.ArtistsApplication
import com.erm.artists.ui.base.BaseActivity

fun BaseActivity.component() = (application as ArtistsApplication).component