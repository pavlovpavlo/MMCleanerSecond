package com.agento.mmcleaner.ui.notifications.utils.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_PACKAGE_ADDED
import android.content.Intent.ACTION_PACKAGE_FULLY_REMOVED
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import com.agento.mmcleaner.ui.notifications.model.types.NotificationType
import com.agento.mmcleaner.ui.notifications.utils.NotificationsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class AppActionsReceiver(val context: Context) : BroadcastReceiver() {
    companion object {
        private const val PACKAGE = "package"
        private const val DEFAULT_LABEL = ""

        private const val MAX_PERCENTS = 100
        private val FIRST_PERCENT_RANGE = 1..5
        private val THIRD_PERCENT_RANGE = 6..15

        private const val MIN_JUNK_SIZE = 20.5
        private const val MAX_JUNK_SIZE = 60.5
    }

    private var userAppsLogos = HashMap<String, Drawable>().apply {
        reload()
    }

    private fun HashMap<String, Drawable>.reload() {
        CoroutineScope(Dispatchers.IO).launch {
            clear()

            with(context.packageManager) {
                getInstalledApplications(0).forEach {
                    put(it.packageName, it.loadIcon(this))
                }
            }
        }
    }

    fun requestLogo(packageName: String): Drawable? = userAppsLogos[packageName]

    init {
        register(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_PACKAGE_FULLY_REMOVED) {
            val packageName = intent.data?.schemeSpecificPart ?: DEFAULT_LABEL
            val firstPercent = FIRST_PERCENT_RANGE.random()
            val thirdPercent = THIRD_PERCENT_RANGE.random()
            val secondPercent = MAX_PERCENTS - firstPercent - thirdPercent
            val randomPercents = listOf(firstPercent, secondPercent, thirdPercent)

            val junkSize = Random.nextDouble(MIN_JUNK_SIZE, MAX_JUNK_SIZE).toFloat()
            val junkSizeList = randomPercents.map {
                junkSize * it / 100f
            }

            NotificationsHelper.instance?.show(
                NotificationType.AppRemoved(
                    packageName = packageName,
                    sizeOfJunkMb = junkSize,
                    junkMbSizes = junkSizeList
                )
            )
        } else
            userAppsLogos.reload()
    }

    private fun register(context: Context) {
        context.registerReceiver(
            this,
            IntentFilter().apply {
                addAction(ACTION_PACKAGE_FULLY_REMOVED)
                addAction(ACTION_PACKAGE_ADDED)
                addDataScheme(PACKAGE)
            }
        )

        userAppsLogos = HashMap<String, Drawable>().apply { reload() }
    }
}