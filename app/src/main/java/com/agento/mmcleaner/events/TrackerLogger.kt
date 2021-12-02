package com.agento.mmcleaner.events

import android.util.Log
import com.agento.mmcleaner.MyApplication
import com.agento.mmcleaner.util.shared.LocalSharedUtil
import com.agento.mmcleaner.util.shared.LocalSharedUtil.CNV_ID_SHARED
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley


object TrackerLogger {
    private const val CNV_ID = "cnv_id"
    private const val EVENT = "event"

    private val app: MyApplication get() = MyApplication.get()
    private val cnvId: String get() = LocalSharedUtil.getStringParameter(CNV_ID_SHARED, app)

    private val baseTrackerUrl = "https://palundrus.com/click.php?id=${app.packageName}"

    private val cnvIdTrackerUrl get() = "$baseTrackerUrl&$CNV_ID=$cnvId"
    private fun configureEventTrackerUrl(eventNumber: Int) = "$cnvIdTrackerUrl&$EVENT$eventNumber=1"

    fun logCnvId() {
        if (cnvId.isEmpty()) return
        sendRequest(cnvIdTrackerUrl)
    }

    fun logEvent(eventNumber: Int) {
        if (cnvId.isEmpty()) return
        sendRequest(configureEventTrackerUrl(eventNumber))
    }

    private fun sendRequest(url: String) =
        StringRequest(
            //method
            Request.Method.GET,
            //request url
            url,
            //on response
            { response -> // Result handling
                Log.i("TAG", "sendRequest, response: ${response}")
            },
            //on error
            { error -> // Error handling
                Log.i("TAG", "Something went wrong!")
                error.printStackTrace()
            })
            .apply {
                // Add the request to the queue
                Log.i("TAG", "call url: $url")
                Volley.newRequestQueue(app).add(this)
            }
}