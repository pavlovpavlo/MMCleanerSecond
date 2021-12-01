package com.agento.mmcleaner.ui.notifications.model.types

import android.os.Parcelable
import com.agento.mmcleaner.ui.clean.first_clean.FirstCleanActivity
import com.agento.mmcleaner.ui.clean.first_clean.FirstScanActivity
import com.agento.mmcleaner.ui.clean.second_clean.SecondCleanActivity
import com.agento.mmcleaner.ui.clean.third_clean.ThirdCleanActivity
import kotlinx.parcelize.Parcelize

sealed class NotificationType(private val scanOpenPosition: Int) : Parcelable {

    companion object {
        private const val NOTIFICATION_OPEN_SCREEN_BATTERY = 11
        private const val NOTIFICATION_OPEN_SCREEN_APP_REMOVED = 8
        private const val NOTIFICATION_OPEN_SCREEN_DAILY = 2
    }

    @Parcelize
    data class ChargingBattery(
        val batteryLevel: Int,
        val drainingAppsAmount: Int
    ) : NotificationType(NOTIFICATION_OPEN_SCREEN_BATTERY), Parcelable

    @Parcelize
    data class SilentBattery(
        val batteryLevel: Int,
        val drainingAppsAmount: Int
    ) : NotificationType(NOTIFICATION_OPEN_SCREEN_BATTERY), Parcelable

    @Parcelize
    data class AppRemoved(
        val packageName: String,
        val sizeOfJunkMb: Float,
        val junkMbSizes: List<Float>
    ) : NotificationType(NOTIFICATION_OPEN_SCREEN_APP_REMOVED), Parcelable

    @Parcelize
    object Daily : NotificationType(NOTIFICATION_OPEN_SCREEN_DAILY), Parcelable

    val activityToNavigate
        get() = when (scanOpenPosition) {
            NOTIFICATION_OPEN_SCREEN_DAILY -> FirstScanActivity::class.java
            NOTIFICATION_OPEN_SCREEN_BATTERY -> ThirdCleanActivity::class.java
            NOTIFICATION_OPEN_SCREEN_APP_REMOVED -> SecondCleanActivity::class.java

            else -> FirstCleanActivity::class.java
        }
}