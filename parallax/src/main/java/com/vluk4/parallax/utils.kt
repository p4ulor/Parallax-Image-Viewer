package com.vluk4.parallax

import android.util.Log
import java.math.RoundingMode
import java.text.DecimalFormat

const val LOG_TAG = "!!!"
const val APP_TAG = "ParallaxViewFork"

inline fun <reified T> T.i(s: String){
    Log.i(T::class.java.simpleName, s)
}

inline fun <reified T> T.w(s: String){
    Log.w(T::class.java.simpleName, LOG_TAG+s)
}

inline fun i(s: String) = Log.i(APP_TAG, s)

fun Float.round2Decimals() : Float {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING
    return df.format(this).toFloat()
}