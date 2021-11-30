package com.agento.mmcleaner.util

import android.app.ActivityManager
import android.content.Context
import android.content.pm.IPackageDataObserver
import android.content.pm.PackageManager
import android.os.Handler
import android.os.RemoteException
import com.agento.mmcleaner.MyApplication
import com.agento.mmcleaner.scan_util.model.JunkInfo
import java.io.File
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*


class CleanUtil {
    fun freeAllAppsCache(junks: ArrayList<JunkInfo>) {
        val context: Context = MyApplication.getInstance()
        val externalDir: File = context.getExternalCacheDir() ?: return
        val pm: PackageManager = context.getPackageManager()
        for (info in junks) {
            if (info.isCheck) {
                val externalCacheDir: String = externalDir.getAbsolutePath()
                    .replace(context.getPackageName(), info.mPackageName)
                val externalCache = File(externalCacheDir)
                if (externalCache.exists() && externalCache.isDirectory()) {
                    deleteFile(externalCache)
                }
            }
        }
        var hanged = true
        try {
            val freeStorageAndNotify: Method = pm.javaClass
                .getMethod(
                    "freeStorageAndNotify",
                    Long::class.javaPrimitiveType,
                    IPackageDataObserver::class.java
                )
            val freeStorageSize = Long.MAX_VALUE
            freeStorageAndNotify.invoke(pm, freeStorageSize, object : IPackageDataObserver.Stub() {
                @Throws(RemoteException::class)
                override fun onRemoveCompleted(packageName: String, succeeded: Boolean) {

                }
            })
            hanged = false
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        if (hanged) {

        }
    }

    fun deleteFile(file: File): Boolean {
        if (file.isDirectory()) {
            val children: Array<String> = file.list()
            for (name in children) {
                val suc = deleteFile(File(file, name))
                if (!suc) {
                    return false
                }
            }
        }
        return file.delete()
    }

    fun killAppProcesses(packageName: String?) {
        if (packageName == null || packageName.isEmpty()) {
            return
        }
        val am = MyApplication.getInstance()
            .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        am.killBackgroundProcesses(packageName)
    }

    fun freeJunkInfos(junks: ArrayList<JunkInfo>) {
        for (info in junks) {
            val file = File(info.mPath)
            if (file != null && file.exists()) {
                file.delete()
            }
        }

    }
}