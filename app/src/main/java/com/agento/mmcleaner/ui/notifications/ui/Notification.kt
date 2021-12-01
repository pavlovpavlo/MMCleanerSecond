package com.agento.mmcleaner.ui.notifications.ui

import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.NotificationManager.IMPORTANCE_LOW
import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat.*
import androidx.core.graphics.drawable.toBitmap
import com.agento.mmcleaner.R
import com.agento.mmcleaner.ui.notifications.model.types.NotificationAction
import com.agento.mmcleaner.ui.notifications.model.types.NotificationType
import com.agento.mmcleaner.ui.notifications.utils.NotificationUtils.NOTIFICATION_VIBRATION_PATTERN
import com.agento.mmcleaner.ui.notifications.utils.NotificationUtils.logNotif
import com.agento.mmcleaner.ui.notifications.utils.NotificationUtils.notificationManager
import com.agento.mmcleaner.ui.notifications.utils.NotificationUtils.notificationSoundUri
import com.agento.mmcleaner.ui.notifications.utils.NotificationsHelper

class Notification(private val context: Context, val type: NotificationType) {
    companion object {
        private const val ID = 513151
        private const val SILENT_ID = 77766

        private const val GROUP = "notification_group"
        private const val SILENT_GROUP = "notification_silent_group"

        private const val CHANNEL = "notification_channel"
        private const val SILENT_CHANNEL = "notification_silent_channel"
    }

    init {
        logNotif("notif created")
    }

    var activity: NotificationActivity? = null

    fun show() {
        logNotif("show notif called, type: ${type.javaClass.simpleName}, is silent: $isSilentNotification")
        val notification = Builder(context, notificationChannel)
            .setGroup(if (isSilentNotification) SILENT_GROUP else GROUP)
            .setOngoing(true)
            .setAutoCancel(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setSound(notificationSoundUri)
            .setVibrate(notificationVibrationPattern)
            .apply {
                if (SDK_INT >= Build.VERSION_CODES.Q) setContent(remoteViews)
                else setContentText(context.getString(R.string.notification_residual_data_found))

                priority = if (isSilentNotification) PRIORITY_LOW else PRIORITY_HIGH
                logNotif("set priority: $priority")

                if (!isSilentNotification) {
                    logNotif("set alarm category, fullscreen intent")
                    setCategory(CATEGORY_ALARM)
                    setFullScreenIntent(openLikeActivityAction, true)
                } else
                    logNotif("isSilentNotification: ${type::class.java.simpleName}")
            }
            .build()

        with(context.notificationManager) {
            //set notification channel
            if (SDK_INT >= Build.VERSION_CODES.O) createNotificationChannel(channel)

            //show
            logNotif("notify called, id : $notificationId")

            notify(notificationId, notification)
            if (!isSilentNotification) {
                logNotif("start activity")
                NotificationActivity.start(context, type)
            }
        }
    }

    fun cancel(fullCancel: Boolean = false) {
        logNotif("cancelling notif: ${type.javaClass.simpleName}, activity: $activity, fullCancel: $fullCancel")
        //close activity
        if (fullCancel) activity?.finish()

        //cancel
        context.notificationManager.cancel(notificationId)
    }

    //utils
    private val closeAction =
        NotificationAction.Close.getPendingIntent(context)
    private val optimizeAction =
        NotificationAction.Optimize(type).getPendingIntent(context)
    private val openLikeActivityAction =
        NotificationAction.OpenLikeActivity(type).getPendingIntent(context)

    private val isSilentNotification
        get() = type is NotificationType.SilentBattery

    private val notificationId
        get() = if (isSilentNotification) SILENT_ID else ID

    private val notificationChannel
        get() = if (isSilentNotification) SILENT_CHANNEL else CHANNEL

    private val notificationSoundUri
        get() = if (isSilentNotification) null else context.notificationSoundUri

    private val notificationVibrationPattern
        get() = if (isSilentNotification) null else NOTIFICATION_VIBRATION_PATTERN

    private val remoteViews: RemoteViews
        get() = when (type) {
            is NotificationType.Daily -> dailyRemoteViews
            is NotificationType.AppRemoved -> appRemovedRemoteViews
            is NotificationType.ChargingBattery -> chargingRemoteViews
            is NotificationType.SilentBattery -> silentChargingRemoteViews
        }

    private val chargingRemoteViews: RemoteViews
        get() = RemoteViews(context.packageName, R.layout.notification_charging).apply {
            with(type as NotificationType.ChargingBattery) {
                //set percent
                setTextViewText(
                    R.id.notification_charging_percent_text,
                    context.getString(R.string.notification_percent_placeholder, batteryLevel)
                )
                //set charging ..% text
                setTextViewText(
                    R.id.notification_charging_text,
                    context.getString(R.string.notification_charging_placeholder, batteryLevel)
                )
                //set programs text
                setTextViewText(
                    R.id.notification_charging_programs_slow_down_text,
                    context.getString(
                        R.string.notification_programs_slow_down_text_placeholder,
                        drainingAppsAmount
                    )
                )

                //set listeners
                setOnClickPendingIntent(
                    R.id.notification_charging_cancel_button,
                    NotificationAction
                        .OpenLikeSilentBatteryNotification(this)
                        .getPendingIntent(context)
                )
                setOnClickPendingIntent(
                    R.id.notification_charging_layout,
                    openLikeActivityAction
                )
                setOnClickPendingIntent(
                    R.id.notification_charging_optimize_button,
                    optimizeAction
                )
            }
        }

    private val silentChargingRemoteViews: RemoteViews
        get() = RemoteViews(context.packageName, R.layout.notification_charging).apply {
            with(type as NotificationType.SilentBattery) {
                //set percent
                setTextViewText(
                    R.id.notification_charging_percent_text,
                    context.getString(R.string.notification_percent_placeholder, batteryLevel)
                )
                //set charging ..% text
                setTextViewText(
                    R.id.notification_charging_text,
                    context.getString(R.string.notification_charging_placeholder, batteryLevel)
                )
                //set programs text
                setTextViewText(
                    R.id.notification_charging_programs_slow_down_text,
                    context.getString(
                        R.string.notification_programs_slow_down_text_placeholder,
                        drainingAppsAmount
                    )
                )

                //set listeners
                setOnClickPendingIntent(R.id.notification_charging_cancel_button, closeAction)
                setOnClickPendingIntent(R.id.notification_charging_layout, openLikeActivityAction)
                setOnClickPendingIntent(R.id.notification_charging_optimize_button, optimizeAction)
            }
        }

    private val appRemovedRemoteViews: RemoteViews
        get() = RemoteViews(context.packageName, R.layout.notification_app_removed).apply {
            with(type as NotificationType.AppRemoved) {
                //set icon if not null
                NotificationsHelper.instance?.requestAppLogo(packageName)?.let {
                    logNotif("set image, drawable != null for $packageName ")
                    setImageViewBitmap(
                        R.id.notification_app_removed_logo,
                        it.toBitmap()
                    )
                }

                //set app removed details text
                setTextViewText(
                    R.id.notification_app_removed_details_text,
                    context.getString(
                        R.string.notification_after_unloading_placeholder,
                        sizeOfJunkMb
                    )
                )

                //set percents
                val percentTextViewList = listOf(
                    R.id.notification_app_removed_first_size,
                    R.id.notification_app_removed_second_size,
                    R.id.notification_app_removed_third_size
                )

                junkMbSizes.forEachIndexed { index, size ->
                    setTextViewText(
                        percentTextViewList[index],
                        context.getString(R.string.notification_mb_placeholder, size)
                    )
                }

                //set listeners
                setOnClickPendingIntent(R.id.notification_app_removed_cancel_button, closeAction)
                setOnClickPendingIntent(
                    R.id.notification_app_removed_layout,
                    openLikeActivityAction
                )
                setOnClickPendingIntent(
                    R.id.notification_app_removed_optimize_button,
                    optimizeAction
                )
            }
        }

    private val dailyRemoteViews: RemoteViews
        get() = RemoteViews(context.packageName, R.layout.notification_daily).apply {
            setOnClickPendingIntent(R.id.notification_daily_layout, openLikeActivityAction)
            setOnClickPendingIntent(R.id.notification_daily_close_button, closeAction)
            setOnClickPendingIntent(R.id.notification_daily_clear_button, optimizeAction)
        }

    private val channel: NotificationChannel
        @RequiresApi(Build.VERSION_CODES.O)
        get() = NotificationChannel(
            notificationChannel,
            notificationChannel,
            if (isSilentNotification) IMPORTANCE_LOW else IMPORTANCE_HIGH
        ).apply {
            setSound(notificationSoundUri, AudioAttributes.Builder().build())
            vibrationPattern = notificationVibrationPattern
            enableVibration(!isSilentNotification)
        }
}