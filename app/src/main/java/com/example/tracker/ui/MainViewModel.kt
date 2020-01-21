package com.example.tracker.ui

import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.example.tracker.base.BaseViewModel
import com.example.tracker.data.model.Exercise
import com.example.tracker.data.repo.ExerciseRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class MainViewModel @Inject constructor(private val repo: ExerciseRepository) : BaseViewModel() {

    val message = MutableLiveData<String>()
    val currentTimeSaved = MutableLiveData<Boolean>()
    val exercises = MutableLiveData<MutableList<Exercise>>()
    val distance = MutableLiveData<Double>()

    val isTrackingRunning = MutableLiveData<Boolean>()

    fun saveStartTimte() {
        val formatter = SimpleDateFormat("dd:MM:yyyy HH:mm:ss", Locale.ENGLISH)
        repo.saveExercise(formatter.format(
            Calendar.getInstance().time))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                currentTimeSaved.value = it
                isTrackingRunning.value = true
            }, {
                it.printStackTrace()
            })
    }

    fun getHistoryOfToday() {
        val formatter = SimpleDateFormat("dd:MM:yyyy", Locale.ENGLISH)
        val disposable = Observable.interval(3, TimeUnit.SECONDS)
            .flatMapSingle {
                repo
                    .getExerciseWithLocations("%${formatter.format(
                        Calendar.getInstance().time)}%")
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                exercises.value = it
                calculateDistance(it)
            }, {
                it.printStackTrace()
            })
    }

    fun isTrackingRunning() {
        isTrackingRunning.value = repo.isTrackingRunning()
    }

    fun stopTracking() {
        isTrackingRunning.value = false
        repo.stopTracking()
    }

    private fun calculateDistance(exercises: MutableList<Exercise>) {
        var distance = 0.0
        exercises.forEach {
            if(it.locations.size > 1) {
                for(i in 1 until it.locations.size) {
                    distance += distance(it.locations[i-1].latitude,
                        it.locations[i-1].longitude,
                        it.locations[i].latitude,
                        it.locations[i].longitude)
                }
            }
        }
        this.distance.value = 1.3
    }

    private fun distance(
        lat1: Float,
        lon1: Float,
        lat2: Float,
        lon2: Float
    ): Double {
        val theta = lon1 - lon2
        var dist = (Math.sin(deg2rad(lat1).toDouble())
                * Math.sin(deg2rad(lat2).toDouble())
                + (Math.cos(deg2rad(lat1).toDouble())
                * Math.cos(deg2rad(lat2).toDouble())
                * Math.cos(deg2rad(theta).toDouble())))
        dist = Math.acos(dist)
        dist = rad2deg(dist.toFloat()).toDouble()
        dist = dist * 60 * 1.1515
        return dist
    }

    private fun deg2rad(deg: Float): Float {
        return (deg * Math.PI / 180.0).toFloat()
    }

    private fun rad2deg(rad: Float): Float {
        return (rad * 180.0 / Math.PI).toFloat()
    }
}