package com.agento.mmcleaner.ui.clean.third_clean

import android.app.usage.UsageStats
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.agento.mmcleaner.MyApplication
import com.agento.mmcleaner.R
import com.agento.mmcleaner.scan_util.model.JunkInfo
import com.agento.mmcleaner.ui.BaseFragment
import com.agento.mmcleaner.ui.clean.second_clean.SecondOptimizationEndActivity
import com.agento.mmcleaner.util.SingletonClassApp
import com.agento.mmcleaner.util.UStats
import com.agento.mmcleaner.util.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ThirdOptimizationFragment : BaseFragment(R.layout.fragment_third_optimization) {

    lateinit var thisView: View
    lateinit var adsLoader: ImageView
    lateinit var textHibernation: TextView
    lateinit var loaderAnimation: Animation
    companion object {
        var usage = mutableListOf<JunkInfo>()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MyApplication.get().setCurrentScreen(13)
        thisView = view

    }

    override fun onResume() {
        super.onResume()
        initViews()
    }

    private fun initViews() {
        adsLoader = thisView.findViewById(R.id.ads_loader)
        textHibernation = thisView.findViewById(R.id.text_hibernation)
        //usage = UStats.getUsageStatsList(requireContext())
        startAnimation()
        kotlinx.coroutines.GlobalScope.launch(context = Dispatchers.Main) {
            for (i in 1 until usage.size) {
                textHibernation.text = "Hibernation ${i} of ${usage.size-1}"
                if (i != (usage.size - 1))
                    delay(750)
            }
            openNextStep()
        }
    }

    private fun startAnimation() {
        loaderAnimation =
            AnimationUtils.loadAnimation(context, R.anim.animation_loader)
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

    private fun openNextStep(){
        startAds();
       adsLoader.clearAnimation()
        loaderAnimation.cancel()
        loaderAnimation.reset()
   SingletonClassApp.getInstance().start_ads=3
    //        startActivity(Intent(requireContext(), ThirdOptimizationEndActivity::class.java))
//        requireActivity().finish()
    }

}