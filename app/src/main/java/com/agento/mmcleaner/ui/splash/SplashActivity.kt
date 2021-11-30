package com.agento.mmcleaner.ui.splash

import android.app.Application
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import android.util.Log
import android.view.animation.Animation
import android.widget.ProgressBar
import android.widget.TextView
import com.agento.mmcleaner.MyApplication
import com.agento.mmcleaner.R
import com.agento.mmcleaner.ui.BaseActivity
import com.agento.mmcleaner.ui.MainActivity
import com.agento.mmcleaner.ui.PrivacyPolicyActivity
import com.agento.mmcleaner.ui.main.FirstMainActivity
import com.agento.mmcleaner.ui.main.SecondMainActivity
import com.agento.mmcleaner.util.ProgressBarAnimation
import com.agento.mmcleaner.util.SingletonClassApp
import com.agento.mmcleaner.util.UtilNotif
import com.agento.mmcleaner.util.shared.LocalSharedUtil


class SplashActivity : BaseActivity(R.layout.activity_splash) {
    private lateinit var cameraManager: CameraManager
    private lateinit var cameraId: String

    override fun onResume() {
        super.onResume()
        initViews()


//        if (SingletonClassApp.getInstance().ads_close){
//            if (LocalSharedUtil.isFirstMainShared(this)){
//                startActivity(Intent(applicationContext, SecondMainActivity::class.java))}
//            else
//                startActivity(Intent(applicationContext, FirstMainActivity::class.java))
//            finish()
//            SingletonClassApp.getInstance().ads_close = false}




}

    private fun switchFlashLight(status: Boolean) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, status)
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }
    override fun onBackPressed() {
    }

    private fun initViews() {
        val newString: String?
        newString =intent.extras?.getString("notif")
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            cameraId = cameraManager.cameraIdList[0]
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

        if (newString.equals("lum")){
            switchFlashLight(true)
            LocalSharedUtil.setParameterInt(1, LocalSharedUtil.SHARED_LUMUS, this)

        }else if (newString.equals("lum_act")){
            switchFlashLight(false)
            LocalSharedUtil.setParameterInt(0, LocalSharedUtil.SHARED_LUMUS, this)
        }
        val progressView = findViewById<ProgressBar>(R.id.progress)
        val privacyPolicy = findViewById<TextView>(R.id.privacy_policy)

        if(LocalSharedUtil.isNotificationOn(this))
        UtilNotif.showScheduleNotification(this)

        privacyPolicy.setOnClickListener {
            startActivity(Intent(applicationContext, PrivacyPolicyActivity::class.java))
        }

        val anim = ProgressBarAnimation(progressView, 0f, 100f)
        anim.duration = 6000
        progressView.startAnimation(anim)
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                //   startNextActivity()


                val application: Application = application

                // If the application is not an instance of MyApplication, log an error message and
                // start the MainActivity without showing the app open ad.

                // If the application is not an instance of MyApplication, log an error message and
                // start the MainActivity without showing the app open ad.
                /*if (application !is MyApplication) {
                    Log.e("LOG_TAG", "Failed to cast application to MyApplication.")
                    startNextActivity()
                    return
                }*/
                startNextActivity()

                // Show the app open ad.

                // Show the app open ad.
                /*(application as MyApplication)
                        .showAdIfAvailable(
                                this@SplashActivity
                        ) { startNextActivity() }*/


            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    private fun startNextActivity() {

//        val intent = Intent(this, MainActivity::class.java)
//        this.startActivity(intent)
        SingletonClassApp.getInstance().start=true;
        SingletonClassApp.getInstance().block=true;
        SingletonClassApp.getInstance().ads_close = false
        SingletonClassApp.getInstance().ads=false;
        if (LocalSharedUtil.isFirstMainShared(this)){
            startActivity(Intent(applicationContext, SecondMainActivity::class.java))}
        else {
            startActivity(Intent(applicationContext, FirstMainActivity::class.java))
        }
        finish()

//       if(!SingletonClassApp.getInstance().ads){
//      //  initAdsMain()
//            }else{
//        if (SingletonClassApp.getInstance().ads_close){
//           if (LocalSharedUtil.isFirstMainShared(this)){
//               startActivity(Intent(applicationContext, SecondMainActivity::class.java))}
//           else
//           startActivity(Intent(applicationContext, FirstMainActivity::class.java))
//           finish()
//           SingletonClassApp.getInstance().ads_close = false}
//        }





    }
}