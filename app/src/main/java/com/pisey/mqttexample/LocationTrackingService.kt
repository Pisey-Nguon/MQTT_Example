package com.pisey.mqttexample

import android.Manifest
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationTrackingService : LifecycleService() {

    companion object{
        fun start(context: Context){
            val intent = Intent(context,LocationTrackingService::class.java)
            ContextCompat.startForegroundService(context,intent)
        }
        fun stop(context: Context){
            val intent = Intent(context,LocationTrackingService::class.java)
            context.stopService(intent)
        }
    }
    private val topicUpdateLocation = "/topic/driver/location"
    private val topicSubscriptionLocationControl = "/topic/driver/location/b6c4b7d8-6f08-45e1-8400-a01209df1f32"
    private val mqttHelper by lazy { MqttHelper(this) }

    private val locationManager by lazy { getSystemService(LocationManager::class.java) }
    private val appNotification by lazy { AppNotification(this) }
    private val notificationId by lazy { System.currentTimeMillis().toInt() }
    private val openAppPendingIntent by lazy { PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE) }


    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            mqttHelper.publish(
                topic = topicUpdateLocation,
                payload = PayloadLocation(
                    driverId = "",
                    lat = location.latitude,
                    lon = location.longitude,
                ).toJsonString().toByteArray()
            )

        }

        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onCreate() {
        super.onCreate()
        lifecycleScope.launch(Dispatchers.IO) {
            mqttHelper.connect{
                mqttHelper.subscribe(topicSubscriptionLocationControl){
                    val response = it.fromJsonStringToDataClass(PayloadLocationControl::class.java)
                }
            }
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            startForeground(notificationId, appNotification.getForegroundNotification("Service not running", "", openAppPendingIntent))
            return super.onStartCommand(intent, flags, startId)
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1000,
            0.1f,
            locationListener
        )
        startForeground(notificationId, appNotification.getForegroundNotification("Service is running", "", openAppPendingIntent))
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        mqttHelper.disconnect()
        locationManager.removeUpdates(locationListener)
    }


}
