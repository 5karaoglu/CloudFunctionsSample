package com.besirkaraoglu.cloudfunctionssample.core

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.besirkaraoglu.cloudfunctionssample.MainActivity
import com.besirkaraoglu.cloudfunctionssample.R
import com.besirkaraoglu.cloudfunctionssample.data.FirestoreRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService (): FirebaseMessagingService() {


    @OptIn(DelicateCoroutinesApi::class)
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        /*GlobalScope.launch(Dispatchers.IO) {
            firestoreRepository.updateToken(token)
        }*/
    }

    @SuppressLint("LongLogTag")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "onMessageReceived: Message received!")
        var notificationTitle: String? = null
        var notificationBody: String? = null

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d(TAG, "MessageNotificationBody: " + remoteMessage.getNotification()?.getBody())
            notificationTitle = remoteMessage.getNotification()?.getTitle()
            notificationBody = remoteMessage.getNotification()?.getBody()
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        sendNotification(notificationTitle, notificationBody)
    }

    private fun sendNotification(notificationTitle: String?, notificationBody: String?) {
        createNotificationChannel()
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this,"all_notifications")
            .setAutoCancel(true) //Automatically delete the notification
            .setSmallIcon(R.mipmap.ic_launcher) //Notification icon
            .setContentIntent(pendingIntent)
            .setContentTitle(notificationTitle)
            .setContentText(notificationBody)
            .setSound(defaultSoundUri) as NotificationCompat.Builder
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        notificationManager!!.notify(1, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "MessagingService"
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "all_notifications" // You should create a String resource for this instead of storing in a variable
            val mChannel = NotificationChannel(
                channelId,
                "General Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            mChannel.description = "This is default channel used for all other notifications"

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }
}