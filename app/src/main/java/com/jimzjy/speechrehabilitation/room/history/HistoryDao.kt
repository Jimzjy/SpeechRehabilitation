package com.jimzjy.speechrehabilitation.room.history

import androidx.room.*

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(history: History)

    @Update
    fun update(history: History)

    @Delete
    fun delete(history: History)

    @Query("SELECT username FROM history")
    fun loadAllUser(): List<String>

    @Query("SELECT * FROM history WHERE username = :username")
    fun loadHistory(username: String): History
}