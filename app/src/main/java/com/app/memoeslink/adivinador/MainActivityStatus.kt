package com.app.memoeslink.adivinador

data class MainActivityStatus(
        var forceEffects: Boolean = true,
        var timerEnabled: Boolean = false,
        var screenMeasured: Boolean = false,
        var adAdded: Boolean = false,
        var adPaused: Boolean = false,
        var coroutineStarted: Boolean = false,
        var nameInGeneration: Boolean = false,
        var screenWidth: Int = 0,
        var screenHeight: Int = 0,
        var originInX: Int = 0,
        var originInY: Int = 0,
        var seconds: Int = 0,
        var updateSeconds: Int = 0,
        var resourceSeconds: Int = 0,
        var adSeconds: Int = 0,
        var refreshFrequency: Int = 20,
        var updateFrequency: Int = 60,
        var measuredTimes: Long = 0,
        var confettiThrown: Long = 0
)