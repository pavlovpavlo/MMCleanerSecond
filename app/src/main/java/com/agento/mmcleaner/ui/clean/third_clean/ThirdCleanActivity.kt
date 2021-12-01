package com.agento.mmcleaner.ui.clean.third_clean

import android.content.Intent
import androidx.navigation.Navigation
import com.agento.mmcleaner.R
import com.agento.mmcleaner.ui.BaseActivity
import com.agento.mmcleaner.ui.optimized.PhoneNoOptimizedActivity
<<<<<<< HEAD
import com.agento.mmcleaner.util.UtilNotif
import com.agento.mmcleaner.util.shared.LocalSharedUtil
=======
>>>>>>> a54b71f3e8c9a125c3c44ce1ccc4fea85b255a50

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
<<<<<<< HEAD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(LocalSharedUtil.isNotificationOn(this))
            UtilNotif.showScheduleNotification(this)
    }
=======
>>>>>>> a54b71f3e8c9a125c3c44ce1ccc4fea85b255a50
}