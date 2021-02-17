package com.udacity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        var intentExtras = intent.extras

        if (intentExtras != null) {
            Log.d("DETAILACTIVITY", "Filename: ${intentExtras.getString("filename")}")
            Log.d("DETAILACTIVITY", "Status: ${intentExtras.getString("downloadstatus")}")
        }

        notification_button.setOnClickListener {
            val intent =Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }

}
