package com.agento.mmcleaner.ui.clean.third_clean

import android.animation.Animator
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.agento.mmcleaner.R
import com.agento.mmcleaner.util.Util


class ThirdScanFragment : Fragment(R.layout.fragment_third_scan) {

    lateinit var thisView: View
    lateinit var scanner: ImageView
    lateinit var scannerImage: ImageView
    lateinit var scannerOrangeSmileImage: ImageView
    lateinit var scannerBorder: ImageView
    lateinit var textScanner: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        thisView = view
        initViews()
    }

    private fun initViews(){
        scanner = thisView.findViewById(R.id.scanner)
        scannerImage = thisView.findViewById(R.id.battery_image)
        scannerOrangeSmileImage = thisView.findViewById(R.id.orange_smile)
        scannerBorder = thisView.findViewById(R.id.battery_border)
        textScanner = thisView.findViewById(R.id.text_battery)

        scanner.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
                view!!.height //height is ready
                scanner.animate()
                    .translationY(-((view!!.height) / 4).toFloat())
                    .setInterpolator(AccelerateInterpolator())
                    .setDuration(3000)
                    .setListener(object:Animator.AnimatorListener{
                        override fun onAnimationStart(p0: Animator?) {
                        }

                        override fun onAnimationEnd(p0: Animator?) {
                            startNextAnimation(view!!.height)
                        }

                        override fun onAnimationCancel(p0: Animator?) {
                        }

                        override fun onAnimationRepeat(p0: Animator?) {
                        }

                    })
            }
        })

    }

    private fun startNextAnimation(height: Int){
        scanner.setImageResource(R.drawable.battery_scaner_orange)
        scannerImage.setImageResource(R.drawable.battery_empty)
        scannerBorder.setImageResource(R.drawable.ic_battery_orange_border)
        scannerOrangeSmileImage.visibility = View.VISIBLE
        //textScanner.text = "Usage analysis\nbatteries ..."
        scanner.animate()
            .translationY((height / 12).toFloat())
            .setInterpolator(AccelerateInterpolator())
            .setDuration(3000)
            .setListener(object:Animator.AnimatorListener{
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
    }

    private fun openNextScreen(){
        val navBuilder = NavOptions.Builder()
        val controller = NavHostFragment.findNavController(this@ThirdScanFragment)
        val navOptions = navBuilder.setPopUpTo(R.id.fragment_third_scan_end, true).build()
        controller.navigate(R.id.fragment_third_scan_end, null, Util.generateNavOptions())
    }
}