package com.example.myprofileapp.data

import androidx.room.*

// Granular interfaces
interface UserProfileReader {
    @Query("SELECT * FROM user_profile LIMIT 1")
    suspend fun getUserProfile(): UserProfile?
}

interface UserProfileWriter {
    @Insert
    suspend fun insertUserProfile(userProfile: UserProfile)

    @Update
    suspend fun updateUserProfile(userProfile: UserProfile)
}

interface UserProfileDeleter {
    @Delete
    suspend fun deleteUserProfile(userProfile: UserProfile)
}

@Dao
interface UserProfileDao : UserProfileReader, UserProfileWriter, UserProfileDeleter {}