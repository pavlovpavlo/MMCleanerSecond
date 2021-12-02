package com.agento.mmcleaner.events

import android.content.Context
import android.util.Log
import com.agento.mmcleaner.util.shared.LocalSharedUtil
import com.agento.mmcleaner.util.shared.LocalSharedUtil.CNV_ID_SHARED
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import java.net.URLDecoder

class AppInstallReferrer(val context: Context) {
    companion object {
        private const val ATTEMPTS_ALLOWED = 2
        private const val FIRST_ATTEMPT = 1
        private const val UTF_8_ENC = "UTF-8"

        private const val SPLIT_BY_AND = "&"

        private const val CNV_ID = "cnv_id"
        private const val ANID = "anid"
        private const val UTM_SOURCE = "utm_source"
        private const val UTM_MEDIUM = "utm_medium"
        private const val UTM_TERM = "utm_term"
        private const val UTM_CONTENT = "utm_content"
        private const val UTM_CAMPAIGN = "utm_campaign"

        private val REFERRER_PARAMS = arrayOf(
            CNV_ID,
            ANID,
            UTM_SOURCE,
            UTM_MEDIUM,
            UTM_TERM,
            UTM_CONTENT,
            UTM_CAMPAIGN
        )
    }

    private var connectionAttempt = FIRST_ATTEMPT
    private val referrerClient = InstallReferrerClient.newBuilder(context).build()

    init {
        startConnection()
    }

    private fun startConnection() {
        if (connectionAttempt > ATTEMPTS_ALLOWED) return

        referrerClient.startConnection(object : InstallReferrerStateListener {

            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
                    onInstalled()
                }
            }

            override fun onInstallReferrerServiceDisconnected() {
                if (connectionAttempt < ATTEMPTS_ALLOWED) {
                    connectionAttempt++
                    startConnection()
                }
            }
        })

    }

    private fun onInstalled() {
        FirebaseLogger.log(FirebaseLogger.EventType.APP_INSTALLED)

        val referrerUrl = try {
            URLDecoder.decode(
                referrerClient.installReferrer.installReferrer,
                UTF_8_ENC
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
            return
        }

        Log.i("TAG", "onInstalled: $referrerUrl")

        val splitValues = try {
            referrerUrl.split(SPLIT_BY_AND).apply {
                forEach {
                    Log.i("TAG", "splitted: $it")
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            Log.e("TAG", "${ex.message}")
            return
        }

        Log.i("TAG", "split values amount: ${splitValues.size}")

        val cnvId = try {
            splitValues.find {
                it.contains(CNV_ID, ignoreCase = true)
            }?.replace("$CNV_ID=", "") ?: ""
        } catch (ex: Exception) {
            ex.printStackTrace()
            Log.e("TAG", "${ex.message}")
            return
        }

        Log.i("TAG", "cnv id: $cnvId")
        LocalSharedUtil.setParameter(cnvId, CNV_ID_SHARED, context)
        TrackerLogger.logCnvId()

        REFERRER_PARAMS.forEach { param ->
            val key = "$param="

            splitValues.find { it.contains(key, ignoreCase = true) }?.let { keyValuePair ->
                val value = keyValuePair.replace(key, "", ignoreCase = true)

                val eventType = FirebaseLogger.EventType.values().find { eventType ->
                    eventType.name.contains(param, ignoreCase = true)
                }

                if (eventType != null) {
                    Log.i("TAG", "LOG TO FIREBASE: $eventType - $value")
                    FirebaseLogger.log(eventType, value)
                }
            }
        }
    }
}