package com.agento.mmcleaner.ui.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import com.agento.mmcleaner.R
import com.agento.mmcleaner.ui.BaseActivity
import com.agento.mmcleaner.ui.splash.SplashActivity
import com.agento.mmcleaner.util.LocaleHelper
import java.util.*

class SelectLanguageActivity : BaseActivity(R.layout.activity_select_language) {

    private lateinit var change: AppCompatButton
    private lateinit var eng: LinearLayout
    private lateinit var esp: LinearLayout
    private lateinit var ind: LinearLayout
    private lateinit var korean: LinearLayout
    private lateinit var port: LinearLayout
    private lateinit var ru: LinearLayout
    private lateinit var lastActive: LinearLayout
    private var language: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        language = LocaleHelper.getLanguage(this)
        change = findViewById(R.id.change_lang)
        eng = findViewById(R.id.eng)
        esp = findViewById(R.id.es)
        ind = findViewById(R.id.`in`)
        korean = findViewById(R.id.ko)
        port = findViewById(R.id.pt)
        ru = findViewById(R.id.ru)

        lastActive = eng

        when (language.toLowerCase(Locale.getDefault())){
            "ru"-> activeTab(ru)
            "es"-> activeTab(esp)
            "in"-> activeTab(ind)
            "ko"-> activeTab(korean)
            "pt"-> activeTab(port)
            else ->activeTab(eng)
        }

        eng.setOnClickListener {
            activeTab(eng)
            language = "en"
        }
        esp.setOnClickListener {
            activeTab(esp)
            language = "es"
        }
        ind.setOnClickListener {
            activeTab(ind)
            language = "in"
        }
        korean.setOnClickListener {
            activeTab(korean)
            language = "ko"
        }
        port.setOnClickListener {
            activeTab(port)
            language = "pt"
        }
        ru.setOnClickListener {
            activeTab(ru)
            language = "ru"
        }


        change.setOnClickListener {
            setLocale(language)
            setLocale(language)
            finishAffinity()
            startActivity(Intent(this, SplashActivity::class.java))
        }
    }

    fun activeTab(tab: LinearLayout){
        lastActive.setBackgroundResource(R.drawable.ic_setting_item_bg)
        lastActive = tab
        lastActive.setBackgroundResource(R.drawable.ic_setting_item_bg_active)
    }
}