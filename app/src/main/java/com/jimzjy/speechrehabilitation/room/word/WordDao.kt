package com.jimzjy.speechrehabilitation.room.word

import androidx.room.*

@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(singleWord: Word)

    @Update
    fun update(singleWord: Word)

    @Delete
    fun delete(singleWord: Word)

    @Query("SELECT * FROM word WHERE id = :type")
    fun loadWord(type: Int): Word
}