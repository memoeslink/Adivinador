@file:JvmName("ActivityExtensions")

package com.app.memoeslink.adivinador.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.WindowManager

@SuppressLint("SourceLockedOrientationActivity")
fun Activity?.lockScreenOrientation() {
    if (this?.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT) this.requestedOrientation =
        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    else this?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
}

fun Activity?.unlockScreenOrientation() {
    this?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
}

fun Activity?.setContinuance(active: Boolean) {
    if (active) this?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) else this?.window?.clearFlags(
        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
    )
}