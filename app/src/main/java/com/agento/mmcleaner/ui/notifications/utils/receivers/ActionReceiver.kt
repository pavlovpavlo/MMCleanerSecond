package com.agento.mmcleaner.ui.notifications.utils.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import com.agento.mmcleaner.events.FirebaseLogger
import com.agento.mmcleaner.ui.notifications.model.types.NotificationAction
import com.agento.mmcleaner.ui.notifications.model.types.NotificationType
import com.agento.mmcleaner.ui.notifications.ui.NotificationActivity
import com.agento.mmcleaner.ui.notifications.utils.NotificationUtils.logNotif
import com.agento.mmcleaner.ui.notifications.utils.NotificationsHelper

class ActionReceiver : BroadcastReceiver() {

    companion object {
        private const val RECEIVER_NOTIFICATION_ACTION = "notification_action"

        fun configureIntent(context: Context, notificationAction: NotificationAction) = Intent(
            context,
            ActionReceiver::class.java
        ).putExtra(RECEIVER_NOTIFICATION_ACTION, notificationAction)
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.getParcelableExtra<NotificationAction>(
            RECEIVER_NOTIFICATION_ACTION
        ) ?: return

        logNotif("onReceive: action: $action")

        when (action) {
            is NotificationAction.Close -> closeNotification()
            is NotificationAction.Optimize -> action.notificationType.optimize(context)
            is NotificationAction.OpenLikeActivity -> action.notificationType.openFullscreen(context)
            is NotificationAction.OpenLikeSilentBatteryNotification -> action.batteryNotification.openLikeSilent()
        }
    }

    private fun closeNotification() =
        NotificationsHelper.instance?.cancelCurrent()

    private fun NotificationType.optimize(context: Context) {
        FirebaseLogger.log(
            when (this) {
                is NotificationType.AppRemoved -> FirebaseLogger.EventType.OPENED_FROM_APP_DELETE_PUSH
                is NotificationType.Daily -> FirebaseLogger.EventType.OPENED_FROM_REGULAR_PUSH
                is NotificationType.SilentBattery, is NotificationType.ChargingBattery -> FirebaseLogger.EventType.OPENED_FROM_CHARGING_PUSH
            }
        )

        context.startActivity(Intent(context, activityToNavigate).addFlags(FLAG_ACTIVITY_NEW_TASK))
        closeNotification()
    }

    private fun NotificationType.openFullscreen(context: Context) {
        NotificationsHelper.instance?.let {
            if (!it.isNotificationShownAsActivity)
                NotificationActivity.start(context, this)
        }
    }

    private fun NotificationType.ChargingBattery.openLikeSilent() {
        NotificationsHelper.instance?.show(
            NotificationType.SilentBattery(
                batteryLevel,
                drainingAppsAmount
            )
        )
    }
}