package com.agento.mmcleaner.util

import android.Manifest
import android.annotation.TargetApi
import android.app.AppOpsManager
import android.app.AppOpsManager.OnOpChangedListener
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Process
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import com.agento.mmcleaner.ui.MainActivity


class UtilPermissions {
    companion object {
        fun isPermissionDenied(context: AppCompatActivity, selfPermission: Boolean): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    false
                } else {
                    if (selfPermission) {
                        val intent = Intent()
                        intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                        val uri: Uri = Uri.fromParts("package", context.getPackageName(), null)
                        intent.data = uri
                        context.startActivity(intent)
                        true
                    } else
                        true

                }
            } else {

                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    false
                } else {
                    if (selfPermission) {
                        context.requestPermissions(
                            arrayOf(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ),
                            110011
                        )
                        true
                    } else
                        true
                }

            }
        }

    }
}