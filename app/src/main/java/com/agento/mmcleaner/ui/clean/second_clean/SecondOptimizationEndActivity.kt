package com.agento.mmcleaner.ui.clean.second_clean

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import com.agento.mmcleaner.MyApplication
import com.agento.mmcleaner.R
import com.agento.mmcleaner.events.FirebaseLogger
import com.agento.mmcleaner.ui.BaseActivity
import com.agento.mmcleaner.ui.clean.third_clean.ThirdCleanActivity
import com.agento.mmcleaner.ui.main.SecondMainActivity
import com.agento.mmcleaner.ui.notifications.ui.NotificationService
import com.agento.mmcleaner.ui.optimized.PhoneNoOptimizedActivity
import com.agento.mmcleaner.util.UStats
import com.agento.mmcleaner.util.shared.LocalSharedUtil
import com.agento.mmcleaner.util.shared.SharedData
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import java.util.*

class SecondOptimizationEndActivity : BaseActivity(R.layout.fragment_second_optimization_end) {

    lateinit var screenOptimized: LinearLayout
    lateinit var adsLoader: ImageView
    lateinit var loaderAnimation: Animation
    lateinit var clearBtn: AppCompatButton
    private lateinit var mAdView: AdView

    override fun onBackPressed() {
        if (isCheckOpen) {
            hideCheck()
        } else {
            startActivity(Intent(this, PhoneNoOptimizedActivity::class.java))
            finishAffinity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyApplication.get().setCurrentScreen(10)
        initViews()
    }

    private fun initViews() {

        screenOptimized = findViewById(R.id.screen_optimized)
        adsLoader = findViewById(R.id.ads_loader)
        clearBtn = findViewById(R.id.clear_btn)
        startAnimation()
        clearBtn.setOnClickListener {
            if (LocalSharedUtil.isStepOptimized(this, LocalSharedUtil.SHARED_THIRD)) {
                finishAffinity()
                startActivity(Intent(this, SecondMainActivity::class.java))
            } else {
                if (UStats.getUsageStatsList(this, false).isEmpty()) {
                    checkPermissionUsage(object : OnPermissionUsageListener {
                        override fun onPermissionAction() {
                            startActivity(
                                Intent(
                                    this@SecondOptimizationEndActivity,
                                    ThirdCleanActivity::class.java
                                )
                            )
                        }
                    })
                } else {
                    startActivity(Intent(this, ThirdCleanActivity::class.java))
                    finish()
                }
                clearBtn.setOnClickListener {
                    startActivity(Intent(this, ThirdCleanActivity::class.java))
                    finish()
                }
            }
        }
        initAds()
        LocalSharedUtil.setParameter(
            SharedData(Date().time.toString()),
            LocalSharedUtil.SHARED_SECOND,
            this
        )
        if(LocalSharedUtil.isNotificationOn(this))
            NotificationService.refresh()
    }

    private fun initAds() {
        mAdView = findViewById(R.id.adView)

        initializeBannerAd("ca-app-pub-3940256099942544~6300978111")

        loadBannerAd()
    }

    private fun initializeBannerAd(appUnitId: String) {

        MobileAds.initialize(
            this
        ) { initializationStatus: InitializationStatus? -> }

        // MobileAds.initialize(this, appUnitId)
    }

    private fun loadBannerAd() {
        val adRequest = AdRequest.Builder().build()
        val listener = object : AdListener() {
            override fun onAdLoaded() {
                hideLoader()
            }

            override fun onAdClosed() {
                hideLoader()
            }
            override fun onAdClicked() {
                super.onAdClicked()
                FirebaseLogger.log(FirebaseLogger.EventType.ADS_NATIVE_CLICK_EVENT_3)
            }

//            override fun onAdFailedToLoad(var1: Int) {
//                hideLoader()
//            }
        }
        mAdView.adListener = listener
        mAdView.loadAd(adRequest)
    }

    private fun startAnimation() {
        loaderAnimation =
            AnimationUtils.loadAnimation(this, R.anim.animation_loader)
        loaderAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {

            }

            override fun onAnimationRepeat(p0: Animation?) {
            }


        })
        adsLoader.animation = loaderAnimation
    }

    private fun hideLoader() {
        adsLoader.clearAnimation()
        loaderAnimation.cancel()
        loaderAnimation.reset()
        screenOptimized.visibility = View.GONE
    }
}