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
import android.widget.RadioButton
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

        createChannel(getString(R.string.notification_channel_id), getString(R.string.notification_channel_name))

        custom_button.setOnClickListener {
            custom_button.buttonState = ButtonState.Clicked
            selectedRadioButton = radio_group.checkedRadioButtonId
            selectedUrl = when(selectedRadioButton) {
                R.id.radioButton1 -> URL_1
                R.id.radioButton2 -> URL_2
                R.id.radioButton3 -> URL_3
                else -> ""
            }
            download(selectedUrl)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val query = DownloadManager.Query()
            if (id != null) {
                query.setFilterById(id)

                val cursor: Cursor = downloadManager.query(query)
                cursor.moveToFirst()
                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    custom_button.buttonState = ButtonState.Completed
                    //Todo check whether to clear selected radio button on download complete
//                    radio_group.clearCheck()

                    val notificationManager = ContextCompat.getSystemService(application, NotificationManager::class.java) as NotificationManager
                    notificationManager.sendNotification("DOWNLOAD COMPLETE", context)
                } else {
                    Toast.makeText(context, "Download unsuccessful", Toast.LENGTH_LONG).show()
                }
                cursor.close()
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        // TODO: Step 1.6 START create a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                // TODO: Step 2.4 change importance
                NotificationManager.IMPORTANCE_HIGH
            )// TODO: Step 2.6 disable badges for this channel
                .apply {
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
        // TODO: Step 1.6 END create a channel
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



        //TODO check whether it's worth animating based on download progress
//        Thread {
//            var downloading = true
//            while (downloading) {
//                val query = DownloadManager.Query()
//                query.setFilterById(downloadID)
//                val cursor: Cursor = downloadManager.query(query)
//                cursor.moveToFirst()
//                val bytesDownloaded =
//                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
//                Log.d("DOWNLOADING", "So far: $bytesDownloaded")
//                val bytesTotal =
//                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
//
//                if (bytesTotal != -1) {
////                    val dlProgress = (bytesDownloaded / bytesTotal * 100).toDouble()
//                    var dlProgress = (bytesDownloaded.toDouble() / bytesTotal) * 100
////                runOnUiThread { mProgressBar.setProgress(dl_progress.toInt()) }
//                    Log.d("DOWNLOADING", "$bytesDownloaded / $bytesTotal")
//                    Log.d("DOWNLOADING", "progress ${dlProgress.toInt()}")
//                }
//
////                Log.d(SyncStateContract.Constants.MAIN_VIEW_ACTIVITY, statusMessage(cursor))
//                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
//                    downloading = false
//                }
//                cursor.close()
//            }
//        }.start()

    }

    companion object {
        private const val URL_1 = "https://github.com/bumptech/glide/archive/master.zip"
        private const val URL_2 = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_3 = "https://github.com/square/retrofit/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
