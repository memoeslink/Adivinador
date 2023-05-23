package com.app.memoeslink.adivinador

data class ActivityStatus(
    var initialized: Boolean = false,
    var screenMeasured: Boolean = false,
    var adAdded: Boolean = false,
    var adPaused: Boolean = false,
    var screenWidth: Int = 0,
    var screenHeight: Int = 0,
    var measurements: MutableMap<String, Int> = mutableMapOf(),
    var tasks: MutableSet<String> = mutableSetOf(),
    var seconds: Int = 0,
    var updateSeconds: Int = 0,
    var resourceSeconds: Int = 0,
    var adSeconds: Int = 0,
    var measuredTimes: Long = 0,
    var confettiThrown: Long = 0
)