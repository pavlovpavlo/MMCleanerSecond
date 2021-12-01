package com.agento.mmcleaner.ui.thanks

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.agento.mmcleaner.R
import com.agento.mmcleaner.ui.BaseActivity

class ThanksActivity : BaseActivity(R.layout.activity_thanks) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Handler(Looper.getMainLooper()).postDelayed({ finish()}, 2000L)
    }
}