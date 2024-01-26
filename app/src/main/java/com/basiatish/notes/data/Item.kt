package com.basiatish.notes.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "title")
    val title: String?,
    @ColumnInfo(name = "note")
    val note: String?,
    @ColumnInfo(name = "date")
    val date: String,
    @ColumnInfo(name = "viewId")
    val viewId: Int,
    @ColumnInfo(name = "color")
    val color: Int
)