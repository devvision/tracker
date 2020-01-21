package com.example.tracker.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "location",
    foreignKeys = arrayOf(
        ForeignKey(entity = Exercise::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("exercise_id"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE)))
data class Location(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0,

    @ColumnInfo(name = "exercise_id")
    var exerciseId: Long = 0,

    @ColumnInfo(name = "latitude")
    var latitude: Float = 0f,

    @ColumnInfo(name = "longitude")
    var longitude: Float = 0f
)