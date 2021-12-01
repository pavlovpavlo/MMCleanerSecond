package com.agento.mmcleaner.ui.clean.second_clean

import android.os.Bundle
import androidx.navigation.Navigation
import com.agento.mmcleaner.R
import com.agento.mmcleaner.events.FirebaseLogger
import com.agento.mmcleaner.ui.BaseActivity
import com.agento.mmcleaner.ui.notifications.ui.NotificationService.Companion.COME_FROM_NOTIFICATION_SERVICE

class SecondCleanActivity : BaseActivity(R.layout.activity_second_clean) {

    override fun onBackPressed() {
        val navController = Navigation.findNavController(this, R.id.nav_host_second_fragment)

        val prevFragId = navController.currentDestination!!.id
        when (prevFragId) {
            R.id.fragment_second_scan,

            R.id.fragment_second_optimization -> {
            }

            R.id.fragment_second_scan_end -> {
            }

        }
    }

    override fun onResume() {
        super.onResume()
        if (intent.getBooleanExtra(COME_FROM_NOTIFICATION_SERVICE, false))
            FirebaseLogger.log(FirebaseLogger.EventType.OPENED_FROM_STATUS_BAR)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}