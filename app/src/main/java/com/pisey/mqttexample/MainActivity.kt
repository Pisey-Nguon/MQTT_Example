package com.pisey.mqttexample

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btnRun).setOnClickListener {
//            runWithBackgroundLocationPermission {
//                LocationTrackingService.start(this)
//            }

            LocationTrackingWorker.start(this)
        }


    }
}