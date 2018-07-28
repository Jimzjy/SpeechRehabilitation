package com.jimzjy.speechrehabilitation.room.lattice

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Lattice (
        @PrimaryKey
        val word: String,

        val lattice: String
)