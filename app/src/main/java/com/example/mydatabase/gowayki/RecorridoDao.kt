package com.example.mydatabase.gowayki

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecorridoDao {
    @Query("SELECT * FROM recorridos ORDER BY id DESC")
    fun getAll(): Flow<List<Recorrido>>

    @Insert
    suspend fun insert(recorrido: Recorrido)

    @Update
    suspend fun update(recorrido: Recorrido)

    @Delete
    suspend fun delete(recorrido: Recorrido)
}
