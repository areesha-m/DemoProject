package com.example.myprofileapp.data

import androidx.room.*

@Dao
interface UserProfileDao {

    @Insert
    suspend fun insertUserProfile(userProfile: UserProfile)

    @Update
    suspend fun updateUserProfile(userProfile: UserProfile)

    @Delete
    suspend fun deleteUserProfile(userProfile: UserProfile)

    @Query("SELECT * FROM user_profile")
    suspend fun getUserProfile(): UserProfile?
}
