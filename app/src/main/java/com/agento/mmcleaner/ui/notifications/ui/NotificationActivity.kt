package com.agento.mmcleaner.ui.notifications.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.media.RingtoneManager
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View.VISIBLE
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.agento.mmcleaner.R
import com.agento.mmcleaner.events.FirebaseLogger
import com.agento.mmcleaner.ui.notifications.model.types.NotificationType
import com.agento.mmcleaner.ui.notifications.utils.NotificationUtils.NOTIFICATION_VIBRATION_PATTERN
import com.agento.mmcleaner.ui.notifications.utils.NotificationUtils.logNotif
import com.agento.mmcleaner.ui.notifications.utils.NotificationUtils.notificationSoundUri
import com.agento.mmcleaner.ui.notifications.utils.NotificationUtils.vibrator
import com.agento.mmcleaner.ui.notifications.utils.NotificationsHelper

class NotificationActivity : Activity() {
    companion object {
        const val NOTIFICATION_TYPE_EXTRA = "notification_type"

        fun start(context: Context, notificationType: NotificationType) = try {
            context.startActivity(
                Intent(context, NotificationActivity::class.java).apply {
                    flags = FLAG_ACTIVITY_NEW_TASK
                    putExtra(NOTIFICATION_TYPE_EXTRA, notificationType)
                }
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
            logNotif("start notif activity failed: $ex")
        }
    }

    override fun onAttachedToWindow() = with(window.decorView) {
        windowManager.updateViewLayout(
            this,
            (layoutParams as WindowManager.LayoutParams).apply {
                width = WindowManager.LayoutParams.MATCH_PARENT
                gravity = Gravity.CENTER
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        with(window) {
            setFlags(262144, 262144)
        }

        playSound()
        vibrate()

        NotificationsHelper.instance?.onActivityShow(this)

        intent.getParcelableExtra<NotificationType>(NOTIFICATION_TYPE_EXTRA)?.let {
            initViews(it)
        }
    }

    override fun onTouchEvent(motionEvent: MotionEvent): Boolean = when (motionEvent.action) {
        MotionEvent.ACTION_OUTSIDE -> true
        else -> super.onTouchEvent(motionEvent)
    }

    override fun onBackPressed() {}

    private fun playSound() = try {
        RingtoneManager.getRingtone(this, notificationSoundUri).play()
    } catch (ex: Exception) {
        ex.printStackTrace()
    }

    private fun vibrate() = try {
        vibrator.vibrate(NOTIFICATION_VIBRATION_PATTERN, -1)
    } catch (ex: Exception) {
        ex.printStackTrace()
    }

    private fun initViews(notificationType: NotificationType) =
        when (notificationType) {
            is NotificationType.Daily -> initDailyViews(notificationType)
            is NotificationType.AppRemoved -> initAppRemovedViews(notificationType)
            is NotificationType.ChargingBattery -> initChargingViews(notificationType)
            is NotificationType.SilentBattery -> initSilentChargingViews(notificationType)
        }

    private fun initDailyViews(daily: NotificationType.Daily) {
        val dailyLayout = findViewById<LinearLayout>(R.id.activity_notification_daily)

        with(dailyLayout) {
            visibility = VISIBLE

            //set listeners
            findViewById<ImageView>(R.id.notification_daily_close_button).setOnClickListener {
                finish()
            }
            findViewById<TextView>(R.id.notification_daily_clear_button).setOnClickListener {
                daily.navigateToActivity()
            }
        }
    }

    private fun initAppRemovedViews(appRemoved: NotificationType.AppRemoved) {
        val appRemovedLayout = findViewById<LinearLayout>(R.id.activity_notification_app_removed)

        with(appRemovedLayout) {
            visibility = VISIBLE

            //set data
            NotificationsHelper.instance?.requestAppLogo(appRemoved.packageName)?.let {
                findViewById<ImageView>(R.id.notification_app_removed_logo).setImageDrawable(it)
            }

            findViewById<TextView>(R.id.notification_app_removed_details_text).text = getString(
                R.string.notification_after_unloading_placeholder,
                appRemoved.sizeOfJunkMb
            )

            val percentTextViewList = listOf(
                R.id.notification_app_removed_first_size,
                R.id.notification_app_removed_second_size,
                R.id.notification_app_removed_third_size
            )

            appRemoved.junkMbSizes.forEachIndexed { index, size ->
                findViewById<TextView>(percentTextViewList[index]).text = getString(
                    R.string.notification_mb_placeholder,
                    size
                )
            }

            //set listeners
            findViewById<TextView>(R.id.notification_app_removed_cancel_button).setOnClickListener {
                finish()
            }
            findViewById<TextView>(R.id.notification_app_removed_optimize_button).setOnClickListener {
                appRemoved.navigateToActivity()
            }
        }
    }

    private fun initChargingViews(chargingBattery: NotificationType.ChargingBattery) {
        val chargingLayout = findViewById<LinearLayout>(R.id.activity_notification_charging)

        with(chargingLayout) {
            visibility = VISIBLE

            //set data
            findViewById<TextView>(R.id.notification_charging_percent_text).text = getString(
                R.string.notification_percent_placeholder,
                chargingBattery.batteryLevel
            )
            findViewById<TextView>(R.id.notification_charging_text).text = getString(
                R.string.notification_charging_placeholder,
                chargingBattery.batteryLevel
            )
            findViewById<TextView>(R.id.notification_charging_programs_slow_down_text).text =
                getString(
                    R.string.notification_programs_slow_down_text_placeholder,
                    chargingBattery.drainingAppsAmount
                )

            //set listeners
            findViewById<TextView>(R.id.notification_charging_cancel_button).setOnClickListener {
                NotificationsHelper.instance?.show(
                    NotificationType.SilentBattery(
                        chargingBattery.batteryLevel,
                        chargingBattery.drainingAppsAmount
                    )
                )
            }

            findViewById<TextView>(R.id.notification_charging_optimize_button).setOnClickListener {
                chargingBattery.navigateToActivity()
            }
        }
    }

    private fun initSilentChargingViews(silentBattery: NotificationType.SilentBattery) {
        initChargingViews(
            NotificationType.ChargingBattery(
                silentBattery.batteryLevel,
                silentBattery.drainingAppsAmount
            )
        )

        findViewById<LinearLayout>(R.id.activity_notification_charging)
            .findViewById<TextView>(R.id.notification_charging_cancel_button)
            .setOnClickListener {
                finish()
            }
    }

    private fun NotificationType.navigateToActivity() {
        FirebaseLogger.log(
            when (this) {
                is NotificationType.AppRemoved -> FirebaseLogger.EventType.OPENED_FROM_APP_DELETE_PUSH
                is NotificationType.Daily -> FirebaseLogger.EventType.OPENED_FROM_REGULAR_PUSH
                is NotificationType.SilentBattery, is NotificationType.ChargingBattery -> FirebaseLogger.EventType.OPENED_FROM_CHARGING_PUSH
            }
        )

        startActivity(Intent(this@NotificationActivity, activityToNavigate).apply {
            flags = FLAG_ACTIVITY_NEW_TASK
        })
    }
}