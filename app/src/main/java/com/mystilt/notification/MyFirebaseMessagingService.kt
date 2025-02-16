package com.mystilt.notification

import android.app.ActivityManager
import android.content.Context
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mystilt.deeplink.DeepLinkData
import com.mystilt.deeplink.DeepLinkResponse
import timber.log.Timber

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        updateToken(token)
    }

    private fun updateToken(token: String) {

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val title = remoteMessage.notification?.title ?: "New Notification"
        val message = remoteMessage.notification?.body ?: "Tap to open"
        val deepLinkData = remoteMessage.data

        val deepLinkResponse = DeepLinkResponse(
            targetUrl = deepLinkData["targetUrl"] ?: "",
            type = deepLinkData["type"] ?: "home",
            deepLinkData = deepLinkData.entries.map { DeepLinkData(it.key, it.value) }
        )


        val notificationHelper = NotificationHelper(applicationContext)
        notificationHelper.showNotification(title, message, deepLinkResponse)
    }
    companion object {
        private val TAG = MyFirebaseMessagingService::class.java.simpleName
        fun isAppRunning(context: Context, packageName: String): Boolean {
            val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            val procInfos = activityManager.runningAppProcesses
            if (procInfos != null) {
                for (processInfo in procInfos) {
                    if (processInfo.processName == packageName) {
                        return true
                    }
                }
            }
            return false
        }
    }
}
