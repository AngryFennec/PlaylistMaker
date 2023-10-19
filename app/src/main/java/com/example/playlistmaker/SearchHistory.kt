package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

const val SEARCH_HISTORY = "search_history"
const val HISTORY_SIZE = 10

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    fun clearHistory() {
        sharedPreferences.edit()
            .remove(SEARCH_HISTORY)
            .apply()
    }

    fun getTracks(): MutableList<Track> {
        val json = sharedPreferences.getString(SEARCH_HISTORY, null)
        val jsonG = GsonBuilder().create()
        return jsonG.fromJson(json, object: TypeToken<MutableList<Track>>() {} .type) ?: mutableListOf()
    }

    fun addTrack(track: Track, index: Int) {
        val editor = sharedPreferences.edit()
        val json = sharedPreferences.getString(SEARCH_HISTORY, null)
        val gson = Gson()

        val currentHistory: MutableList<Track> =
            gson.fromJson(json, object : TypeToken<MutableList<Track>>() {}.type) ?: mutableListOf()

        currentHistory.removeAll { it.trackId == track.trackId }

        currentHistory.add(index, track)
        while (currentHistory.size > HISTORY_SIZE) {
            currentHistory.removeAt(currentHistory.lastIndex)
        }

        val jsonHistory = gson.toJson(currentHistory)
        editor.putString(SEARCH_HISTORY, jsonHistory)
        editor.apply()
    }

    fun addTracks(trackList: List<Track>) {
        val editor = sharedPreferences.edit()
        val json = sharedPreferences.getString(SEARCH_HISTORY, null)
        val gson = Gson()
        var currentHistory = ArrayList<Track>()
        trackList.forEach{
            currentHistory.add(it)
        }
        while (currentHistory.size > HISTORY_SIZE) {
            currentHistory.removeAt(currentHistory.lastIndex)
        }
        val jsonHistory = gson.toJson(currentHistory)

        editor.putString(SEARCH_HISTORY, jsonHistory)
        editor.apply()
    }
}