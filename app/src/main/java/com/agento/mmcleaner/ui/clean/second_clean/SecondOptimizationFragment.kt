package com.agento.mmcleaner.ui.clean.second_clean

import android.animation.Animator
import android.app.usage.UsageStats
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import cn.septenary.ui.widget.GradientProgressBar
import com.agento.mmcleaner.MyApplication
import com.agento.mmcleaner.R
import com.agento.mmcleaner.scan_util.model.JunkInfo
import com.agento.mmcleaner.ui.BaseFragment
import com.agento.mmcleaner.ui.clean.first_clean.FirstCleanActivity
import com.agento.mmcleaner.util.GradientProgressBarAnimation
import com.agento.mmcleaner.util.SingletonClassApp
import com.agento.mmcleaner.util.UStats
import com.agento.mmcleaner.util.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception


class SecondOptimizationFragment : BaseFragment(R.layout.fragment_second_optimization) {

    lateinit var thisView: View
    lateinit var progressBar: GradientProgressBar
    lateinit var progressBarGreen: GradientProgressBar
    lateinit var progressBarOrange: GradientProgressBar
    lateinit var progressText: TextView
    lateinit var countProcess: TextView
    lateinit var complete: ImageView
    lateinit var appsContainer: ConstraintLayout
    lateinit var anim: GradientProgressBarAnimation
    private var allProgress: Int = 100

    companion object {
        var usage = mutableListOf<JunkInfo>()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MyApplication.get().setCurrentScreen(9)
        thisView = view

        initViews()
    }

    private fun initViews() {
        complete = thisView.findViewById(R.id.complete)
        appsContainer = thisView.findViewById(R.id.apps_container)
        progressBar = thisView.findViewById(R.id.bar2)
        progressBarGreen = thisView.findViewById(R.id.bar_green)
        progressBarOrange = thisView.findViewById(R.id.bar_orange)
        progressText = thisView.findViewById(R.id.progress_text)
        countProcess = thisView.findViewById(R.id.count_process)
        //usage = UStats.getUsageStatsList(requireContext())


        //startAnimation(0f, 100f, usage.size.toLong() * 900)
        startAnimationDeleteProcess()
    }

    private fun startAnimationDeleteProcess() {
        val lLayoutInflater: LayoutInflater =
            (requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)

        //val oneAppProccent:Float = 1F/usage.size.toFloat()
        val addProccent:Float = (1F/(usage.size.toFloat()-1)) *100F

        appsContainer.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
                view!!.height //height is ready
                kotlinx.coroutines.GlobalScope.launch(context = Dispatchers.Main) {
                    for (i in 1 until usage.size) {
                        val programData = usage[i]
                        val program: View =
                            lLayoutInflater.inflate(R.layout.item_process_icon, null)
                        val image: ImageView = program.findViewById<ImageView>(R.id.program_image)

                        val pm: PackageManager = requireActivity().packageManager
                        val icon: Drawable =try {
                            pm.getApplicationIcon(programData.mPackageName)
                        }catch (e: Exception){
                            resources.getDrawable(R.drawable.ic_document_text)
                        }
                        image.setImageDrawable(icon)
                        val layoutParams = ConstraintLayout.LayoutParams(
                            ConstraintLayout.LayoutParams.WRAP_CONTENT,
                            ConstraintLayout.LayoutParams.WRAP_CONTENT
                        )

                        layoutParams.bottomToBottom = ConstraintSet.PARENT_ID
                        layoutParams.endToEnd = ConstraintSet.PARENT_ID
                        layoutParams.startToStart = ConstraintSet.PARENT_ID
                        layoutParams.topToTop = ConstraintSet.PARENT_ID

                        program.layoutParams = layoutParams

                        appsContainer.addView(program)


                        allProgress-= Math.ceil(addProccent.toDouble()).toInt()
                        countProcess.text = "$i / ${usage.size - 1}"
                        if(allProgress<0)
                            allProgress=0
                        progressText.text = allProgress.toString()
                        if(allProgress<66){
                            if(allProgress< 33){
                                progressBar.visibility = View.GONE
                                progressBarOrange.visibility = View.GONE
                                progressBarGreen.visibility = View.VISIBLE
                            }else{
                                progressBar.visibility = View.GONE
                                progressBarGreen.visibility = View.GONE
                                progressBarOrange.visibility = View.VISIBLE
                            }
                        }

                        progressBar.setProgress(allProgress,true)
                        progressBarOrange.setProgress(allProgress,true)
                        progressBarGreen.setProgress(allProgress,true)

                        program.animate().scaleY(3.4f).scaleX(3.4f).alpha(0.7f)
                            .translationX(-(appsContainer.width.toFloat() / 2))
                            .setDuration(1200).setListener(object : Animator.AnimatorListener {
                                override fun onAnimationStart(p0: Animator?) {

                                }

                                override fun onAnimationEnd(p0: Animator?) {
                                    appsContainer.removeView(program)
                                }

                                override fun onAnimationCancel(p0: Animator?) {

                                }

                                override fun onAnimationRepeat(p0: Animator?) {

                                }

                            })

                        if (i != (usage.size - 1))
                            delay(650)
                    }
                    optimizationEnd()
                }

            }
        })

    }

    private fun startAnimation(from: Float, to: Float, duration: Long) {
        anim = GradientProgressBarAnimation(progressBar, from, to)
        anim.setProgress(progressText)
        anim.duration = duration
        progressBar.startAnimation(anim)
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                optimizationEnd()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    private fun optimizationEnd() {
        complete.visibility = View.VISIBLE
        startAds()
        SingletonClassApp.getInstance().start_ads=2
//        startActivity(Intent(requireContext(), SecondOptimizationEndActivity::class.java))
//        requireActivity().finish()
    }
}