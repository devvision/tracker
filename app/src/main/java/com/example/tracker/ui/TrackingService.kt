package com.example.tracker.ui

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import com.example.tracker.data.model.Exercise
import com.example.tracker.data.preference.AppPreference
import com.example.tracker.data.repo.ExerciseRepository
import com.google.android.gms.location.*
import com.orhanobut.logger.Logger
import dagger.android.AndroidInjection
import dagger.android.DaggerService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class TrackingService : DaggerService() {

    @Inject lateinit var preference: AppPreference
    @Inject lateinit var repository: ExerciseRepository

    private var notificationBuilder: NotificationBuilder? = null
    private var callback: LocationCallback? = null

    override fun onBind(intent: Intent?): IBinder? {
        return TrackingServiceBinder()
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(100, getNotificationBuilder().buildNotification(true))
        startLocationUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
        stopForeground(true)
    }

    private fun getNotificationBuilder() : NotificationBuilder {
        if(notificationBuilder == null) {
            notificationBuilder = NotificationBuilder(this)
        }
        return notificationBuilder!!
    }

    inner class TrackingServiceBinder: Binder() {
        fun getTrackingService(): TrackingService {
            return this@TrackingService
        }
    }

    fun startLocationUpdates() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        locationRequest.interval = 3000
        locationRequest.fastestInterval = 100

        val settings = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .build()
        val settingsClient = LocationServices.getSettingsClient(applicationContext)
        settingsClient.checkLocationSettings(settings)

        if(callback == null) {
            callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult?) {
                    result?.let {
                        this@TrackingService.onLocationChanged(it.lastLocation)
                    }

                }
            }
        }

        LocationServices.getFusedLocationProviderClient(this)
            .requestLocationUpdates(locationRequest, callback, Looper.myLooper())
    }

    fun stopLocationUpdates() {
        if(callback != null) {
            LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(callback)
        }
    }

    fun onLocationChanged(location: Location) {
        val exerciseId = preference.currentExercise
        val location = com.example.tracker.data.model.Location(exerciseId = exerciseId,
            latitude = location.latitude.toFloat(),
            longitude = location.longitude.toFloat())
        repository.saveLocation(location)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({
                Logger.d(it)
            }, {
                it.printStackTrace()
            })

    }
}