package com.app.memoeslink.adivinador.extensions

import android.graphics.Rect
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.core.view.isVisible

fun View?.isObservable(): Boolean {
    if (this == null || !this.isVisible || !this.isShown) return false
    val rect = Rect()
    return this.getGlobalVisibleRect(rect) && this.height == rect.height() && this.width == rect.width()
}

fun View?.fadeAndShow() {
    if (this == null) return
    this.clearAnimation()
    val alphaAnimation = AlphaAnimation(1.0f, 0.0f)
    alphaAnimation.duration = 350
    alphaAnimation.repeatCount = 1
    alphaAnimation.repeatMode = Animation.REVERSE
    this.startAnimation(alphaAnimation)
}