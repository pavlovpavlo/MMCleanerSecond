package com.agento.mmcleaner.ui.notifications.utils.receivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import com.agento.mmcleaner.ui.notifications.model.types.NotificationType
import com.agento.mmcleaner.ui.notifications.utils.NotificationsHelper
import com.agento.mmcleaner.ui.notifications.utils.receivers.DailySchedule.DailyScheduleReceiver.Companion.ACTION_DAILY_NOTIFICATION
import com.agento.mmcleaner.util.shared.LocalSharedUtil
import com.agento.mmcleaner.util.shared.LocalSharedUtil.IS_FIRST_OPEN
import java.util.*

class DailySchedule(val context: Context) {
    companion object {
        private const val DAILY_NOTIFICATION_REQUEST_CODE = 81585

        //TODO set DAILY_INTERVAL_MILLIS
//        private const val DAILY_INTERVAL_MILLIS = 48 * 3_600_000L
        private const val DAILY_TEST_INTERVAL = 10 * 60_000L
    }

    private val notifyAt: Calendar
        get() = Calendar.getInstance().apply {
            if (LocalSharedUtil.getBooleanParameter(IS_FIRST_OPEN, context)) {
                add(Calendar.MINUTE, 5)
//                add(Calendar.MILLISECOND, DAILY_TEST_INTERVAL.toInt())
                LocalSharedUtil.setParameter(false, IS_FIRST_OPEN, context)
            }
            else
                add(Calendar.MINUTE, 10)
        }

    private val Context.alarmManager: AlarmManager
        get() = getSystemService(ALARM_SERVICE) as AlarmManager

    init {
        schedule()
    }

    private fun schedule() {
        val intent = Intent(context, DailyScheduleReceiver::class.java).apply {
            action = ACTION_DAILY_NOTIFICATION
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            DAILY_NOTIFICATION_REQUEST_CODE,
            intent,
            FLAG_IMMUTABLE
        )

        context.alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            notifyAt.timeInMillis,
            DAILY_TEST_INTERVAL,
            pendingIntent
        )
    }

    class DailyScheduleReceiver : BroadcastReceiver() {
        companion object {
            const val ACTION_DAILY_NOTIFICATION = "daily_notification"
        }

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_DAILY_NOTIFICATION)
                NotificationsHelper.instance?.show(NotificationType.Daily)
        }
    }
}