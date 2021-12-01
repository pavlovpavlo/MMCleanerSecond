package com.agento.mmcleaner.ui.notifications.utils

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.PowerManager
import android.os.Vibrator
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleService
import com.agento.mmcleaner.R

object NotificationUtils {

    fun logNotif(message: String) {
        Log.i("TAG", "notif: $message")
    }

    const val EXTRA_NOTIFICATION = "notif"

    val Context.vibrator: Vibrator
        get() = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    val Context.cameraManager: CameraManager
        get() = getSystemService(Context.CAMERA_SERVICE) as CameraManager

    val Context.alarmManager: AlarmManager
        get() = getSystemService(LifecycleService.ALARM_SERVICE) as AlarmManager

    val Context.powerManager: PowerManager
        get() = getSystemService(LifecycleService.POWER_SERVICE) as PowerManager

    val Context.notificationManager: NotificationManager
        get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val Context.activityManager: ActivityManager
        get() = getSystemService(LifecycleService.ACTIVITY_SERVICE) as ActivityManager

    val Context.notificationSoundUri: Uri
        get() = "android.resource://${packageName}/${R.raw.notification_sound}".toUri()

    private const val VIBRATION_PAUSE = 100L
    private const val VIBRATION_LENGTH = 300L
    val NOTIFICATION_VIBRATION_PATTERN = longArrayOf(
        VIBRATION_PAUSE,
        VIBRATION_LENGTH,
        VIBRATION_PAUSE,
        VIBRATION_LENGTH,
        VIBRATION_PAUSE,
        VIBRATION_LENGTH
    )
}