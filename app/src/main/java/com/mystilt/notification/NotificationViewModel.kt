package com.mystilt.notification

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class NotificationViewModel(application: Application) : AndroidViewModel(application) {

    private val notificationHelper = NotificationHelper(application)

    fun sendNotification(title: String, message: String, targetActivity: Class<*>) {
        //otificationHelper.showNotification(title, message, targetActivity)
    }
}
