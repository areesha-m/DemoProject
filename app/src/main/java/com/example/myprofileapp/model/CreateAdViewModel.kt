package com.example.myprofileapp.model

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewModelScope
import com.example.myprofileapp.data.AdListing
import com.example.myprofileapp.data.IListingRepository
import com.example.myprofileapp.data.IProfileRepository // To get current user ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAdViewModel @Inject constructor(
    application: Application,
    private val listingRepository: IListingRepository,
    private val profileRepository: IProfileRepository // To get current user's ID
) : AndroidViewModel(application) {

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description

    private val _price = MutableStateFlow("")
    val price: StateFlow<String> = _price

    private val _subcategory = MutableStateFlow("")
    val subcategory: StateFlow<String> = _subcategory


    private val _city = MutableStateFlow("")
    val city: StateFlow<String> = _city

    private val _category = MutableStateFlow("")
    val category: StateFlow<String> = _category

    private val _condition = MutableStateFlow("")
    val condition: StateFlow<String> = _condition
    val conditions = listOf("New", "Used - Like New", "Used - Good", "Used - Fair")

    private val _location = MutableStateFlow("")
    val location: StateFlow<String> = _location

    private val _imageUris = MutableStateFlow<List<Uri>>(emptyList())
    val imageUris: StateFlow<List<Uri>> = _imageUris

    private val _isAdPosting = MutableStateFlow(false)
    val isAdPosting: StateFlow<Boolean> = _isAdPosting

    private val _adPostSuccess = MutableStateFlow<Boolean?>(null)
    val adPostSuccess: StateFlow<Boolean?> = _adPostSuccess

    fun onCitySelected(selectedCity: String) {
        _city.value = selectedCity
        _location.value = selectedCity
    }

    fun onCategorySelected(selectedCategory: String) {
        _category.value = selectedCategory
    }
    fun onSubCategorySelected(subCategory: String) {
        _subcategory.value = subCategory
    }

    fun onTitleChange(newTitle: String) { _title.value = newTitle }
    fun onDescriptionChange(newDescription: String) { _description.value = newDescription }
    fun onPriceChange(newPrice: String) { _price.value = newPrice }
    fun onConditionChange(newCondition: String) { _condition.value = newCondition }
    fun onLocationChange(newLocation: String) { _location.value = newLocation } // Allow user to edit pre-filled city
    fun onImagesSelected(uris: List<Uri>) { _imageUris.value = uris }

    private fun validateAllFields(): Boolean {
        return title.value.isNotBlank() && description.value.isNotBlank() && price.value.isNotBlank() &&
                condition.value.isNotBlank() && location.value.isNotBlank() && imageUris.value.isNotEmpty()
    }

    fun postAd() {
        if (!validateAllFields()) {
            Log.e("CreateAdViewModel", "Validation failed. Aborting post.")
            viewModelScope.launch { _adPostSuccess.value = false }
            return
        }
        viewModelScope.launch {
            _isAdPosting.value = true
            _adPostSuccess.value = null
            val currentUserProfile = profileRepository.getUserProfile()
            if (currentUserProfile == null) {
                Log.e("CreateAdViewModel", "Failed to post ad: No current user profile found.")
                _isAdPosting.value = false
                _adPostSuccess.value = false
                return@launch
            }

            val ad = AdListing(
                userId = currentUserProfile.id,
                title = _title.value,
                description = _description.value,
                category = _category.value,
                price = _price.value.toDoubleOrNull() ?: 0.0,
                condition = _condition.value,
                location = _location.value,
                imageUris = _imageUris.value.joinToString(",") { it.toString() },
                postedDate = System.currentTimeMillis()
            )
            try {
                listingRepository.createAd(ad)
                _adPostSuccess.value = true
            } catch (e: Exception) {
                Log.e("CreateAdViewModel", "Error saving ad to repository", e)
                _adPostSuccess.value = false
            } finally {
                _isAdPosting.value = false
            }
        }
    }
}
