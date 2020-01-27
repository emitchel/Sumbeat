package com.erm.artists.extensions

import com.erm.artists.ArtistsApplication
import com.erm.artists.ui.base.BaseFragment

fun BaseFragment.component() = (activity?.application as ArtistsApplication).component