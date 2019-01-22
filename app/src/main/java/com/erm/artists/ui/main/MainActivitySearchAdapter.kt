package com.erm.artists.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter
import kotlinx.android.synthetic.main.main_search_history_row.view.*
import com.erm.artists.com.R
import com.erm.artists.data.model.entity.Artist
import com.erm.artists.extensions.whenAllNotNull

class MainActivitySearchAdapter(
    val context: Context,
    layoutInflater: LayoutInflater
) : SuggestionsAdapter<Artist, MainActivitySearchAdapter.ViewHolder>(layoutInflater) {

    interface OnItemSelectedListener {
        fun onSuggestionClicked(artist: Artist)
    }

    var onItemSelectedListener: OnItemSelectedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(layoutInflater.inflate(R.layout.main_search_history_row, parent, false))
    }

    override fun getSingleViewHeight(): Int {
        return (context.resources.getDimension(R.dimen.search_history_row_height).toInt() / context.resources.displayMetrics.density).toInt()
    }

    override fun onBindSuggestionHolder(artist: Artist?, viewHolder: ViewHolder?, position: Int) {
        listOf(artist, viewHolder).whenAllNotNull {
            viewHolder!!.bind(artist!!)
        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(artist: Artist) {
            view.tv_artist_name.text = artist.name
            view.row_wrapper.setOnClickListener {
                onItemSelectedListener?.onSuggestionClicked(artist)
            }
        }
    }
}