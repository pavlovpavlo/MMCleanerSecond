package com.agento.mmcleaner.ui.clean.second_clean

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.animation.addListener
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import cn.septenary.ui.widget.GradientProgressBar
import com.agento.mmcleaner.R
import com.agento.mmcleaner.ui.BaseFragment
import com.agento.mmcleaner.util.Util
import com.agento.mmcleaner.util.UtilPhoneInfo


class SecondScanFragment : BaseFragment(R.layout.fragment_second_scan) {

    lateinit var thisView: View
    lateinit var procText: TextView
    lateinit var memData: TextView
    lateinit var bar: GradientProgressBar
    lateinit var progressBarGreen: GradientProgressBar
    lateinit var progressBarOrange: GradientProgressBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        thisView = view
        initViews()
    }

    private fun initViews(){
        bar = thisView.findViewById(R.id.bar2)
        progressBarGreen = thisView.findViewById(R.id.bar_green)
        progressBarOrange = thisView.findViewById(R.id.bar_orange)
        procText = thisView.findViewById(R.id.proc_text)
        memData = thisView.findViewById(R.id.mem_data)

        val procentUse = (50..90).random()
        SecondScanEndFragment.procentUse = procentUse
        val totalRam = UtilPhoneInfo.getTotalRAM()
        val totalRamDigit = totalRam.substring(0, totalRam.indexOf(" ")).toLong()

        startCountAnimation(procentUse)
        memData.text = "${(totalRamDigit.toDouble() *procentUse.toDouble())/100.0} GB / ${UtilPhoneInfo.getTotalRAM()}"

        if(procentUse<66){
            if(procentUse< 33){
                bar.visibility = View.GONE
                progressBarOrange.visibility = View.GONE
                progressBarGreen.visibility = View.VISIBLE
            }else{
                bar.visibility = View.GONE
                progressBarGreen.visibility = View.GONE
                progressBarOrange.visibility = View.VISIBLE
            }
        }

        bar.setProgress(procentUse, true)
        progressBarGreen.setProgress(procentUse, true)
        progressBarOrange.setProgress(procentUse, true)


        /*bar.animate().rotation(1080f).setListener(object : Animator.AnimatorListener{
            override fun onAnimationStart(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {
                openNextScreen()
            }

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationRepeat(p0: Animator?) {

            }

        }).setDuration(4000L).start()*/
    }

    private fun startCountAnimation(endValue: Int) {
        val animator = ValueAnimator.ofInt(0, endValue)
        animator.duration = 4000
        animator.addUpdateListener { animation ->
            procText.text = "${animation.animatedValue.toString()}" + getString(R.string.employed)}
        animator.addListener(object: Animator.AnimatorListener{
            override fun onAnimationStart(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                openNextScreen()
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationRepeat(p0: Animator?) {
            }

        })
        animator.start()
    }

    private fun openNextScreen(){
        val navBuilder = NavOptions.Builder()
        val controller = NavHostFragment.findNavController(this@SecondScanFragment)
        controller.navigate(R.id.fragment_second_scan_end, null, Util.generateNavOptions())
    }
}