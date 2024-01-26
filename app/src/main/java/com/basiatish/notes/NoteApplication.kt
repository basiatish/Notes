package com.basiatish.notes

import android.app.Application
import com.basiatish.notes.data.ItemRoomDatabase

class NoteApplication : Application() {
    val database: ItemRoomDatabase by lazy { ItemRoomDatabase.getDatabase(this) }
}