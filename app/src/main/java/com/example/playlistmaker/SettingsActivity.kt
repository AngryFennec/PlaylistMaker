package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val toolbar = findViewById<Toolbar>(R.id.toolbarSettings)
        setSupportActionBar(toolbar)

        val shareBtn = findViewById<Button>(R.id.textShare)
        val supportBtn = findViewById<Button>(R.id.textSupport)
        val termsBtn = findViewById<Button>(R.id.textTerms)

        shareBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = getString(R.string.text_plain)
            val  appUrl = getString(R.string.yap_link)
            intent.putExtra(Intent.EXTRA_TEXT, appUrl)
            startActivity(intent)
        }

        supportBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse(getString(R.string.dev_mail))

            intent.putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.mail_title)
            )
            intent.putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.mail_text)
            )
            startActivity(intent)
        }

        termsBtn.setOnClickListener {
            val termsUrl = getString(R.string.yap_offer)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(termsUrl))
            startActivity(intent)
        }
    }
}