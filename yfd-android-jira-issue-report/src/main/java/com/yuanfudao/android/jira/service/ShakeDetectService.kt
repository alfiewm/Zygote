package com.yuanfudao.android.jira.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import com.yuanfudao.android.jira.JIRAIssueReport
import com.yuanfudao.android.jira.ui.JIRAEntryActivity

class ShakeDetectService : Service() {

    companion object {
        private const val SHAKE_SLOP_TIME_MS = 200
        private const val SHAKE_INTERVAL_MS = 2000
    }

    private var lastShakeTimestamp: Long = 0
    private var shakeCount: Int = 0
    private var sensorManager: SensorManager? = null
    private var sensor: Sensor? = null

    private val shakeListener: SensorEventListener by lazy {
        object : SensorEventListener {

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // ignore
            }

            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val gX = x / SensorManager.GRAVITY_EARTH
                val gY = y / SensorManager.GRAVITY_EARTH
                val gZ = z / SensorManager.GRAVITY_EARTH

                val gForce = Math.sqrt((gX * gX + gY * gY + gZ * gZ).toDouble()).toFloat()
                if (gForce > 2.2f) {
                    val now = System.currentTimeMillis()
                    if (lastShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                        return
                    }
                    if (lastShakeTimestamp + SHAKE_INTERVAL_MS < now) {
                        shakeCount = 0
                    }
                    lastShakeTimestamp = now
                    shakeCount++
                    if (shakeCount == 3) {
                        shakeCount = 0
                        if (JIRAIssueReport.isAppForeground()) {
                            startActivity(
                                    Intent(this@ShakeDetectService, JIRAEntryActivity::class.java)
                                            .apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
                        }
                    }
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sensorManager = baseContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager?
        val sensors = sensorManager?.getSensorList(Sensor.TYPE_ACCELEROMETER) as List<Sensor>?
        if (sensors != null && sensors.isNotEmpty()) {
            sensor = sensors[0]
            sensorManager?.registerListener(shakeListener, sensor, SensorManager.SENSOR_DELAY_GAME)
        }
        return START_STICKY
    }

    override fun onDestroy() {
        sensorManager?.unregisterListener(shakeListener)
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
