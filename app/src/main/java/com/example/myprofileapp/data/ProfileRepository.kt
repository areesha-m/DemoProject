package com.example.myprofileapp.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface IProfileRepository {
    suspend fun getUserProfile(): UserProfile?
    suspend fun saveUserProfile(userProfile: UserProfile)
}

class ProfileRepository(private val userProfileDao: UserProfileDao) : IProfileRepository {

    override suspend fun getUserProfile(): UserProfile? {
        return withContext(Dispatchers.IO) {
            try {
                userProfileDao.getUserProfile()
            } catch (e: Exception) {
                Log.e("ProfileRepository", "Error getting user profile", e)
                null
            }
        }
    }

    override suspend fun saveUserProfile(userProfile: UserProfile) {
        withContext(Dispatchers.IO) {
            try {
                val existingProfile = userProfileDao.getUserProfile()
                if (existingProfile != null) {
                    // Preserve the ID if updating an existing profile
                    val profileToUpdate = userProfile.copy(id = existingProfile.id)
                    userProfileDao.updateUserProfile(profileToUpdate)
                    Log.d("ProfileRepository", "Profile updated: $profileToUpdate")
                } else {
                    userProfileDao.insertUserProfile(userProfile)
                    Log.d("ProfileRepository", "Profile inserted: $userProfile")
                }
            } catch (e: Exception) {
                Log.e("ProfileRepository", "Error saving user profile", e)
            }
        }
    }
}
