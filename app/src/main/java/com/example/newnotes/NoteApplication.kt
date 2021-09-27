package com.example.newnotes

import android.app.Application
import com.example.newnotes.data.ItemRoomDatabase

class NoteApplication : Application() {
    val database: ItemRoomDatabase by lazy { ItemRoomDatabase.getDatabase(this) }
}