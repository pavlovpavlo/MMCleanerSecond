package com.agento.mmcleaner.ui.optimized

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import com.agento.mmcleaner.R
import com.agento.mmcleaner.events.FirebaseLogger
import com.agento.mmcleaner.ui.BaseActivity
import com.agento.mmcleaner.ui.main.SecondMainActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus

class PhoneOptimizedActivity : BaseActivity(R.layout.activity_phone_optimized) {

    lateinit var toMainBtn: AppCompatButton
    lateinit var stars: Array<ImageView>
    lateinit var loaderAnimation: Animation
    private lateinit var mAdView: AdView
    lateinit var adsLoader: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
    }

    private fun initViews() {
        adsLoader = findViewById(R.id.ads_loader)
        stars = arrayOf(
            findViewById(R.id.star1),
            findViewById(R.id.star2),
            findViewById(R.id.star3),
            findViewById(R.id.star4),
            findViewById(R.id.star5)
        )
        toMainBtn = findViewById(R.id.to_main)

        for (i in stars.indices) {
            stars[i].setOnClickListener {
                setStars(i + 1)
                if (i == 4) {
                    try {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=$packageName")
                            )
                        )
                    } catch (e: ActivityNotFoundException) {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                            )
                        )
                    }
                } else {
                    val selectorIntent = Intent(Intent.ACTION_SENDTO)
                    selectorIntent.data = Uri.parse("mailto:")

                    val emailIntent = Intent(Intent.ACTION_SEND)
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("support@agento.pro"))
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "My review for MM Cleaner app")
                    emailIntent.selector = selectorIntent

                    startActivity(
                        Intent.createChooser(
                            emailIntent,
                            "Send email..."
                        )
                    )
                }
            }
        }

        toMainBtn.setOnClickListener {
            finishAffinity()
            startActivity(Intent(this, SecondMainActivity::class.java))
        }
        initAds()
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
        startAnimation()
        val adRequest = AdRequest.Builder().build()
        val listener = object : AdListener() {
            override fun onAdLoaded() {
                hideLoader()
            }

            override fun onAdClosed() {
                hideLoader()
            }

            override fun onAdOpened() {
                super.onAdOpened()
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
        (adsLoader.parent as View).visibility = View.GONE
    }

    private fun setStars(countSelectStars: Int) {
        for (i in 0 until countSelectStars) {
            stars[i].setImageResource(R.drawable.ic_star_active)
        }

        for (i in (countSelectStars) until stars.size) {
            stars[i].setImageResource(R.drawable.ic_star_noactive)
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, SecondMainActivity::class.java))
        finishAffinity()
    }

}