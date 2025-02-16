package com.mystilt.common

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

enum class AppPermission(
    val permission: String,
    val requestCode: Int,
    val rationale: String
) {
    CAMERA(
        android.Manifest.permission.CAMERA,
        100,
        "Camera permission is required to take photos and videos."
    ),
    LOCATION_FINE(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        101,
        "Precise location permission is required to show your current location."
    ),
    LOCATION_COARSE(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        102,
        "Approximate location permission is required to show your location."
    ),
    STORAGE_READ(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        103,
        "Storage permission is required to access files."
    ),
    STORAGE_WRITE(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        104,
        "Storage permission is required to save files."
    ),
    MICROPHONE(
        android.Manifest.permission.RECORD_AUDIO,
        105,
        "Microphone permission is required to record audio."
    );

    companion object {
        fun fromRequestCode(requestCode: Int): AppPermission? {
            return values().find { it.requestCode == requestCode }
        }
    }
}

object PermissionUtils {
    sealed class PermissionResult {
        data object Granted : PermissionResult()
        data object Denied : PermissionResult()
        data object ShowRationale : PermissionResult()
        data object PermanentlyDenied : PermissionResult()
    }

    fun checkPermission(context: Context, permission: AppPermission): PermissionResult {
        return when {
            ContextCompat.checkSelfPermission(
                context,
                permission.permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                PermissionResult.Granted
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                context as Activity,
                permission.permission
            ) -> {
                PermissionResult.ShowRationale
            }
            !hasAskedForPermission(context, permission.permission) -> {
                setPermissionAsked(context, permission.permission)
                PermissionResult.Denied
            }
            else -> {
                PermissionResult.PermanentlyDenied
            }
        }
    }

    private fun hasAskedForPermission(context: Context, permission: String): Boolean {
        val preferences = context.getSharedPreferences("PermissionPrefs", Context.MODE_PRIVATE)
        return preferences.getBoolean(permission, false)
    }

    private fun setPermissionAsked(context: Context, permission: String) {
        val preferences = context.getSharedPreferences("PermissionPrefs", Context.MODE_PRIVATE)
        preferences.edit().putBoolean(permission, true).apply()
    }
}
