package com.agento.mmcleaner.ui.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import com.agento.mmcleaner.R
import com.agento.mmcleaner.ui.BaseActivity
import com.agento.mmcleaner.ui.splash.SplashActivity

class SelectLanguageActivity : BaseActivity(R.layout.activity_select_language) {

    private lateinit var change: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        change = findViewById(R.id.change_lang)

        change.setOnClickListener {
            setLocale("ru")
            finishAffinity()
            startActivity(Intent(this, SplashActivity::class.java))
        }
    }
}