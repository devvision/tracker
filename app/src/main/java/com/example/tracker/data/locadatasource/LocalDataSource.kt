package com.example.tracker.data.locadatasource

import com.example.tracker.data.localdb.AppDb
import com.example.tracker.data.model.Exercise
import com.example.tracker.data.model.Location
import io.reactivex.Single
import javax.inject.Inject

class LocalDataSource @Inject constructor(private val db: AppDb) {

    fun getExercisesOfADate(date: String): Single<MutableList<Exercise>> {
        return db.getDao().getAllExercisesOfADate(date)
    }

    fun getLocationsOfAExercise(exerciseId: Long): Single<MutableList<Location>> {
        return db.getDao().getAllLocationsOfAExercise(exerciseId)
    }

    fun addAExercise(exercise: Exercise): Single<Long> {
        return db.getDao().insertExercise(exercise)
    }

    fun addALocation(location: Location): Single<Long> {
        return db.getDao().insertLocation(location)
    }
}