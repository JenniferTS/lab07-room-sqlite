package com.example.mydatabase.gowayki

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Igual que la tabla "recorridos" de GoWayki (mi proyecto final)
@Entity(tableName = "recorridos")
data class Recorrido(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "nombre") val nombre: String,
    @ColumnInfo(name = "origen") val origen: String,
    @ColumnInfo(name = "destino") val destino: String,
    @ColumnInfo(name = "ruta") val ruta: String,
    @ColumnInfo(name = "notas") val notas: String = ""
)
