package com.hoang.daniwebnotificationpermission

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED

class MainActivity : AppCompatActivity() {
    private val channelId by lazy {
        getString(R.string.channel_id) //Don't access me before resources are available.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
            if (isGranted){
                createNotificationChannel()
                showNotification()
            }
        }

        val textView = findViewById<TextView>(R.id.textView)
        textView.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (isNotificationPermissionGranted()) {
                    showNotification()
                } else {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                showNotification()
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //O = Oreo, not zero
            val channelName = getString(R.string.channel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            //Create the NotificationChannel object
            val channel = NotificationChannel(
                channelId,
                channelName,
                importance)

            //Retrieve the NotificationManager from the system.
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //Registers the channel with NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification() {
        val notificationId = R.id.notification_id
        val notification = createNotification()

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, notification)
        }
    }

    private fun createNotification(): Notification {
        val icon = R.drawable.ic_baseline_notifications_24
        val title = getString(R.string.notification_title)
        val content = getString(R.string.notification_content)

        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(icon) //required
            .setContentTitle(title) //required
            .setContentText(content) //required
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun isNotificationPermissionGranted() =
        ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PERMISSION_GRANTED

}