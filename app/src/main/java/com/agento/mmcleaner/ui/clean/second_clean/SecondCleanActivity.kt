package com.agento.mmcleaner.ui.clean.second_clean

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import com.agento.mmcleaner.R
import com.agento.mmcleaner.ui.BaseActivity
import com.agento.mmcleaner.ui.optimized.PhoneNoOptimizedActivity

class SecondCleanActivity : BaseActivity(R.layout.activity_second_clean) {

    override fun onBackPressed() {
        val navController = Navigation.findNavController(this, R.id.nav_host_second_fragment)

        val prevFragId = navController.currentDestination!!.id
        when(prevFragId){
            R.id.fragment_second_scan,
            R.id.fragment_second_optimization->{
            }

            R.id.fragment_second_scan_end->{

            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}