package com.basiatish.notes.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    @Query("SELECT * FROM item")
    suspend fun getNotes() : List<Item>

    @Query("SELECT * FROM item WHERE id = :id")
    fun getNote(id: Int) : Flow<Item>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item)

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Query("delete from item where id in (:idList)")
    suspend fun deleteListOfNotes(idList: List<Int>)
}