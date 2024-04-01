package com.hisense.einkservice.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hisense.einkservice.model.EinkApp

@Database(entities = [EinkApp::class], version = 1, exportSchema = false)
abstract class EinkAppDatabase : RoomDatabase() {
    abstract fun einkAppDao(): EinkAppDao

    companion object {
        @Volatile
        private var instance: EinkAppDatabase? = null

        fun getInstance(context: Context): EinkAppDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(context, EinkAppDatabase::class.java, "eink_app_database")
                    .build()
                    .also { instance = it }
            }
        }
    }
}