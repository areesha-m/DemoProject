package com.example.myprofileapp.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UserProfile::class, AdListing::class], version = 2) // Increment version number
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun adListingDao(): AdListingDao // Add this
}