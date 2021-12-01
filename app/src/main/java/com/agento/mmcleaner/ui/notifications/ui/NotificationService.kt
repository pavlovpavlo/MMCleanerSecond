package com.agento.mmcleaner.ui.notifications.ui

import android.app.*
import android.app.Notification
import android.app.NotificationManager.IMPORTANCE_LOW
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BOOT_COMPLETED
import android.hardware.camera2.CameraAccessException
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.PowerManager
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import com.agento.mmcleaner.MyApplication
import com.agento.mmcleaner.R
import com.agento.mmcleaner.ui.clean.first_clean.FirstScanActivity
import com.agento.mmcleaner.ui.clean.second_clean.SecondCleanActivity
import com.agento.mmcleaner.ui.notifications.ui.NotificationService.LanternReceiver.Companion.SET_LANTERN_ACTIVE_ACTION
import com.agento.mmcleaner.ui.notifications.ui.NotificationService.LanternReceiver.Companion.SET_LANTERN_ACTIVE_KEY
import com.agento.mmcleaner.ui.notifications.ui.NotificationService.ServiceRestartReceiver.Companion.RESTART_SERVICE_ACTION
import com.agento.mmcleaner.ui.notifications.ui.NotificationService.ServiceRestartReceiver.Companion.RESTART_SERVICE_DELAY
import com.agento.mmcleaner.ui.notifications.ui.NotificationService.ServiceRestartReceiver.Companion.RESTART_SERVICE_REQUEST_CODE
import com.agento.mmcleaner.ui.notifications.utils.NotificationUtils.activityManager
import com.agento.mmcleaner.ui.notifications.utils.NotificationUtils.alarmManager
import com.agento.mmcleaner.ui.notifications.utils.NotificationUtils.cameraManager
import com.agento.mmcleaner.ui.notifications.utils.NotificationUtils.notificationManager
import com.agento.mmcleaner.ui.notifications.utils.NotificationUtils.powerManager
import com.agento.mmcleaner.ui.notifications.utils.NotificationsHelper
import com.agento.mmcleaner.ui.splash.SplashActivity
import com.agento.mmcleaner.util.Util.Companion.cpuTemperature
import com.agento.mmcleaner.util.shared.LocalSharedUtil

class NotificationService : LifecycleService() {
    companion object {
        const val COME_FROM_NOTIFICATION_SERVICE = "come_from_notification_service"

        private const val SERVICE_NOTIFICATION_ID = 91999
        private const val SERVICE_NOTIFICATION_GROUP = "service_group"
        private const val SERVICE_NOTIFICATION_CHANNEL = "service_foreground_notification"

        var instance: NotificationService? = null
            private set

        fun start(context: Context) = with(context) {
            Log.i("TAG", "start")
            Intent(this, NotificationService::class.java).let {
                if (SDK_INT >= Build.VERSION_CODES.O)
                    startForegroundService(it)
                else
                    startService(it)
            }
        }

        fun refresh() = instance?.initialize()

        fun release(context: Context) = with(context) {
            Log.i("TAG", "release")
            instance?.let { stopService(Intent(this, NotificationService::class.java)) }
            instance = null
        }

        fun isRunningNow(context: Context) = with(context) {
            activityManager.getRunningServices(Int.MAX_VALUE).any {
                it.service.className == NotificationService::class.java.name
            }
        }
    }

    private var restartRequired = true
    private lateinit var remoteViews: RemoteViews
    private lateinit var channel: NotificationChannel
    private lateinit var foregroundNotification: Notification

    var notificationsHelper: NotificationsHelper? = null
        private set

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        initialize()
        if (intent == null) return START_STICKY_COMPATIBILITY
        if (instance != null) return START_STICKY

        instance = this
        restartRequired = false

        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        initialize()
        if (instance != null) return

        start(MyApplication.get())
        instance = this
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        callServiceRestartReceiver(this)
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (SDK_INT < Build.VERSION_CODES.O) return

        instance = null
        if (restartRequired) callServiceRestartReceiver(this)
    }

    private fun initialize() {
        //creating of other services
        Log.i("TAG", "initialize: notif helper")
        if (notificationsHelper == null)
            notificationsHelper = NotificationsHelper(this)

        if (SDK_INT >= Build.VERSION_CODES.O) {
            initializeNotificationChannel()
            initializeRemoteViews()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeNotificationChannel() {
        val newChannel = NotificationChannel(
            SERVICE_NOTIFICATION_CHANNEL,
            SERVICE_NOTIFICATION_CHANNEL,
            IMPORTANCE_LOW
        ).apply {
            description = SERVICE_NOTIFICATION_CHANNEL
            lightColor = ContextCompat.getColor(this@NotificationService, R.color.black)
            enableLights(true)
            enableVibration(false)
        }

        notificationManager.createNotificationChannel(newChannel)
        channel = newChannel
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeRemoteViews() {
        val contentView = RemoteViews(packageName, R.layout.notification_long_time)

        val radio = Intent(this, FirstScanActivity::class.java).putExtra(COME_FROM_NOTIFICATION_SERVICE, true)
        val pRadio = PendingIntent.getActivity(this, 0, radio, 0)
        if (!LocalSharedUtil.isStepOptimized(this, LocalSharedUtil.SHARED_FIRST))
            contentView.setImageViewResource(R.id.clear, R.drawable.ic_junk_pass)
        else
            contentView.setImageViewResource(R.id.clear, R.drawable.ic_junk_active)

        contentView.setOnClickPendingIntent(R.id.clear, pRadio)

        val volume = Intent(this, SecondCleanActivity::class.java).putExtra(COME_FROM_NOTIFICATION_SERVICE, true)
        val pVolume = PendingIntent.getActivity(this, 1, volume, 0)
        if (!LocalSharedUtil.isStepOptimized(this, LocalSharedUtil.SHARED_SECOND))
            contentView.setImageViewResource(R.id.batteru, R.drawable.ic_battery_pass)
        else
            contentView.setImageViewResource(R.id.batteru, R.drawable.ic_battery_active)

        contentView.setOnClickPendingIntent(R.id.batteru, pVolume)

        val reboot = Intent(this, FirstScanActivity::class.java).putExtra(COME_FROM_NOTIFICATION_SERVICE, true)
        val pReboot = PendingIntent.getActivity(this, 5, reboot, 0)
        if (!LocalSharedUtil.isStepOptimized(this, LocalSharedUtil.SHARED_THIRD))
            contentView.setImageViewResource(R.id.speed, R.drawable.ic_booster_pass)
        else
            contentView.setImageViewResource(R.id.speed, R.drawable.ic_booster_active)

        contentView.setOnClickPendingIntent(R.id.speed, pReboot)

        val top = Intent(this, SplashActivity::class.java).putExtra(COME_FROM_NOTIFICATION_SERVICE, true)
        val pTop = PendingIntent.getActivity(this, 3, top, 0)

        if (cpuTemperature() > 40)
            contentView.setImageViewResource(R.id.temperature, R.drawable.ic_temperature_act)
        else
            contentView.setImageViewResource(R.id.temperature, R.drawable.ic_temperature)

        val isLumEnabled = LocalSharedUtil.getParameterInt(
            LocalSharedUtil.SHARED_LUMUS,
            applicationContext
        ) == 1

        val lumIntent = Intent(this, LanternReceiver::class.java).apply {
            action = SET_LANTERN_ACTIVE_ACTION
            putExtra(SET_LANTERN_ACTIVE_KEY, !isLumEnabled)
        }

        contentView.setImageViewResource(
            R.id.lantern,
            if (isLumEnabled) R.drawable.ic_lantern_active else R.drawable.ic_lantern
        )

        contentView.setOnClickPendingIntent(
            R.id.lantern_l, PendingIntent.getBroadcast(
                this,
                0,
                lumIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        )

        remoteViews = contentView
        showServiceNotification()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showServiceNotification() {
        try {
            initializeForegroundNotification()
            startForeground(SERVICE_NOTIFICATION_ID, foregroundNotification)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeForegroundNotification() {
        val intent = Intent(this, SplashActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        //build notification
        foregroundNotification =
            NotificationCompat.Builder(this, SERVICE_NOTIFICATION_CHANNEL)
                .setColor(ContextCompat.getColor(this, R.color.black))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(Notification.DEFAULT_ALL)
                .setGroup(SERVICE_NOTIFICATION_GROUP)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setContent(remoteViews)
                .setAutoCancel(true)
                .build().apply {
                    flags = flags or (
                            Notification.FLAG_NO_CLEAR
                                    or Notification.FLAG_ONGOING_EVENT
                                    or Notification.FLAG_FOREGROUND_SERVICE
                            )
                }
    }

    private fun callServiceRestartReceiver(context: Context) {
        val intent = Intent(context, ServiceRestartReceiver::class.java).apply {
            action = RESTART_SERVICE_ACTION
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            RESTART_SERVICE_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        AlarmManagerCompat.setExactAndAllowWhileIdle(
            context.alarmManager,
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + RESTART_SERVICE_DELAY,
            pendingIntent
        )
    }

    class ServiceRestartReceiver : BroadcastReceiver() {
        companion object {
            const val RESTART_SERVICE_REQUEST_CODE = 16996
            const val RESTART_SERVICE_DELAY = 1000L
            const val RESTART_SERVICE_ACTION = "restart_service"
            private val ALLOWED_ACTIONS = arrayOf(RESTART_SERVICE_ACTION, ACTION_BOOT_COMPLETED)

            private const val WAKE_LOG_TIMEOUT = 10 * 60_000L
            private const val WAKE_LOG_TAG = "myapp:wake_log_service"
        }

        override fun onReceive(context: Context, intent: Intent) {
            if (!ALLOWED_ACTIONS.contains(intent.action) || isRunningNow(context)) return

            with(context.powerManager) {
                newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK,
                    WAKE_LOG_TAG
                ).acquire(WAKE_LOG_TIMEOUT)
            }

            with(NotificationService) {
                release(context)
                start(context)
            }
        }
    }

    class LanternReceiver : BroadcastReceiver() {
        companion object {
            const val DEFAULT_IS_ACTIVE = false
            const val SET_LANTERN_ACTIVE_KEY = "set_lantern_active"
            const val SET_LANTERN_ACTIVE_ACTION = "set_lantern_active_action"
        }

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == SET_LANTERN_ACTIVE_ACTION) {
                val needToEnable = intent.getBooleanExtra(SET_LANTERN_ACTIVE_KEY, DEFAULT_IS_ACTIVE)

                try {
                    with(context.cameraManager) {
                        setTorchMode(cameraIdList.first(), needToEnable)
                        LocalSharedUtil.setParameterInt(
                            if (needToEnable) 1 else 0,
                            LocalSharedUtil.SHARED_LUMUS,
                            context
                        )
                    }

                    refresh()

                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            }
        }
    }
}