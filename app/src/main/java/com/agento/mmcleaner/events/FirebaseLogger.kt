package com.agento.mmcleaner.events

import com.agento.mmcleaner.MyApplication
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent

object FirebaseLogger {
    private val app: MyApplication get() = MyApplication.get()
    private val analytics get() = FirebaseAnalytics.getInstance(app)

    private const val DEFAULT_EVENT_ID = -1
    private const val APP_OPEN_EVENT_ID = 1
    private const val INTERSTITIAL_EVENT_ID = 2
    private const val NATIVE_EVENT_ID = 3

    enum class EventType {
        APP_INSTALLED,

        REFERRER_ANID,
        REFERRER_UTM_SOURCE,
        REFERRER_UTM_MEDIUM,
        REFERRER_UTM_TERM,
        REFERRER_UTM_CONTENT,
        REFERRER_UTM_CAMPAIGN,

        ADS_APP_OPEN_CLICK_EVENT_1,
        ADS_INTERSTITIAL_CLICK_EVENT_2,
        ADS_NATIVE_CLICK_EVENT_3,

        OPENED_FROM_WIDGET,
        OPENED_FROM_STATUS_BAR,
        OPENED_FROM_CHARGING_PUSH,
        OPENED_FROM_APP_DELETE_PUSH,
        OPENED_FROM_REGULAR_PUSH,

        LEAVE_FROM_SCREEN_NUMBER;

        fun appendWith(text: String) = "${name}_$text"
    }

    fun log(type: EventType) {
        //log firebase
        log(type.name)

        //log to tracker if it's ads event
        when (type) {
            EventType.ADS_APP_OPEN_CLICK_EVENT_1 -> APP_OPEN_EVENT_ID
            EventType.ADS_INTERSTITIAL_CLICK_EVENT_2 -> INTERSTITIAL_EVENT_ID
            EventType.ADS_NATIVE_CLICK_EVENT_3 -> NATIVE_EVENT_ID

            else -> DEFAULT_EVENT_ID

        }.let {
            if (it != DEFAULT_EVENT_ID) TrackerLogger.logEvent(it)
        }
    }

    fun log(type: EventType, text: String) = log(type.appendWith(text))

    fun log(type: EventType, number: Int) = log(type.appendWith(number.toString()))

    private fun log(text: String) = analytics.logEvent(text) {}
}