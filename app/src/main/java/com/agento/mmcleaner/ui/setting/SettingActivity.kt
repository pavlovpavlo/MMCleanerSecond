package com.agento.mmcleaner.ui.setting

import android.app.NotificationManager
import android.app.PendingIntent
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.agento.mmcleaner.MyApplication
import com.agento.mmcleaner.R
import com.agento.mmcleaner.ui.PrivacyPolicyActivity
import com.agento.mmcleaner.ui.optimized.PhoneOptimizedActivity
import com.agento.mmcleaner.ui.splash.SplashActivity
import com.agento.mmcleaner.util.shared.LocalSharedUtil
import com.agento.mmcleaner.util.widget.SimpleWidgetProvider


class SettingActivity : AppCompatActivity() {

    lateinit var switchCompat: SwitchCompat
    lateinit var switchBg: LinearLayout
    lateinit var notificationSwitch: LinearLayout
    lateinit var switchCompatWidget: SwitchCompat
    lateinit var switchBgWidget: LinearLayout
    lateinit var update: LinearLayout
    lateinit var policy: LinearLayout
    lateinit var rate: LinearLayout
    lateinit var backBtn: ImageView
    lateinit var appWidgetManager: AppWidgetManager
    lateinit var appWidgetHost: AppWidgetHost
    var REQUEST_PICK = 12004
    var REQUEST_BIND = 12005

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        MyApplication.get().setCurrentScreen(19)
        backBtn = findViewById(R.id.back_btn)
        notificationSwitch = findViewById(R.id.notification_switch)
        switchCompat = findViewById(R.id.switch_control)
        switchBg = findViewById(R.id.switch_bg)

        switchCompatWidget = findViewById(R.id.switch_control_widget)
        switchBgWidget = findViewById(R.id.switch_bg_widget)

        update = findViewById(R.id.update)
        policy = findViewById(R.id.privacy_policy)
        rate = findViewById(R.id.rate_us)

        backBtn.setOnClickListener {
            finish()
        }

        notificationSwitch.setOnClickListener {
            switchCompat.isChecked = !switchCompat.isChecked
            if (switchCompat.isChecked) {

                LocalSharedUtil.setNotificationOn(true, this)
            } else {
                hideNotification()
                LocalSharedUtil.setNotificationOn(false, this)
            }
        }

        update.setOnClickListener {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName")
                    )
                )
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    )
                )
            }
        }
        policy.setOnClickListener {
            startActivity(Intent(this, PrivacyPolicyActivity::class.java))
        }
        rate.setOnClickListener {
            startActivity(Intent(this, PhoneOptimizedActivity::class.java))
        }

        switchCompatWidget.setOnCheckedChangeListener { buttonView, isChecked ->
            switchBgWidget.background = if (switchCompatWidget.isChecked)
                resources.getDrawable(R.drawable.custom_track_active)
            else
                resources.getDrawable(R.drawable.custom_track)

            if (switchCompatWidget.isChecked) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    setWidget()
                }
            } else deletWidget()
        }


        switchCompat.setOnCheckedChangeListener { buttonView, isChecked ->
            switchBg.background = if (switchCompat.isChecked)
                resources.getDrawable(R.drawable.custom_track_active)
            else
                resources.getDrawable(R.drawable.custom_track)
        }
    }

    fun hideNotification() {
        val notificationService = NOTIFICATION_SERVICE
        val mNotificationManager = getSystemService(notificationService) as NotificationManager
        mNotificationManager.cancel(9075)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setWidget() {
        val packageManager = applicationContext.packageManager

        packageManager.setComponentEnabledSetting(
            ComponentName(
                applicationContext,
                SimpleWidgetProvider::class.java
            ), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val appWidgetManager = getSystemService(AppWidgetManager::class.java)
            val myProvider = ComponentName(this, SimpleWidgetProvider::class.java)
            if (appWidgetManager.isRequestPinAppWidgetSupported) {
                val intent = Intent(this, SplashActivity::class.java)
                val pendingIntent = PendingIntent.getBroadcast(
                    this, 90065,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT
                )
                appWidgetManager.requestPinAppWidget(myProvider, null, pendingIntent)
            }
        }
    }

    fun deletWidget() {
        val packageManager = applicationContext.packageManager
        packageManager.setComponentEnabledSetting(
            ComponentName(
                applicationContext,
                SimpleWidgetProvider::class.java
            ), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
        )
    }
}