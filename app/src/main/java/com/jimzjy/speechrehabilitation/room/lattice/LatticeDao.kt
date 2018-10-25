package com.jimzjy.speechrehabilitation.room.lattice

import androidx.room.*


@Dao
interface LatticeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(lattice: Lattice)

    @Update
    fun update(lattice: Lattice)

    @Delete
    fun delete(lattice: Lattice)

    @Query("SELECT * FROM lattice WHERE word = :word")
    fun loadLattice(word: String): Lattice

    @Query("SELECT * FROM lattice WHERE word IN(:words)")
    fun loadLattices(words: List<String>): List<Lattice>
}