package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.widget.Toolbar

class SearchActivity : AppCompatActivity() {
    private var currentText: String = ""
    private lateinit var searchField: EditText
    private lateinit var clearButton: ImageButton
    companion object {
        const val CURRENT_TEXT = "CURRENT_TEXT"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val toolbar = findViewById<Toolbar>(R.id.toolbarSearch)
        setSupportActionBar(toolbar)

        searchField = findViewById<EditText>(R.id.searchField)
        clearButton = findViewById<ImageButton>(R.id.clearButton)

        clearButton.visibility = View.INVISIBLE

        if (savedInstanceState != null) {
            currentText = savedInstanceState.getString(CURRENT_TEXT, "")
            searchField.setText(currentText)
        }
        textWatcher()

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
                } else {
                    clearButton.visibility = View.VISIBLE
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        searchField.addTextChangedListener(simpleTextWatcher)

        clearButton.setOnClickListener{
            searchField.setText("")
            hideKeyboard()
        }
    }
    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(clearButton.windowToken, 0)
    }
}