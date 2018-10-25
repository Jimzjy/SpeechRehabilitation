package com.jimzjy.speechrehabilitation.room.lattice

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Lattice::class], version = 1)
abstract class LatticeDatabase : RoomDatabase() {

    abstract fun latticeDao(): LatticeDao
}