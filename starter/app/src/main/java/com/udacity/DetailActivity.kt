package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        var intentExtras = intent.extras
        val notificationManager = ContextCompat.getSystemService(this, NotificationManager::class.java) as NotificationManager

        if (intentExtras != null) {
            download_file_name.text = intentExtras.getString("filename")
            download_status.text = intentExtras.getString("downloadstatus")
            notificationManager.cancel(intentExtras.getInt("notificationid"))
        }

        notification_button.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }

}
