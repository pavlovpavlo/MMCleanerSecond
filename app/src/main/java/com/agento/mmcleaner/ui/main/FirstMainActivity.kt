package com.agento.mmcleaner.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.agento.mmcleaner.MyApplication
import com.agento.mmcleaner.R
import com.agento.mmcleaner.ui.clean.first_clean.FirstScanActivity
import com.agento.mmcleaner.ui.setting.SettingActivity
import com.agento.mmcleaner.util.shared.LocalSharedUtil

class FirstMainActivity : AppCompatActivity(R.layout.activity_first_main) {

    lateinit var loaderAnimation: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyApplication.get().setCurrentScreen(2)
        initViews()
    }

    private fun initViews(){
        val scanBtn : ImageButton = findViewById(R.id.scan_btn)
        val scanBtnBorder : ImageView = findViewById(R.id.scan_btn_border)
        val settingBtn : ImageView = findViewById(R.id.setting_btn)

        loaderAnimation =
            AnimationUtils.loadAnimation(this, R.anim.animation_button_circle)
        scanBtnBorder.animation = loaderAnimation

        settingBtn.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
        scanBtn.setOnClickListener {
            startActivity(Intent(this, FirstScanActivity::class.java))
            LocalSharedUtil.setSharedFirstMain(this)
        }
    }
}