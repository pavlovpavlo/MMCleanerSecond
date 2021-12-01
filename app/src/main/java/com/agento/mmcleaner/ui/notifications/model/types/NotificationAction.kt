package com.agento.mmcleaner.ui.notifications.model.types

import android.app.PendingIntent
import android.content.Context
import android.os.Parcelable
import com.agento.mmcleaner.ui.notifications.utils.receivers.ActionReceiver
import kotlinx.parcelize.Parcelize

sealed class NotificationAction(private val pendingId: Int) : Parcelable {

    companion object {
        const val CLOSE_ID = 222
        const val OPTIMIZE_ID = 333
        const val OPEN_ACTIVITY_ID = 444
        const val OPEN_SILENT_NOTIFICATION_ID = 555
    }

    @Parcelize
    object Close : NotificationAction(CLOSE_ID), Parcelable

    @Parcelize
    data class Optimize(
        val notificationType: NotificationType
    ) : NotificationAction(OPTIMIZE_ID), Parcelable

    @Parcelize
    data class OpenLikeActivity(
        val notificationType: NotificationType
    ) : NotificationAction(OPEN_ACTIVITY_ID), Parcelable

    @Parcelize
    data class OpenLikeSilentBatteryNotification(
        val batteryNotification: NotificationType.ChargingBattery
    ) : NotificationAction(OPEN_SILENT_NOTIFICATION_ID), Parcelable

    fun getPendingIntent(context: Context): PendingIntent = PendingIntent.getBroadcast(
        context,
        pendingId,
        ActionReceiver.configureIntent(context, this),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}