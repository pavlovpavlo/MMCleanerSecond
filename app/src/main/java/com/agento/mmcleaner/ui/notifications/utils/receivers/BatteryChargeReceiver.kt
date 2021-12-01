package com.agento.mmcleaner.ui.notifications.utils.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BATTERY_CHANGED
import android.content.IntentFilter
import android.os.BatteryManager.EXTRA_LEVEL
import android.os.BatteryManager.EXTRA_PLUGGED
import com.agento.mmcleaner.scan_util.model.JunkInfo
import com.agento.mmcleaner.ui.notifications.model.types.NotificationType
import com.agento.mmcleaner.ui.notifications.utils.NotificationsHelper
import com.agento.mmcleaner.util.UStats

class BatteryChargeReceiver(val context: Context) : BroadcastReceiver() {
    companion object {
        private const val DEFAULT_LEVEL = 100
        private const val NOT_CHARGING = 0
    }

    init {
        register(context)
    }

    private var chargingNow = true

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_BATTERY_CHANGED) {
            val batteryLevel = intent.getIntExtra(EXTRA_LEVEL, DEFAULT_LEVEL)
            val isPluggedNow = intent.getIntExtra(EXTRA_PLUGGED, NOT_CHARGING) != NOT_CHARGING
            val startedChargingNow = !chargingNow && isPluggedNow

            chargingNow = isPluggedNow

            if (startedChargingNow) {
                val usage = UStats.getUsageStatsList(context, true)
                if (usage.size > 0) {
                    val usageStat = JunkInfo()
                    usage.add(0, usageStat)
                }

                NotificationsHelper.instance?.show(
                    NotificationType.ChargingBattery(
                        batteryLevel,
                        usage.size
                    )
                )
            }
        }
    }

    private fun register(context: Context) =
        context.registerReceiver(this, IntentFilter().apply { addAction(ACTION_BATTERY_CHANGED) })
}