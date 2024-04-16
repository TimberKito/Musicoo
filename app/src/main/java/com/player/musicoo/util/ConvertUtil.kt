package com.player.musicoo.util

import android.content.Context
import android.content.pm.PackageManager

fun convertMillisToMinutesAndSecondsString(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = (totalSeconds / 60).toInt()
    val seconds = (totalSeconds % 60).toInt()
    return String.format("%02d:%02d", minutes, seconds)
}

fun getAppVersion(context: Context): String {
    return try {
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        pInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        "N/A"
    }
}