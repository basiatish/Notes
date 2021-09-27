package com.example.newnotes.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    @Query("SELECT * FROM item")
    fun getNotes() : Flow<List<Item>>

    @Query("SELECT * FROM item WHERE id = :id")
    fun getNote(id: Int) : Flow<Item>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item)

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)
}