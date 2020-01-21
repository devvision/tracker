package com.example.tracker.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "exercise")
data class Exercise (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0,

    @ColumnInfo(name = "date")
    var date: String = "",

    @Ignore
    var locations: MutableList<Location> = mutableListOf()

)