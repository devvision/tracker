package com.example.tracker.data.repo

import com.example.tracker.data.model.Exercise
import com.example.tracker.data.model.Location
import io.reactivex.Single

interface ExerciseRepository {

    fun saveExercise(time: String): Single<Boolean>
    fun saveLocation(location: Location): Single<Boolean>
    fun getExerciseWithLocations(date: String): Single<MutableList<Exercise>>
    fun isTrackingRunning(): Boolean
    fun stopTracking()
}