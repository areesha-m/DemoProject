package com.example.myprofileapp.model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myprofileapp.data.IProfileRepository

class ProfileViewModelFactory(
    private val application: Application,
    private val profileRepository: IProfileRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(application, profileRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
