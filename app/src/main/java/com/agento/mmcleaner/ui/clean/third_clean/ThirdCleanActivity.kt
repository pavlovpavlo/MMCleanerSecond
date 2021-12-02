package com.agento.mmcleaner.ui.clean.third_clean

import android.content.Intent
import android.os.Bundle
import androidx.navigation.Navigation
import com.agento.mmcleaner.R
import com.agento.mmcleaner.ui.BaseActivity
import com.agento.mmcleaner.ui.notifications.ui.NotificationService
import com.agento.mmcleaner.ui.optimized.PhoneNoOptimizedActivity
import com.agento.mmcleaner.util.shared.LocalSharedUtil


class ThirdCleanActivity : BaseActivity(R.layout.activity_third_clean) {

    override fun onBackPressed() {
        val navController = Navigation.findNavController(this, R.id.nav_host_third_fragment)

        val prevFragId = navController.currentDestination!!.id
        when (prevFragId) {
            R.id.fragment_third_scan,
            R.id.fragment_third_optimization -> {
            }

            R.id.fragment_third_scan_end -> {
                startActivity(Intent(this, PhoneNoOptimizedActivity::class.java))
                finishAffinity()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(LocalSharedUtil.isNotificationOn(this))
            NotificationService.refresh()
    }
}