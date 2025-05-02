package com.example.myprofileapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.example.myprofileapp.data.DatabaseHelper
import com.example.myprofileapp.data.UserProfile
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.delay


class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val userProfileDao = DatabaseHelper.getDatabase(application).userProfileDao()

    private val _firstName = MutableStateFlow("")
    val firstName: StateFlow<String> = _firstName

    private val _lastName = MutableStateFlow("")
    val lastName: StateFlow<String> = _lastName

    private val _dob = MutableStateFlow("")
    val dob: StateFlow<String> = _dob

    private val _nationality = MutableStateFlow("")
    val nationality: StateFlow<String> = _nationality

    private val _gender = MutableStateFlow("")
    val gender: StateFlow<String> = _gender

    private val _isFormComplete = MutableStateFlow(false)
    val isFormComplete: StateFlow<Boolean> = _isFormComplete

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading


    init {
        // Asynchronously load profile data
        loadProfileData()
    }

    // Method to update first name
    fun updateFirstName(newFirstName: String) {
        _firstName.value = newFirstName
        checkFormComplete()
    }

    // Method to update last name
    fun updateLastName(newLastName: String) {
        _lastName.value = newLastName
        checkFormComplete()
    }

    // Method to update date of birth
    fun updateDob(newDob: String) {
        _dob.value = newDob
        checkFormComplete()
    }

    // Method to update nationality
    fun updateNationality(newNationality: String) {
        _nationality.value = newNationality
        checkFormComplete()
    }

    // Method to update gender
    fun updateGender(newGender: String) {
        _gender.value = newGender
        checkFormComplete()
    }

    // Method to check if the form is complete
    private fun checkFormComplete() {
        _isFormComplete.value = firstName.value.isNotEmpty() &&
                lastName.value.isNotEmpty() &&
                dob.value.isNotEmpty() &&
                nationality.value.isNotEmpty() &&
                gender.value.isNotEmpty()
    }

    fun saveProfileData() {
        val profile = UserProfile(
            firstName = firstName.value,
            lastName = lastName.value,
            dob = dob.value,
            nationality = nationality.value,
            gender = gender.value
        )
        viewModelScope.launch {
            try {
                Log.d("ProfileViewModel", "Saving profile: $profile")

                // Update the existing profile
                val existingProfile = userProfileDao.getUserProfile()
                if (existingProfile != null) {
                    // If there's an existing profile, update it
                    profile.id = existingProfile.id  // Preserve the ID of the existing profile
                    userProfileDao.updateUserProfile(profile)
                    Log.d("ProfileViewModel", "Profile updated successfully")
                } else {
                    // If no profile exists, insert a new one (fallback case)
                    userProfileDao.insertUserProfile(profile)
                    Log.d("ProfileViewModel", "Profile inserted successfully")
                }

                // Now try to load the profile data again after saving or updating
                val updatedProfile = userProfileDao.getUserProfile()
                Log.d("ProfileViewModel", "Loaded profile after save/update: $updatedProfile")

                updatedProfile?.let {
                    _firstName.value = it.firstName
                    _lastName.value = it.lastName
                    _dob.value = it.dob
                    _nationality.value = it.nationality
                    _gender.value = it.gender
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error saving profile data", e)
            }
        }
    }

    // Load profile data from the database asynchronously
    fun loadProfileData() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(2000) // Simulate delay
            val profile = userProfileDao.getUserProfile()
            Log.d("ProfileViewModel", "Loaded profile: $profile")

            profile?.let {
                _firstName.value = it.firstName
                _lastName.value = it.lastName
                _dob.value = it.dob
                _nationality.value = it.nationality
                _gender.value = it.gender
                checkFormComplete()  // Check form completion after loading the profile
                _isLoading.value = false
            }

        }
    }
}
