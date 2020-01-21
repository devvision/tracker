package com.example.tracker.data.localdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query
import com.example.tracker.data.model.Exercise
import com.example.tracker.data.model.Location
import io.reactivex.Single

@Dao
interface Dao {

    @Query("SELECT * FROM exercise WHERE date LIKE :date")
    fun getAllExercisesOfADate(date: String): Single<MutableList<Exercise>>

    @Query("SELECT * FROM location WHERE exercise_id = :exerciseId")
    fun getAllLocationsOfAExercise(exerciseId: Long): Single<MutableList<Location>>

    @Insert(onConflict = IGNORE)
    fun insertExercise(exercise: Exercise): Single<Long>

    @Insert
    fun insertLocation(location: Location): Single<Long>
}