package com.example.newnotes

import androidx.lifecycle.*
import com.example.newnotes.data.Item
import com.example.newnotes.data.ItemDao
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.*

class NoteViewModel(private val itemDao: ItemDao): ViewModel() {

    val allNotes: LiveData<List<Item>> = itemDao.getNotes().asLiveData()

    fun retrieveItem(id: Int): LiveData<Item> {
        return itemDao.getNote(id).asLiveData()
    }

    fun addNewNote(title: String, description: String) {
        val currentDate = getDate()
        val newNote = getNewNote(title, description, currentDate)
        insert(newNote)
    }

    private fun getNewNote(title: String, description: String,
                           date: String) : Item {
        return Item(title = title, note = description, date = date)
    }

    private fun updatedNote(id: Int, title: String, description: String, date: String) : Item {
        return Item(id = id, title = title, note = description, date = ("Updated: $date"))
    }

    private fun getDate(): String {
        val current = Date()
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return formatter.format(current)
    }

    fun updateNote(id: Int, title: String, description: String) {
        val currentDate = getDate()
        val updatedNote = updatedNote(id, title, description, currentDate)
        update(updatedNote)
    }

    fun isEntryNull(title: String, description: String): Boolean {
        if (title.isBlank() && description.isBlank()) {
            return false
        }
        return true
    }

    private fun update(item: Item) {
        viewModelScope.launch {
            itemDao.update(item)
        }
    }

    private fun insert(item: Item) {
        viewModelScope.launch {
            itemDao.insert(item)
        }
    }

    fun delete(item: Item) {
        viewModelScope.launch {
            itemDao.delete(item)
        }
    }
}

class NoteViewModelFactory(private val itemDao: ItemDao):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(itemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}