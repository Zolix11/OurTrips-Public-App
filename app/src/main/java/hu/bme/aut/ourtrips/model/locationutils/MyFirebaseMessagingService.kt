package hu.bme.aut.ourtrips.model.locationutils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.aut.ourtrips.MainActivity
import hu.bme.aut.ourtrips.R
import hu.bme.aut.ourtrips.model.repository.impl.AccountRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var accountRepository: AccountRepositoryImpl

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Token has changed
        Log.d(TAG, "New token: $token")
        CoroutineScope(Dispatchers.IO).launch {
            accountRepository.updateUserFCMToken(token)
        }
    }


    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(TAG, "From: ${message.from}")

        // Check if message contains data payload
        if (message.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${message.data}")
        }

        // Check if message contains notification payload
        message.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
        val title = message.notification?.title ?: "OurTrips"
        val body =  message.notification?.body
        val imageUrl = message.data.entries

        // Create notification builder
        val builder = NotificationCompat.Builder(this, "default")
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.notifications_48px)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // Create intent to launch app when notification is clicked
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        builder.setContentIntent(pendingIntent)

        // Create and display notification
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, builder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMessagingService"
    }
}

