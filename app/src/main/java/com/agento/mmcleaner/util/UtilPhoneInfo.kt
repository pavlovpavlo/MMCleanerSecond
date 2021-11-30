package com.agento.mmcleaner.util

import android.os.Build
import java.io.*
import java.text.DecimalFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class UtilPhoneInfo {
    companion object {
        fun getDeviceName(): String {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (model.toLowerCase(Locale.getDefault())
                    .startsWith(manufacturer.toLowerCase())
            ) {
                capitalize(model)
            } else {
                capitalize(manufacturer) + " " + model
            }
        }

        fun getTotalRAM(): String {
            var reader: RandomAccessFile? = null
            var load: String? = null
            val twoDecimalForm = DecimalFormat("#")
            var totRam = 0.0
            var lastValue = ""
            try {
                reader = RandomAccessFile("/proc/meminfo", "r")
                load = reader.readLine()

                // Get the Number value from the string
                val p: Pattern = Pattern.compile("(\\d+)")
                val m: Matcher = p.matcher(load)
                var value = ""
                while (m.find()) {
                    value = m.group(1)
                    // System.out.println("Ram : " + value);
                }
                reader.close()
                totRam = value.toDouble()
                // totRam = totRam / 1024;
                val mb = totRam / 1024.0
                val gb = totRam / 1048576.0
                val tb = totRam / 1073741824.0
                lastValue = if (tb > 1) {
                    twoDecimalForm.format(tb) + (" TB")
                } else if (gb > 1) {
                    twoDecimalForm.format(gb) +(" GB")
                } else if (mb > 1) {
                    twoDecimalForm.format(mb) +(" MB")
                } else {
                    twoDecimalForm.format(totRam) +(" KB")
                }
            } catch (ex: IOException) {
                ex.printStackTrace()
            } finally {
                // Streams.close(reader);
            }
            return lastValue
        }

        fun toNormalFormat(size: Double, format: String = "#"): String{
            val twoDecimalForm = DecimalFormat(format)
            val kb = size* 0.000977
            val mb = kb / 1024.0
            val gb = kb / 1048576.0
            val tb = kb / 1073741824.0
            return when {
                tb > 1 -> {
                    twoDecimalForm.format(tb) + (" TB")
                }
                gb > 1 -> {
                    twoDecimalForm.format(gb) +(" GB")
                }
                mb > 1 -> {
                    twoDecimalForm.format(mb) +(" MB")
                }
                else -> {
                    twoDecimalForm.format(kb) +(" KB")
                }
            }
        }

        fun getCPUName(): String{
            try {
                val fr = FileReader("/proc/cpuinfo")
                val br = BufferedReader(fr)
                val text: String = br.readLine()
                val array = text.split(":\\s+").toTypedArray()
                for (i in array.indices) {
                }
                return array[0]
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return "0"
        }

        private fun capitalize(s: String?): String {
            if (s == null || s.isEmpty()) {
                return ""
            }
            val first = s[0]
            return if (Character.isUpperCase(first)) {
                s
            } else {
                Character.toUpperCase(first).toString() + s.substring(1)
            }
        }
    }
}