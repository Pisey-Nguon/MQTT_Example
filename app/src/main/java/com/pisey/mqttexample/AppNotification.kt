package com.pisey.mqttexample

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService

class AppNotification(private val context: Context) {

    companion object {
        const val TRACKING_CHANNEL_ID = "TRACKING_CHANNEL_ID"
        const val TRACKING_CHANNEL_NAME = "Tracking"

        const val COLLECTION_PLAN_CHANNEL_ID = "COLLECTION_PLAN_CHANNEL_ID"
        const val COLLECTION_PLAN_CHANNEL_NAME = "Collection Plan"

        fun createTrackingChannel(context: Context) {
            val serviceChanel = NotificationChannel(
                TRACKING_CHANNEL_ID,
                TRACKING_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = getSystemService(context, NotificationManager::class.java)
            notificationManager?.createNotificationChannel(serviceChanel)
        }

        fun createCollectionPlanChannel(context: Context) {
            val serviceChanel = NotificationChannel(
                COLLECTION_PLAN_CHANNEL_ID,
                COLLECTION_PLAN_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = getSystemService(context, NotificationManager::class.java)
            notificationManager?.createNotificationChannel(serviceChanel)
        }

        fun clearAllNotification(context: Context){
            val notificationManager = getSystemService(context, NotificationManager::class.java)
            notificationManager?.cancelAll()
        }

    }

    private val notificationManager = context.getSystemService(NotificationManager::class.java)
    private val intentLaunchApp = Intent(context, MainActivity::class.java).also { it.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP) }
    private val pendingIntentLaunchApp = PendingIntent.getActivity(context, 0, intentLaunchApp, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

    fun showEndVisitConfirm(notificationId: Int) {
        val notification = NotificationCompat.Builder(context, COLLECTION_PLAN_CHANNEL_ID)
            .setContentText("we will automatic end your visit after you leave customer in 60s")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setOnlyAlertOnce(true)
            .setAutoCancel(false)
            .setOngoing(true)
            .build()
        notificationManager.notify(notificationId, notification)
    }

    fun showEndVisitProgress(notificationId: Int) {
        val notification = NotificationCompat.Builder(context, COLLECTION_PLAN_CHANNEL_ID)
            .setContentTitle("Ending visit customer...")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setProgress(0, 0, true)
            .setOnlyAlertOnce(true)
            .setAutoCancel(false)
            .setOngoing(true)
            .build()
        notificationManager.notify(notificationId, notification)
    }

    fun showEndVisitSuccess(collectionPlanVisitId: Int) {
        val notification = NotificationCompat.Builder(context, COLLECTION_PLAN_CHANNEL_ID)
            .setContentTitle("End visit customer successfully")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(pendingIntentLaunchApp)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setOngoing(false)
            .build()
        notificationManager.notify(collectionPlanVisitId, notification)
    }

    fun showEndVisitFailed(collectionPlanVisitId: Int) {
        val notification = NotificationCompat.Builder(context, COLLECTION_PLAN_CHANNEL_ID)
            .setContentTitle("End visit customer failed")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(pendingIntentLaunchApp)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setOngoing(true)
            .build()
        notificationManager.notify(collectionPlanVisitId, notification)
    }

    fun showStatusArrival(title: String, notificationId: Int) {
        val notification = NotificationCompat.Builder(context, COLLECTION_PLAN_CHANNEL_ID)
            .setContentTitle(title)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(pendingIntentLaunchApp)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setOngoing(false)
            .build()
        notificationManager.notify(notificationId, notification)
    }

    fun showApproachEndVisit(notificationId: Int, distance: Int) {
        val notification = NotificationCompat.Builder(context, COLLECTION_PLAN_CHANNEL_ID)
            .setContentTitle("Please End your visit!")
            .setContentText("we will automatic end your visit after you leave customer in ${distance}m with 60s")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(pendingIntentLaunchApp)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setOngoing(false)
            .build()
        notificationManager.notify(notificationId, notification)
    }

    fun showLatLng(notificationId: Int,lat:Double,lng:Double){
        val notification = NotificationCompat.Builder(context, TRACKING_CHANNEL_ID)
            .setContentTitle("Tracking")
            .setContentText("Lat = $lat / Lng = $lng")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(pendingIntentLaunchApp)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setOngoing(false)
            .build()
        notificationManager.notify(notificationId, notification)
    }


    fun getForegroundNotification(title: String, message: String, pendingIntent: PendingIntent): Notification {
        return NotificationCompat.Builder(context, TRACKING_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(pendingIntent)
            .build()
    }

    fun updateForegroundNotification(title: String, message: String, notificationId: Int, pendingIntent: PendingIntent) {
        val notification = NotificationCompat.Builder(context, TRACKING_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(pendingIntent)
            .build()
        notificationManager.notify(notificationId, notification)
    }
}