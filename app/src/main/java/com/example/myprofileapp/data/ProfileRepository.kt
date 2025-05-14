package com.example.myprofileapp.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// IProfileRepository remains the same as its client (ViewModel) needs both read and save
interface IProfileRepository {
    suspend fun getUserProfile(): UserProfile?
    suspend fun saveUserProfile(userProfile: UserProfile)
}

// ProfileRepository now depends on the more specific DAO capabilities
class ProfileRepository(
    private val profileReader: UserProfileReader,
    private val profileWriter: UserProfileWriter
) : IProfileRepository {

    override suspend fun getUserProfile(): UserProfile? {
        return withContext(Dispatchers.IO) {
            try {
                // Now uses the profileReader
                profileReader.getUserProfile()
            } catch (e: Exception) {
                Log.e("ProfileRepository", "Error getting user profile", e)
                null
            }
        }
    }

    override suspend fun saveUserProfile(userProfile: UserProfile) {
        withContext(Dispatchers.IO) {
            try {
                // Needs to read first to check if exists, so profileReader is still needed here too.
                val existingProfile = profileReader.getUserProfile()
                if (existingProfile != null) {
                    val profileToUpdate = userProfile.copy(id = existingProfile.id)
                    // Now uses the profileWriter
                    profileWriter.updateUserProfile(profileToUpdate)
                    Log.d("ProfileRepository", "Profile updated: $profileToUpdate")
                } else {
                    // Now uses the profileWriter
                    profileWriter.insertUserProfile(userProfile)
                    Log.d("ProfileRepository", "Profile inserted: $userProfile")
                }
            } catch (e: Exception) {
                Log.e("ProfileRepository", "Error saving user profile", e)
            }
        }
    }
}