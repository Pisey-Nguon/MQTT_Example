package com.pisey.mqttexample

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit


class LocationTrackingWorker(
    private val context: Context,
    private val workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {


    private val sharedPreferences by lazy { SharedPreferences(context) }
    companion object {
        fun start(context: Context) {
            // Create a PeriodicWorkRequest that will run every 15 minutes.
            val locationWorkRequest: PeriodicWorkRequest = PeriodicWorkRequest.Builder(
                LocationTrackingWorker::class.java,
                15,
                TimeUnit.MINUTES
            ).build()

            // Enqueue the locationWorkRequest with WorkManager.
            WorkManager.getInstance(context).enqueue(locationWorkRequest)
        }
    }

    override suspend fun doWork(): Result {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.failure()
        }
        val location = LocationServices.getFusedLocationProviderClient(applicationContext).lastLocation.await()
        sharedPreferences.putString("workerLocation","lat = ${location.latitude} || lng = ${location.longitude}")
        return if (location != null) Result.success()
        else Result.failure()
    }
}