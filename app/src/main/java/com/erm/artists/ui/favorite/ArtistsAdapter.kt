package com.erm.artists.ui.favorite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_artist.view.*
import com.erm.artists.com.R
import com.erm.artists.data.model.entity.Artist


class ArtistsAdapter(
    var artists: MutableList<Artist>,
    val artistListener: ArtistListener
) : RecyclerView.Adapter<ArtistsAdapter.ArtistViewHolder>() {

    interface ArtistListener {
        fun onArtistClicked(artist: Artist, imageView: ImageView)
        fun onShareArtistClicked(artistEvent: Artist)
        fun toggleFavoriteStatusForArtist(artist: Artist, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val rowView = LayoutInflater.from(parent.context).inflate(R.layout.row_artist, parent, false)
        return ArtistViewHolder(rowView)
    }

    override fun getItemCount(): Int {
        return artists.count()
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        holder.bind(artists[position])
    }

    fun removeItem(position: Int) {
        artists.removeAt(position)
        notifyItemRemoved(position)
    }

    fun addItem(artist: Artist, position: Int) {
        artists.add(position, artist)
        notifyItemInserted(position)
    }

    fun updateItems(newArtists:MutableList<Artist>){
        artists = newArtists
        notifyDataSetChanged()
    }

    inner class ArtistViewHolder(val rowView: View) : RecyclerView.ViewHolder(rowView) {
        private lateinit var artist: Artist
        fun bind(artist: Artist) {
            this.artist = artist

            Picasso.get()
                .load(artist.imageUrl)
                .into(rowView.iv_artist_image)

            rowView.setOnClickListener { artistListener.onArtistClicked(artist, rowView.iv_artist_image) }
            rowView.tv_artist_name.text = artist.name
            rowView.tv_event_count.text =
                    rowView.context.getString(R.string.upcoming_events, artist.upcomingEventCount ?: 0)
            rowView.iv_share.setOnClickListener { artistListener.onShareArtistClicked(artist) }
            setupFavoriteImage(artist)
        }

        private fun setupFavoriteImage(
            artist: Artist
        ) {
            setFavoriteImageResource(artist.favorite)
            rowView.iv_favorite.setOnClickListener {
                setFavoriteImageResource(!artist.favorite)
                artistListener.toggleFavoriteStatusForArtist(artist, layoutPosition)
                artist.favorite = !artist.favorite
            }
        }

        private fun setFavoriteImageResource(favorite: Boolean) {
            if (favorite) {
                rowView.iv_favorite.setImageResource(R.drawable.ic_favorite_dark)
            } else {
                rowView.iv_favorite.setImageResource(R.drawable.ic_non_favorite_dark)
            }
        }

    }
}