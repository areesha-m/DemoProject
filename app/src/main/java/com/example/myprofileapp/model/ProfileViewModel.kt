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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Date

@HiltViewModel
class ProfileViewModel @Inject constructor(
    application: Application,
    private val profileRepository: IProfileRepository
) : AndroidViewModel(application) {

    private val _firstName = MutableStateFlow("")
    val firstName: StateFlow<String> = _firstName
    private val _firstNameError = MutableStateFlow<String?>(null)
    val firstNameError: StateFlow<String?> = _firstNameError

    private val _lastName = MutableStateFlow("")
    val lastName: StateFlow<String> = _lastName
    private val _lastNameError = MutableStateFlow<String?>(null)
    val lastNameError: StateFlow<String?> = _lastNameError

    private val _dob = MutableStateFlow("")
    val dob: StateFlow<String> = _dob
    private val _dobError = MutableStateFlow<String?>(null)
    val dobError: StateFlow<String?> = _dobError

    private val _nationality = MutableStateFlow("")
    val nationality: StateFlow<String> = _nationality
    private val _nationalityError = MutableStateFlow<String?>(null)
    val nationalityError: StateFlow<String?> = _nationalityError

    private val _gender = MutableStateFlow("")
    val gender: StateFlow<String> = _gender

    private val _isFormComplete = MutableStateFlow(false)
    val isFormComplete: StateFlow<Boolean> = _isFormComplete

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadProfileData()
    }

    private fun validateFirstName(name: String = _firstName.value): Boolean {
        return if (name.isBlank()) {
            _firstNameError.value = "First name cannot be empty"
            false
        } else if (!name.matches(Regex("^[a-zA-ZÀ-ÿ]+(([',. -][a-zA-ZÀ-ÿ ])?[a-zA-ZÀ-ÿ]*)*$"))) {
            _firstNameError.value = "Invalid characters in first name"
            false
        }
        else {
            _firstNameError.value = null
            true
        }
    }

    private fun validateLastName(name: String = _lastName.value): Boolean {
        return if (name.isBlank()) {
            _lastNameError.value = "Last name cannot be empty"
            false
        } else if (!name.matches(Regex("^[a-zA-ZÀ-ÿ]+(([',. -][a-zA-ZÀ-ÿ ])?[a-zA-ZÀ-ÿ]*)*$"))) {
            _lastNameError.value = "Invalid characters in last name"
            false
        }
        else {
            _lastNameError.value = null
            true
        }
    }

    private fun validateDob(dateStr: String = _dob.value): Boolean {
        if (dateStr.isBlank()) {
            _dobError.value = "Date of birth cannot be empty"
            return false
        }
        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
        dateFormat.isLenient = false
        try {
            val parsedDate = dateFormat.parse(dateStr)
            if (parsedDate != null) {
                val calendar = Calendar.getInstance()
                calendar.time = parsedDate

                if (calendar.get(Calendar.YEAR) < 1900) {
                    _dobError.value = "Year seems too far in the past"
                    return false
                }
                if (parsedDate.after(Date())) {
                    _dobError.value = "Date of birth cannot be in the future"
                    return false
                }
            } else {
                _dobError.value = "Invalid date"
                return false
            }
        } catch (e: java.text.ParseException) {
            _dobError.value = "Invalid date format (MM/DD/YYYY)"
            return false
        }
        _dobError.value = null
        return true
    }

    private fun validateNationality(nat: String = _nationality.value): Boolean {
        return if (nat.isBlank()) {
            _nationalityError.value = "Nationality cannot be empty"
            false
        } else {
            _nationalityError.value = null
            true
        }
    }

    private fun validateAllFields(): Boolean {
        val isFirstNameValid = validateFirstName()
        val isLastNameValid = validateLastName()
        val isDobValid = validateDob()
        val isNationalityValid = validateNationality()
        val isGenderSelected = _gender.value.isNotEmpty()

        checkFormCompleteInternal(
            isFirstNameValid, isLastNameValid, isDobValid, isNationalityValid, isGenderSelected
        )
        return isFirstNameValid && isLastNameValid && isDobValid && isNationalityValid && isGenderSelected
    }

    fun updateFirstName(newFirstName: String) {
        _firstName.value = newFirstName
        validateFirstName(newFirstName)
        checkFormCompleteInternal()
    }

    fun updateLastName(newLastName: String) {
        _lastName.value = newLastName
        validateLastName(newLastName)
        checkFormCompleteInternal()
    }

    fun updateDob(newDob: String) {
        _dob.value = newDob
        validateDob(newDob)
        checkFormCompleteInternal()
    }

    fun updateNationality(newNationality: String) {
        _nationality.value = newNationality
        validateNationality(newNationality)
        checkFormCompleteInternal()
    }

    fun updateGender(newGender: String) {
        _gender.value = newGender
        checkFormCompleteInternal()
    }

    private fun checkFormCompleteInternal(
        isFirstNameValid: Boolean = _firstNameError.value == null && _firstName.value.isNotEmpty(),
        isLastNameValid: Boolean = _lastNameError.value == null && _lastName.value.isNotEmpty(),
        isDobValid: Boolean = _dobError.value == null && _dob.value.isNotEmpty(),
        isNationalityValid: Boolean = _nationalityError.value == null && _nationality.value.isNotEmpty(),
        isGenderSelected: Boolean = _gender.value.isNotEmpty()
    ) {
        _isFormComplete.value = isFirstNameValid && isLastNameValid &&
                isDobValid && isNationalityValid && isGenderSelected
    }

    fun saveProfileData() {
        val isFormValid = validateAllFields()
        if (!isFormValid) {
            Log.d("ProfileViewModel", "Form is invalid. Save aborted.")
            return
        }

        val profile = UserProfile(
            firstName = firstName.value,
            lastName = lastName.value,
            dob = dob.value,
            nationality = nationality.value,
            gender = gender.value
        )

        viewModelScope.launch {
            _isLoading.value = true
            Log.d("ProfileViewModel", "Attempting to save profile: $profile")
            try {
                profileRepository.saveUserProfile(profile)
                Log.d("ProfileViewModel", "Profile save operation completed via repository.")
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error saving profile data via repository", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadProfileData() {
        viewModelScope.launch {
            _isLoading.value = true
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
                } ?: run {
                    _firstName.value = ""
                    _lastName.value = ""
                    _dob.value = ""
                    _nationality.value = ""
                    _gender.value = ""
                }
                validateAllFields()
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error loading profile data via repository", e)
                _firstName.value = ""
                _lastName.value = ""
                _dob.value = ""
                _nationality.value = ""
                _gender.value = ""
                validateAllFields()
            } finally {
                _isLoading.value = false
            }
        }
    }
}