package com.vluk4.example.parallax.sensor

import android.content.Context
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.vluk4.example.parallax.model.SensorData
import com.vluk4.parallax.i
import com.vluk4.parallax.w
import kotlinx.coroutines.channels.Channel

internal class SensorDataManager(context: Context) : SensorEventListener {

    private val sensorManager by lazy { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    private val accelerometer by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) ?: null.also { w("No sensor for TYPE_ACCELEROMETER. See if your device as TYPE_GRAVITY sensor or similar to replace this") }
    }
    private val magnetometer by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) ?: null.also { w("No sensor for TYPE_MAGNETIC_FIELD") }
    }
    private var geomagnetic: FloatArray? = null
    private var gravity: FloatArray? = null
    private var screenOrientation: Int = Configuration.ORIENTATION_PORTRAIT

    val data by lazy { Channel<SensorData>(Channel.UNLIMITED) }

    private var lastProcessingTime: Long = 0

    fun init(newScreenOrientation: Int) {
        logDetectedSensors()
        screenOrientation = newScreenOrientation
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME)
    }

    fun cancel() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val currentTime = System.currentTimeMillis()
        val processingInterval = 1000L / 60L // Interval in milliseconds
        if (currentTime - lastProcessingTime >= processingInterval || true) {
            if(event!=null) {
                when (event.sensor.type) {
                    Sensor.TYPE_MAGNETIC_FIELD -> geomagnetic = event.values
                    Sensor.TYPE_ACCELEROMETER -> gravity = event.values
                    else -> w("Unhandled sensor type ${event.sensor.stringType}")
                }

                if (gravity != null && geomagnetic != null) {
                    //i("onSensorChanged: ${it.sensor.stringType} with ${it.values}")
                    processSensorData()
                    lastProcessingTime = currentTime
                }
            } else {
                w("SensorEvent is null")
            }
        }
    }

    private fun processSensorData() {
        val r = FloatArray(9)
        val i = FloatArray(9)

        if (SensorManager.getRotationMatrix(r, i, gravity, geomagnetic)) {
            val orientationZXY = FloatArray(3)
            SensorManager.getOrientation(r, orientationZXY)

            val (pitch, roll) = when (screenOrientation) {
                Configuration.ORIENTATION_PORTRAIT -> Pair(orientationZXY[1], orientationZXY[2])
                else -> Pair(orientationZXY[2], -orientationZXY[1])
            }

            val maxVerticalInclination = -1.5f //if its this, the phone is completely vertical
            val maxVerticalDeclination = 0.5f //like if you were to drop your phone fall back
            val maxHorizontalTiltLeft= -1.5f
            val maxHorizontalTiltRight = 1.5f
            if (pitch in maxVerticalInclination..maxVerticalDeclination &&
                roll in maxHorizontalTiltLeft..maxHorizontalTiltRight
            ) {
                data.trySend(SensorData(roll = roll, pitch = pitch))
            } else {
                // phone is not too inclined/declined vertically or turned sideways
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun logDetectedSensors(){
        val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
        i("Sensors found = ${deviceSensors.map { "Name=${it.name}, Type=${it.stringType.replace("android.sensor.", "")}" }}")
    }
}

