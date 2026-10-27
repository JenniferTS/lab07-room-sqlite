package com.example.mydatabase.tareas

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tareas")
data class Tarea(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "titulo") val titulo: String,
    @ColumnInfo(name = "curso") val curso: String,
    @ColumnInfo(name = "fecha_entrega") val fechaEntrega: String,
    @ColumnInfo(name = "completada") val completada: Boolean = false
)
