package com.basiatish.notes

import androidx.lifecycle.*
import com.basiatish.notes.data.Item
import com.basiatish.notes.data.ItemDao
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.*

class NoteViewModel(private val itemDao: ItemDao): ViewModel() {

    private var _notes = MutableLiveData<List<Item>>()
    val notes: LiveData<List<Item>> = _notes

    private val _deleteStatus = MutableLiveData<Status>()
    val deleteStatus: LiveData<Status> = _deleteStatus

    private val selectedNotes: MutableList<Item> = mutableListOf()
    private val selectedNotesPositions: MutableList<Int> = mutableListOf()

    fun addSelectedNode(note: Item, position: Int) {
        selectedNotes.add(note)
        selectedNotesPositions.add(position)
    }

    fun retrieveSelectedNote(note: Item, position: Int) {
        selectedNotes.remove(note)
        selectedNotesPositions.remove(position)
    }

    fun getSelectedNotesPositions(): List<Int> {
        return selectedNotesPositions
    }

    fun clearSelection() {
        selectedNotes.clear()
        selectedNotesPositions.clear()
    }

    fun deleteSelectedNotes() {
        viewModelScope.launch {
            try {
                _deleteStatus.value = Status.LOADING
                if (selectedNotes.isNotEmpty()) {
                    val ids = mutableListOf<Int>()
                    selectedNotes.forEach { ids.add(it.id) }
                    itemDao.deleteListOfNotes(ids)
                }
                _deleteStatus.value = Status.DONE
            } catch (e: Exception) {
                _deleteStatus.value = Status.ERROR
            }
        }
    }

    fun getAllNotes() {
        viewModelScope.launch {
            _notes.value = listOf()
            _notes.value = itemDao.getNotes()
        }
    }

    fun retrieveItem(id: Int): LiveData<Item> {
        return itemDao.getNote(id).asLiveData()
    }

    fun addNewNote(title: String, description: String, viewId: Int, color: Int) {
        val currentDate = getDate()
        val newNote = getNewNote(title, description.trim(), currentDate, viewId, color)
        insert(newNote)
    }

    private fun getNewNote(title: String, description: String,
                           date: String, viewId: Int, color: Int) : Item {
        return Item(title = title, note = description, date = date, viewId = viewId, color = color)
    }

    private fun updatedNote(id: Int, title: String, description: String, date: String, viewId: Int, color: Int) : Item {
        return Item(id = id, title = title, note = description.trim(), date = ("Updated: $date"), viewId = viewId, color = color)
    }

    private fun getDate(): String {
        val current = Date()
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return formatter.format(current)
    }

    fun updateNote(id: Int, title: String, description: String, viewId: Int, color: Int) {
        val currentDate = getDate()
        val updatedNote = updatedNote(id, title, description, currentDate, viewId, color)
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
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(itemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}