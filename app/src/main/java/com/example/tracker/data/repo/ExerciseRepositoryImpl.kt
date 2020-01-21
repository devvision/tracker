package com.example.tracker.data.repo

import android.content.Context
import com.example.tracker.data.locadatasource.LocalDataSource
import com.example.tracker.data.model.Exercise
import com.example.tracker.data.model.Location
import com.example.tracker.data.preference.AppPreference
import com.example.tracker.di.annotations.AppScope
import com.example.tracker.di.annotations.ApplicationContext
import io.reactivex.Single
import javax.inject.Inject

class ExerciseRepositoryImpl @Inject constructor(@ApplicationContext val context: Context,
                                                 private val localDataSource: LocalDataSource,
                                                 private val preference: AppPreference): ExerciseRepository {

    override fun saveExercise(time: String): Single<Boolean> {
        if(preference.currentExercise == 0L) {
            return localDataSource
                .addAExercise(Exercise(date = time))
                .map {
                    preference.currentExercise = it
                    it > 0L
                }
        } else {
            return Single.create { it.onSuccess(true) }
        }
    }

    override fun saveLocation(location: Location): Single<Boolean> {
        return localDataSource.addALocation(location)
            .map {
                it > 0L
            }
    }

    override fun getExerciseWithLocations(date: String): Single<MutableList<Exercise>> {
        return localDataSource.getExercisesOfADate(date)
            .flatMap {
                val requests = mutableListOf<Single<Exercise>>()
                it.forEach { exercise ->
                    requests.add(localDataSource.getLocationsOfAExercise(exercise.id)
                        .map {
                            exercise.apply {
                                locations = it
                            }
                        })
                }
                Single.zip(requests) {
                    it.toMutableList() as MutableList<Exercise>
                }
            }
    }

    override fun isTrackingRunning(): Boolean {
        return preference.currentExercise != 0L
    }

    override fun stopTracking() {
        preference.currentExercise = 0L
    }
}