package com.example.mydatabase.gowayki

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Recorrido::class], version = 1)
abstract class GoWaykiDatabase : RoomDatabase() {
    abstract fun recorridoDao(): RecorridoDao

    companion object {
        @Volatile
        private var INSTANCE: GoWaykiDatabase? = null

        fun getDatabase(context: Context): GoWaykiDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    GoWaykiDatabase::class.java,
                    "gowayki_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
