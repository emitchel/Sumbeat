package com.erm.artists.ui.events

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_event.view.*
import kotlinx.android.synthetic.main.row_lineup.view.*
import com.erm.artists.R
import com.erm.artists.constants.Duration
import com.erm.artists.data.model.relation.EventWithArtist
import com.erm.artists.extensions.gone
import com.erm.artists.extensions.visible
import com.erm.artists.ui.view.transformation.PicassoCircleTransformation
import com.erm.artists.util.DateTimeUtil
import com.erm.artists.util.ViewAnimationUtil
import org.threeten.bp.format.DateTimeFormatter


class EventsAdapter(
    var events: MutableList<EventWithArtist>,
    val artistEventsListener: ArtistEventsListener
) : RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {

    interface ArtistEventsListener {
        fun onArtistEventClicked(artistEvent: EventWithArtist)
        fun onArtistImageClicked(artistEvent: EventWithArtist, imageView: ImageView)
        fun onLineupArtistClicked(artistName: String)
        fun toggleFavoriteStatusForEvent(artistEvent: EventWithArtist, position: Int)
        fun shareEvent(artistEvent: EventWithArtist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val rowView = LayoutInflater.from(parent.context).inflate(R.layout.row_event, parent, false)
        return EventViewHolder(rowView)
    }

    override fun getItemCount(): Int {
        return events.count()
    }

    fun removeItem(position: Int) {
        events.removeAt(position)
        notifyItemRemoved(position)
    }

    fun addItem(eventWithArtist: EventWithArtist, position: Int) {
        events.add(position, eventWithArtist)
        notifyItemInserted(position)
    }

    fun updateItems(newEvents:MutableList<EventWithArtist>){
        events = newEvents
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    inner class EventViewHolder(val rowView: View) : RecyclerView.ViewHolder(rowView) {
        private lateinit var artistEvent: EventWithArtist
        fun bind(event: EventWithArtist) {
            this.artistEvent = event

            Picasso.get()
                .load(event.artist?.thumbUrl)
                .transform(PicassoCircleTransformation())
                .into(rowView.iv_artist_image)
            rowView.iv_artist_image.setOnClickListener {
                artistEventsListener.onArtistImageClicked(
                    artistEvent,
                    rowView.iv_artist_image
                )
            }

            rowView.setOnClickListener { artistEventsListener.onArtistEventClicked(artistEvent) }
            rowView.tv_artist_name.text = event.artist?.name
            rowView.tv_date.text = DateTimeFormatter.ofPattern(DateTimeUtil.DEFAULT_DATE_FORMAT)
                .format(event.artistEvent?.dateTime)
            rowView.iv_share.setOnClickListener { artistEventsListener.shareEvent(artistEvent) }
            setupLocation(event)
            setupFavoriteImage(event)
            setupLineup(event)
        }

        private fun setupLocation(event: EventWithArtist) {
            rowView.tv_location.text = rowView.context.getString(
                R.string.event_location,
                event.artistEvent?.venue?.name,
                event.artistEvent?.venue?.city,
                event.artistEvent?.venue?.region
            )
            if (event.artistEvent?.venue?.region.isNullOrBlank()) {
                rowView.tv_location.text = rowView.tv_location.text.removeSuffix(", ")
            }
        }

        private fun setupLineup(event: EventWithArtist) {
            rowView.btn_lineup.setOnClickListener {
                //should only get clicked if visible (determined below)
                if (rowView.ll_lineup.height <= ViewAnimationUtil.DEFAULT_COLLAPSED_HEIGHT) {
                    ViewAnimationUtil.expand(rowView.ll_lineup, Duration.SHORT, false)
                } else {
                    ViewAnimationUtil.collapse(rowView.ll_lineup, Duration.SHORT, rowView.ll_lineup_placeholder.height)
                }
            }

            val uniqueLineup = event.artistEvent?.lineup?.filter { !it.equals(event.artist!!.name, true) }
            if (uniqueLineup?.isNotEmpty() == true) {
                rowView.ll_lineup.removeAllViews()
                rowView.btn_lineup.visible()
                val inflater = LayoutInflater.from(rowView.context)
                uniqueLineup.forEach { lineupName ->
                    val lineupRow = inflater.inflate(R.layout.row_lineup, rowView.ll_lineup, false)
                    lineupRow.tv_lineup_artist_name.text = lineupName
                    lineupRow.cl_wrapper.setOnClickListener {
                        artistEventsListener.onLineupArtistClicked(lineupName)
                    }
                    rowView.ll_lineup.addView(lineupRow)
                }

            } else {
                //immediate hide the lineup
                val params = rowView.ll_lineup.layoutParams
                params.height = 0
                rowView.ll_lineup.layoutParams = params
                rowView.ll_lineup.removeAllViews()
                rowView.btn_lineup.gone()
            }
        }

        private fun setupFavoriteImage(
            event: EventWithArtist
        ) {
            setFavoriteImageResource(event.artistEvent!!.favorite)
            rowView.iv_favorite.setOnClickListener {
                setFavoriteImageResource(!event.artistEvent!!.favorite)
                artistEventsListener.toggleFavoriteStatusForEvent(event, layoutPosition)
                event.artistEvent!!.favorite = !event.artistEvent?.favorite!!
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