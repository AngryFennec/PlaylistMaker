package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val trackNameTextView: TextView = itemView.findViewById(R.id.track_name)
    private val artistNameTextView: TextView = itemView.findViewById(R.id.artist_name)
    private val trackTimeTextView: TextView = itemView.findViewById(R.id.track_time)
    private val artworkImageView: ImageView = itemView.findViewById(R.id.artwork_url_100)

    fun bind(track: Track) {
        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        trackTimeTextView.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime)

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .error(R.drawable.track_placeholder)
            .placeholder(R.drawable.track_placeholder)
            .centerCrop()
            .transform(RoundedCorners(10))
            .into(artworkImageView)
    }
}