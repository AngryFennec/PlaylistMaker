package com.example.playlistmaker

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class Track (
    val trackName: String,
    val artistName: String,
    @SerializedName("trackTimeMillis") val trackTime: Int,
    val artworkUrl100: String,
    val trackId: Int,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String
) {
    fun getBigArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
    fun getYear() = releaseDate.substring(0, 4)
    fun getTime() = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTime)
}

