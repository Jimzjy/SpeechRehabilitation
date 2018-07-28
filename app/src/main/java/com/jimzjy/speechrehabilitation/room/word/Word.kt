package com.jimzjy.speechrehabilitation.room.word

import androidx.room.Entity
import androidx.room.PrimaryKey

const val ROOM_SINGLE_WORD = 0
const val ROOM_WORDS = 1

@Entity
data class Word (
        @PrimaryKey
        val id: Int,

        val words: String
)