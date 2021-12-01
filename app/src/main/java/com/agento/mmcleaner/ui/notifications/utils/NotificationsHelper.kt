package com.agento.mmcleaner.ui.notifications.utils

import android.content.Context
import com.agento.mmcleaner.ui.notifications.model.types.NotificationType
import com.agento.mmcleaner.ui.notifications.ui.Notification
import com.agento.mmcleaner.ui.notifications.ui.NotificationActivity
import com.agento.mmcleaner.ui.notifications.ui.NotificationService
import com.agento.mmcleaner.ui.notifications.utils.receivers.AppActionsReceiver
import com.agento.mmcleaner.ui.notifications.utils.receivers.BatteryChargeReceiver
import com.agento.mmcleaner.ui.notifications.utils.receivers.DailySchedule

class NotificationsHelper(private val context: Context) {
    companion object {
        val instance: NotificationsHelper?
            get() = NotificationService.instance?.notificationsHelper
    }

    val isNotificationShownAsActivity: Boolean
        get() = currentNotification?.activity != null

    private val appActionsReceiver: AppActionsReceiver
    private var currentNotification: Notification? = null

    init {
        DailySchedule(context)
        BatteryChargeReceiver(context)
        appActionsReceiver = AppActionsReceiver(context)
    }

    fun show(type: NotificationType) {
        currentNotification?.cancel(fullCancel = true)
        currentNotification = Notification(context, type).apply {
            show()
        }
    }

    fun onActivityShow(activity: NotificationActivity) {
        cancelCurrent()
        currentNotification?.activity = activity
    }

    fun cancelCurrent() = currentNotification?.cancel()

    fun requestAppLogo(packageName: String) = appActionsReceiver.requestLogo(packageName)
}