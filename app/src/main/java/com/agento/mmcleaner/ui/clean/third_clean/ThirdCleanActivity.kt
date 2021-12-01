package com.agento.mmcleaner.ui.clean.third_clean

import android.content.Intent
import androidx.navigation.Navigation
import com.agento.mmcleaner.R
import com.agento.mmcleaner.ui.BaseActivity
import com.agento.mmcleaner.ui.optimized.PhoneNoOptimizedActivity

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
}