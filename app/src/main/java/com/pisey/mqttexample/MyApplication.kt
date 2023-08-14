package com.pisey.mqttexample

import android.app.Application

class MyApplication:Application() {

    override fun onCreate() {
        super.onCreate()
        AppNotification.createTrackingChannel(this)
    }
}