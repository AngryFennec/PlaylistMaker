package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson

class PlayerActivity : AppCompatActivity() {

    private lateinit var trackCover: ImageView
    private lateinit var trackTitle: TextView
    private lateinit var trackArtist: TextView
    private lateinit var trackDuration: TextView
    private lateinit var trackAlbum: TextView
    private lateinit var trackAlbumLabel: TextView
    private lateinit var trackYear: TextView
    private lateinit var trackGenre: TextView
    private lateinit var trackCountry: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        val toolbar = findViewById<Toolbar>(R.id.toolbarPlayer)
        setSupportActionBar(toolbar)

        val jsonTrack = intent.getStringExtra("track")
        val track = Gson().fromJson(jsonTrack, Track::class.java)
        println(track)

        trackCover = findViewById(R.id.trackCover)
        trackTitle = findViewById(R.id.trackTitle)
        trackArtist = findViewById(R.id.trackArtist)
        trackDuration = findViewById(R.id.duration)
        trackAlbum = findViewById(R.id.album)
        trackAlbumLabel = findViewById(R.id.albumLabel)
        trackYear = findViewById(R.id.year)
        trackGenre = findViewById(R.id.genre)
        trackCountry = findViewById(R.id.country)

        Glide.with(this)
            .load(track.getBigArtwork())
            .error(R.drawable.track_placeholder_big)
            .placeholder(R.drawable.track_placeholder_big)
            .centerCrop()
            .transform(RoundedCorners(8))
            .into(trackCover)

        trackTitle.text = track.trackName
        trackArtist.text = track.artistName
        trackDuration.text = track.getTime()
        if (track.collectionName.isNullOrEmpty()) {
            trackAlbum.visibility = View.INVISIBLE
            trackAlbumLabel.visibility = View.INVISIBLE
        } else {
            trackAlbum.text = track.collectionName
        }
        trackYear.text = track.getYear()
        trackGenre.text = track.primaryGenreName
        trackCountry.text = track.country
    }
}