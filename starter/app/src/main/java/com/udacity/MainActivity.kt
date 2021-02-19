package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    lateinit var downloadManager: DownloadManager
    private var selectedUrl: String = ""
    private var selectedRadioButton: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        createChannel(getString(R.string.notification_channel_id), CHANNEL_ID)

        custom_button.setOnClickListener {
            custom_button.buttonState = ButtonState.Clicked
            selectedRadioButton = radio_group.checkedRadioButtonId
            selectedUrl = when(selectedRadioButton) {
                R.id.radioButton1 -> URL_GLIDE
                R.id.radioButton2 -> URL_LOADAPP
                R.id.radioButton3 -> URL_RETRO
                else -> ""
            }
            download(selectedUrl)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val query = DownloadManager.Query()

            val fileName = when(selectedUrl) {
                URL_GLIDE -> getString(R.string.glide_title)
                URL_LOADAPP -> getString(R.string.loadapp_title)
                URL_RETRO -> getString(R.string.retrofit_title)
                else -> ""
            }

            var downloadStatus = ""

            if (id != null) {
                query.setFilterById(id)

                val cursor: Cursor = downloadManager.query(query)
                cursor.moveToFirst()
                val notificationManager = ContextCompat.getSystemService(application, NotificationManager::class.java) as NotificationManager

                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    custom_button.buttonState = ButtonState.Completed
                    //Todo check whether to clear selected radio button on download complete
//                    radio_group.clearCheck()
                    downloadStatus = "Success"

                } else {
                    downloadStatus = "Failed"
                }
                notificationManager.sendNotification(getString(R.string.notification_description), context, fileName, downloadStatus)

                cursor.close()
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_description)

            val notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun download(url: String) {
        if (url.isNotEmpty()) {
            custom_button.buttonState = ButtonState.Loading

            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

            downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID = downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        } else {
            Toast.makeText(applicationContext, getString(R.string.button_select), Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val URL_GLIDE = "https://github.com/bumptech/glide/archive/master.zip"
        private const val URL_LOADAPP = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_RETRO = "https://github.com/square/retrofit/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
