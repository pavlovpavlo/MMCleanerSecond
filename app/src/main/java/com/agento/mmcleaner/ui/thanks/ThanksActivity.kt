package com.agento.mmcleaner.ui.thanks

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.agento.mmcleaner.MyApplication
import com.agento.mmcleaner.R

class ThanksActivity : AppCompatActivity(R.layout.activity_thanks) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyApplication.get().setCurrentScreen(20)
        Handler(Looper.getMainLooper()).postDelayed({ finish()}, 2000L)
    }
}