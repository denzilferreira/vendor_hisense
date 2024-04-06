package com.hisense.einkservice.model

enum class EinkSpeed(private val speed: Int) {
    CLEAR(515),
    BALANCED(513),
    SMOOTH(518),
    FAST(521),
    ;

    fun getSpeed(): Int {
        return speed
    }

    companion object {
        fun fromSpeed(speed: Int): EinkSpeed {
            return when (speed) {
                515 -> CLEAR
                513 -> BALANCED
                518 -> SMOOTH
                521 -> FAST
                else -> throw IllegalArgumentException("Unknown speed: $speed")
            }
        }
    }
}
