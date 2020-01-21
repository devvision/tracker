package com.example.tracker.data.localdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tracker.data.model.Exercise
import com.example.tracker.data.model.Location

@Database(entities = arrayOf(
    Exercise::class,
    Location::class
), version = 1)
abstract class AppDb: RoomDatabase() {

    abstract fun getDao(): Dao

}