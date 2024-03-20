@file:JvmName("ViewExtensions")

package com.app.memoeslink.adivinador.extensions

import android.graphics.Rect
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import androidx.core.view.isVisible

fun View?.isObservable(): Boolean {
    if (this == null || !this.isVisible || !this.isShown) return false
    val rect = Rect()
    return this.getGlobalVisibleRect(rect) && this.height == rect.height() && this.width == rect.width()
}

fun View?.fadeIn(offset: Long = 0, duration: Long = 1000) {
    if (this == null) return
    this.clearAnimation()
    val animation = AlphaAnimation(0.0f, 1.0f)
    animation.interpolator = DecelerateInterpolator()
    animation.startOffset = offset
    animation.duration = duration
    this.startAnimation(animation)
}

fun View?.fadeOut(offset: Long = 0, duration: Long = 1000) {
    if (this == null) return
    this.clearAnimation()
    val animation = AlphaAnimation(1.0f, 0.0f)
    animation.interpolator = AccelerateInterpolator()
    animation.startOffset = offset
    animation.duration = duration
    this.startAnimation(animation)
}

fun View?.fadeAndShow(offset: Long = 0, duration: Long = 1000) {
    if (this == null) return
    this.clearAnimation()
    val animation = AlphaAnimation(1.0f, 0.0f)
    animation.startOffset = offset
    animation.duration = duration
    animation.repeatCount = 1
    animation.repeatMode = Animation.REVERSE
    this.startAnimation(animation)
}