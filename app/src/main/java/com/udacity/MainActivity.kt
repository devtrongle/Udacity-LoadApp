package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var mDownloadID: Long = 1220

    private lateinit var mNotificationManager: NotificationManager
    private lateinit var mPendingIntent: PendingIntent
    private lateinit var mNotificationAction: NotificationCompat.Action
    private var mFileName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        custom_button.setOnClickListener {
            var url = ""
            var fileName = ""
            when {
                glide_radio.isChecked -> {
                    url = Constants.URL_GLIDE
                    mFileName = glide_radio.text.toString()
                }
                load_app_radio.isChecked -> {
                    url = Constants.URL_LOAD_APP
                    mFileName = load_app_radio.text.toString()
                }
                retrofit_radio.isChecked -> {
                    url = Constants.URL_RETROFIT
                    mFileName = retrofit_radio.text.toString()
                }
                else -> Toast.makeText(
                    this,
                    R.string.please_choose_a_download_package,
                    Toast.LENGTH_SHORT
                ).show()
            }

            if (!TextUtils.isEmpty(url)) {
                download(url)
                custom_button.buttonState = ButtonState.Loading
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val downloadID = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            downloadID?.let { id ->
                if (id != -1L && downloadID == mDownloadID) {
                    val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                    val query = DownloadManager.Query()
                    query.setFilterById(id)
                    val cursor = downloadManager.query(query)
                    if(cursor.moveToFirst()){
                        when(cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))){
                            DownloadManager.STATUS_SUCCESSFUL -> createNotification(mFileName,Constants.Status.SUCCESS)
                            DownloadManager.STATUS_FAILED -> createNotification(mFileName,Constants.Status.FAIL)
                        }
                    }

                    custom_button.buttonState = ButtonState.Completed
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    private fun download(url: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(getString(R.string.app_name))
            .setDescription(String.format(getString(R.string.app_description), mFileName))
            .setRequiresCharging(false)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        // enqueue puts the download request in the queue.
        mDownloadID = downloadManager.enqueue(request)
    }

    private fun createNotification(
        @NonNull fileName: String,
        @NonNull status: Constants.Status
    ) {

        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra(Constants.KEY_STATUS, status.name)
            putExtra(Constants.KEY_FILE_NAME, fileName)
        }

        mPendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        mNotificationAction = NotificationCompat.Action(
            R.drawable.ic_baseline_open_in_new_24,
            getString(R.string.notification_button),
            mPendingIntent
        )

        val builder = NotificationCompat.Builder(this, Constants.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_round_notifications_24)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_description))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .addAction(mNotificationAction)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.change_name)
            val important = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(Constants.CHANNEL_ID, name, important).apply {
                description = getString(R.string.change_description)
            }
            mNotificationManager.createNotificationChannel(channel)
        }

        mNotificationManager.notify(Constants.NOTIFICATION_ID, builder.build())
    }
}
