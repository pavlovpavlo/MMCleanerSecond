package com.agento.mmcleaner.events

import com.agento.mmcleaner.MyApplication
import com.agento.mmcleaner.util.shared.LocalSharedUtil
import com.agento.mmcleaner.util.shared.LocalSharedUtil.CNV_ID_SHARED
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley


object TrackerLogger {
    private val app: MyApplication get() = MyApplication.get()
    private val cnvId: String get() = LocalSharedUtil.getStringParameter(CNV_ID_SHARED, app)

    private val baseTrackerUrl = "https://tracker.com/click.php?id=${app.packageName}"

    private val cnvIdTrackerUrl get() = "$baseTrackerUrl?cnv_id=$cnvId"
    private fun configureEventTrackerUrl(eventNumber: Int) = "$cnvIdTrackerUrl&event$eventNumber=1"

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
                println(response.substring(0, 100))
            },
            //on error
            { error -> // Error handling
                println("Something went wrong!")
                error.printStackTrace()
            })
            .apply {
                // Add the request to the queue
                Volley.newRequestQueue(app).add(this)
            }
}