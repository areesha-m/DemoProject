package com.example.myprofileapp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine

class ProfileViewModel : ViewModel() {

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

    val isFormComplete: Flow<Boolean> = combine(
        firstName, lastName, dob, nationality, gender
    ) { f, l, d, n, g ->
        f.isNotBlank() && l.isNotBlank() && d.isNotBlank() && n.isNotBlank() && g.isNotBlank()
    }

    fun updateFirstName(value: String) { _firstName.value = value }
    fun updateLastName(value: String) { _lastName.value = value }
    fun updateDob(value: String) { _dob.value = value }
    fun updateNationality(value: String) { _nationality.value = value }
    fun updateGender(value: String) { _gender.value = value }
}
