package com.vluk4.example.parallax.model

import com.vluk4.parallax.i
import com.vluk4.parallax.round2Decimals

internal data class SensorData(
    var roll: Float = 0f,
    var pitch: Float = 0f,
) {
    init {
        this.roll = roll.round2Decimals()
        this.pitch = pitch.round2Decimals()
        i(this.toString())
    }
}