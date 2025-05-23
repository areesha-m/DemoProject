package com.example.myprofileapp.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.myprofileapp.data.IProfileRepository
import com.example.myprofileapp.data.UserProfile
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.delay

@HiltViewModel
class ProfileViewModel @Inject constructor(
    application: Application,
    private val profileRepository: IProfileRepository // Use the interface
) : AndroidViewModel(application) {

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
        loadProfileData()
    }

    fun updateFirstName(newFirstName: String) {
        _firstName.value = newFirstName
        checkFormComplete()
    }

    fun updateLastName(newLastName: String) {
        _lastName.value = newLastName
        checkFormComplete()
    }

    fun updateDob(newDob: String) {
        _dob.value = newDob
        checkFormComplete()
    }

    fun updateNationality(newNationality: String) {
        _nationality.value = newNationality
        checkFormComplete()
    }

    fun updateGender(newGender: String) {
        _gender.value = newGender
        checkFormComplete()
    }

    private fun checkFormComplete() {
        _isFormComplete.value = firstName.value.isNotEmpty() &&
                lastName.value.isNotEmpty() &&
                dob.value.isNotEmpty() &&
                nationality.value.isNotEmpty() &&
                gender.value.isNotEmpty()
    }

    fun saveProfileData() {
        val profile = UserProfile(
            // id will be handled by repository/DAO (autoGenerate or based on existing)
            firstName = firstName.value,
            lastName = lastName.value,
            dob = dob.value,
            nationality = nationality.value,
            gender = gender.value
        )
        viewModelScope.launch {
            _isLoading.value = true // Indicate loading during save
            Log.d("ProfileViewModel", "Attempting to save profile: $profile")
            try {
                profileRepository.saveUserProfile(profile)
                Log.d("ProfileViewModel", "Profile save operation completed via repository.")
                // Optionally, reload data or assume repository handles updates correctly
                // For simplicity, let's reload to ensure UI consistency if repository doesn't directly update ViewModel states
                val updatedProfile = profileRepository.getUserProfile()
                updatedProfile?.let {
                    _firstName.value = it.firstName
                    _lastName.value = it.lastName
                    _dob.value = it.dob
                    _nationality.value = it.nationality
                    _gender.value = it.gender
                    checkFormComplete()
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error saving profile data via repository", e)
            } finally {
                _isLoading.value = false // Stop loading after save attempt
            }
        }
    }

    fun loadProfileData() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(2000)
            Log.d("ProfileViewModel", "Loading profile data via repository...")
            try {
                val profile = profileRepository.getUserProfile()
                Log.d("ProfileViewModel", "Loaded profile from repository: $profile")

                profile?.let {
                    _firstName.value = it.firstName
                    _lastName.value = it.lastName
                    _dob.value = it.dob
                    _nationality.value = it.nationality
                    _gender.value = it.gender
                    checkFormComplete()
                } ?: run {
                    // Handle case where no profile exists yet, reset fields if necessary
                    _firstName.value = ""
                    _lastName.value = ""
                    _dob.value = ""
                    _nationality.value = ""
                    _gender.value = ""
                    checkFormComplete()
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error loading profile data via repository", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}