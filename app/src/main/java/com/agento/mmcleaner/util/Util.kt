package com.agento.mmcleaner.util

import android.content.Context
import android.os.BatteryManager
import androidx.navigation.NavOptions
import com.agento.mmcleaner.R
import java.io.BufferedReader
import java.io.InputStreamReader


class Util {
    companion object{
        fun generateNavOptions(): NavOptions{
            val navBuilder = NavOptions.Builder()
            navBuilder.setEnterAnim(R.anim.slide_in_right).setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.slide_in_left).setPopExitAnim(R.anim.slide_out_right)
            return navBuilder.build()
        }

        fun getBatteryPercentage(context: Context): Int {
            val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        }
        @JvmStatic
        fun cpuTemperature(): Float {
            val process: Process
            return try {
                process = Runtime.getRuntime().exec("cat sys/class/thermal/thermal_zone0/temp")
                process.waitFor()
                val reader = BufferedReader(InputStreamReader(process.inputStream))
                val line = reader.readLine()
                if (line != null) {
                    val temp = line.toFloat()
                    temp / 1000.0f
                } else {
                    51.0f
                }
            } catch (e: Exception) {
                e.printStackTrace()
                40.1f
            }
        }

    }
}