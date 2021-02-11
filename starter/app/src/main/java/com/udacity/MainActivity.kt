package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
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

        custom_button.setOnClickListener {
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
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val query = DownloadManager.Query()
            if (id != null) {
                query.setFilterById(id)

                val cursor: Cursor = downloadManager.query(query)
                cursor.moveToFirst()
                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    custom_button.buttonState = ButtonState.Completed
                } else {
                    Toast.makeText(context, "Download unsuccessful", Toast.LENGTH_LONG).show()
                }
                cursor.close()
            }
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
