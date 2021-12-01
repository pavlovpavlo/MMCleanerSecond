package com.agento.mmcleaner.ui.clean.first_clean

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.widget.TextView
import cn.septenary.ui.widget.GradientProgressBar
import com.agento.mmcleaner.MyApplication
import com.agento.mmcleaner.R
import com.agento.mmcleaner.events.FirebaseLogger
import com.agento.mmcleaner.ui.BaseActivity
import com.agento.mmcleaner.ui.notifications.ui.NotificationService
import com.agento.mmcleaner.util.DiskStat
import com.agento.mmcleaner.util.GradientProgressBarAnimation
import com.agento.mmcleaner.util.SingletonClassApp
import com.agento.mmcleaner.util.UtilPhoneInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FirstScanActivity : BaseActivity(R.layout.fragment_first_scan) {

    lateinit var progressBar: GradientProgressBar
    lateinit var progressText: TextView
    lateinit var brandText: TextView
    lateinit var modelText: TextView
    lateinit var androidText: TextView
    lateinit var cpuText: TextView
    lateinit var ramText: TextView
    lateinit var memoryText: TextView
    lateinit var anim: GradientProgressBarAnimation


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
    }

    override fun onResume() {
        super.onResume()
        MyApplication.get().setCurrentScreen(3)
        if (intent.getBooleanExtra(NotificationService.COME_FROM_NOTIFICATION_SERVICE, false))
            FirebaseLogger.log(FirebaseLogger.EventType.OPENED_FROM_STATUS_BAR)
    }

    private fun initViews() {
        brandText = findViewById(R.id.brand)
        modelText = findViewById(R.id.model)
        androidText = findViewById(R.id.android_version)
        cpuText = findViewById(R.id.cpu_name)
        ramText = findViewById(R.id.ram_count)
        memoryText = findViewById(R.id.memory_count)
        progressBar = findViewById(R.id.bar2)
        progressText = findViewById(R.id.progress_text)

        setPhoneData()
    }

    private fun startAnimation(from: Float, to: Float, duration: Long) {
        anim = GradientProgressBarAnimation(progressBar, from, to)
        anim.setProgress(progressText)
        anim.duration = duration
        progressBar.startAnimation(anim)
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                openNextScreen()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    private fun setPhoneData() {
        startShowAnimation()

        startAnimation(0f, 100f, 6000)
    }

    private fun startShowAnimation() {
        val brand = UtilPhoneInfo.getDeviceName()
        kotlinx.coroutines.GlobalScope.launch(context = Dispatchers.Main) {
            for (i in 0..5) {
                when (i) {
                    0 -> {
                        brandText.text = brand.substring(0, brand.indexOf(" "))
                        (brandText.parent as View).visibility = View.VISIBLE
                    }
                    1 -> {
                        modelText.text = brand.substring(brand.indexOf(" "))
                        (modelText.parent as View).visibility = View.VISIBLE
                    }
                    2 -> {
                        androidText.text = Build.VERSION.RELEASE.toString()
                        (androidText.parent as View).visibility = View.VISIBLE
                    }
                    3 -> {
                        var cpuData = UtilPhoneInfo.getCPUName()
                        cpuData = cpuData.substring(cpuData.indexOf(":") + 1)
                        try {
                            cpuData = cpuData.substring(0, cpuData.indexOf("Processor") - 1)
                        } catch (e: StringIndexOutOfBoundsException) {
                            cpuData = "Undefined"
                        }

                        cpuText.text = cpuData
                        if (cpuText.text == "0")
                            cpuText.text = "Undefined"
                        (cpuText.parent as View).visibility = View.VISIBLE
                    }
                    4 -> {
                        ramText.text = UtilPhoneInfo.getTotalRAM()
                        (ramText.parent as View).visibility = View.VISIBLE
                    }
                    5 -> {
                        memoryText.text =
                            UtilPhoneInfo.toNormalFormat(DiskStat().totalSpace.toDouble())
                        (memoryText.parent as View).visibility = View.VISIBLE
                    }
                }
                delay(750)
            }
        }
    }

    override fun onBackPressed() {
    }

    private fun openNextScreen() {
        initAdsMain();
        SingletonClassApp.getInstance().start_ads = 4;
//        val intent = Intent(this, FirstCleanActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
//        startActivity(intent)
//        finish()
    }
}