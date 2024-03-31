package com.hisense.einkservice

enum class EinkSpeed(private val speed: Int) {
    CLEAR(515),
    BALANCED(513),
    SMOOTH(518),
    FAST(521);

    fun getSpeed(): Int {
        return speed
    }
}