package com.example.myprofileapp.data

import android.content.Context
import androidx.room.Room

object DatabaseHelper {

    private var appDatabase: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "user_profile_db"  // Name of the database
            ).build()
        }
        return appDatabase!!
    }
}
