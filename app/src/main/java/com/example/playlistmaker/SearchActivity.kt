package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://itunes.apple.com"
const val HTTP_OK_CODE = 200

class SearchActivity : AppCompatActivity() {
    private var currentText: String = ""
    private lateinit var searchField: EditText
    private lateinit var clearButton: ImageButton
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var refreshButton: Button
    private lateinit var trackList: RecyclerView
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var searchHistory: SearchHistory
    private lateinit var historyView: LinearLayout


    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ItunesApiService::class.java)

    private var tracks = ArrayList<Track>()
    private var historyTracks = ArrayList<Track>()

    companion object {
        const val CURRENT_TEXT = "CURRENT_TEXT"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val toolbar = findViewById<Toolbar>(R.id.toolbarSearch)
        setSupportActionBar(toolbar)

        sharedPrefs = getSharedPreferences(SEARCH_HISTORY, Context.MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPrefs)
        adapter = TrackAdapter(::onHistoryTrackClick)
        historyAdapter = TrackAdapter(::onHistoryTrackClick)


        searchField = findViewById(R.id.searchField)
        clearButton = findViewById(R.id.clearButton)

        clearButton.visibility = View.INVISIBLE

        if (savedInstanceState != null) {
            currentText = savedInstanceState.getString(CURRENT_TEXT, "")
            searchField.setText(currentText)
        }
        textWatcher()

        placeholderImage = findViewById(R.id.placeholderImage)
        placeholderText = findViewById(R.id.placeholderText)
        refreshButton = findViewById(R.id.refreshBtn)

        adapter.trackList = tracks
        historyAdapter.trackList = historyTracks

        trackList = findViewById(R.id.searchResults)
        trackList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        trackList.adapter = adapter

        historyView = findViewById(R.id.searchHistory)
        val historyList = findViewById<RecyclerView>(R.id.historyList)
        val clearHistoryBtn = findViewById<Button>(R.id.clearHistory)

        historyList.adapter = historyAdapter


        searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTracks()
                true
            }
            false
        }

        searchField.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && searchField.text.isEmpty()) {
                showSearchHistory()
            } else historyView.visibility = View.INVISIBLE
        }

        searchField.requestFocus()


        searchField.doOnTextChanged { text, _, _, _ ->
            if (searchField.hasFocus() && text?.isEmpty() == true) {
                showSearchHistory()
            } else {
                historyView.visibility = View.GONE
            }
        }

        clearHistoryBtn.setOnClickListener{
            trackList.visibility = View.VISIBLE
            historyView.visibility = View.INVISIBLE
            searchHistory.clearHistory()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        currentText = searchField.text.toString()
        outState.putString(CURRENT_TEXT, currentText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentText = savedInstanceState.getString(CURRENT_TEXT, "")
        searchField.setText(currentText)
    }

    private fun textWatcher(){
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                clearButton.visibility = View.INVISIBLE
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    clearButton.visibility = View.INVISIBLE
                    currentText = ""
                } else {
                    clearButton.visibility = View.VISIBLE
                    currentText = s.toString()
                }

            }
            override fun afterTextChanged(s: Editable?) {}
        }

        searchField.addTextChangedListener(simpleTextWatcher)

        clearButton.setOnClickListener{
            searchField.setText("")
            tracks.clear()
            adapter.notifyDataSetChanged()
            hideKeyboard()
        }
    }
    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(clearButton.windowToken, 0)
    }

    private fun searchTracks() {
        iTunesService.search(currentText).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(
                call: Call<TrackResponse>,
                response: Response<TrackResponse>
            ) {
                if (response.code() == HTTP_OK_CODE){
                    tracks.clear()
                    if (response.body()?.results?.isNotEmpty() == true){
                        tracks.addAll(response.body()?.results!!)
                        adapter.notifyDataSetChanged()
                    }
                    if (tracks.isEmpty()){
                        showNotFoundError()
                    } else {
                        changePlaceholdersVisibility(shouldShow = false, shouldShowBtn = false)
                        trackList.visibility = View.VISIBLE
                    }
                } else {
                    showConnectionError()
                    refreshButton.setOnClickListener {
                        searchTracks()
                    }
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                showConnectionError()
                refreshButton.setOnClickListener {
                    searchTracks()
                }
            }
        })
    }

    private fun showConnectionError() {
        tracks.clear();
        adapter.notifyDataSetChanged()
        trackList.visibility = View.INVISIBLE
        changePlaceholdersVisibility(shouldShow = true, shouldShowBtn = true)
        placeholderText.text = getText(R.string.no_connection)
        placeholderImage.setImageResource(R.drawable.no_connection)
    }

    private fun showNotFoundError(){
        tracks.clear()
        adapter.notifyDataSetChanged()
        changePlaceholdersVisibility(shouldShow = true, shouldShowBtn = false)
        placeholderImage.setImageResource(R.drawable.not_found)
        placeholderText.text = getText(R.string.not_found)
    }

    private fun showSearchHistory() {
        changePlaceholdersVisibility(false, false)
        val tracks = searchHistory.getTracks()
        if (tracks.isNotEmpty()) {
            trackList.visibility = View.INVISIBLE
            historyView.visibility = View.VISIBLE
            historyTracks.clear()
            historyTracks.addAll(tracks)
            historyAdapter.notifyDataSetChanged()
        }
    }

    private fun changePlaceholdersVisibility(shouldShow: Boolean, shouldShowBtn: Boolean) {
        if (shouldShow) {
            placeholderImage.visibility = View.VISIBLE
            placeholderText.visibility = View.VISIBLE
        } else {
            placeholderImage.visibility = View.INVISIBLE
            placeholderText.visibility = View.INVISIBLE
        }
        if (shouldShowBtn) {
            refreshButton.visibility = View.VISIBLE
        } else {
            refreshButton.visibility = View.INVISIBLE
        }
    }

    private fun onHistoryTrackClick(track: Track) {
        historyTracks.clear()
        historyTracks.addAll(searchHistory.getTracks())
        if (historyTracks.contains(track)) {
            historyTracks.remove(track)
        } else if (historyTracks.size == HISTORY_SIZE) {
            historyTracks.removeAt(HISTORY_SIZE - 1)
        }
        historyTracks.add(0, track)
        searchHistory.addTracks(historyTracks)
    }
}