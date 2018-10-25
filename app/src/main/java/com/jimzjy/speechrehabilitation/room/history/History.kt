package com.jimzjy.speechrehabilitation.room.history

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class History (
        @PrimaryKey
        val username: String,

        @ColumnInfo(name = "single_word")
        val singleWord: String,

        @ColumnInfo(name = "words")
        val words: String
)