package com.example.myprofileapp.model

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
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
    private val _titleError = MutableStateFlow<String?>(null)
    val titleError: StateFlow<String?> = _titleError

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description
    private val _descriptionError = MutableStateFlow<String?>(null)
    val descriptionError: StateFlow<String?> = _descriptionError

    private val _price = MutableStateFlow("") // Store as String for TextField
    val price: StateFlow<String> = _price
    private val _priceError = MutableStateFlow<String?>(null)
    val priceError: StateFlow<String?> = _priceError

    private val _category = MutableStateFlow("")
    val category: StateFlow<String> = _category
    private val _categoryError = MutableStateFlow<String?>(null)
    val categoryError: StateFlow<String?> = _categoryError
    val categories = listOf("Electronics", "Furniture", "Vehicle", "Property", "Other") // Example categories

    private val _condition = MutableStateFlow("")
    val condition: StateFlow<String> = _condition
    private val _conditionError = MutableStateFlow<String?>(null)
    val conditionError: StateFlow<String?> = _conditionError
    val conditions = listOf("New", "Used - Like New", "Used - Good", "Used - Fair") // Example conditions

    private val _location = MutableStateFlow("")
    val location: StateFlow<String> = _location
    private val _locationError = MutableStateFlow<String?>(null)
    val locationError: StateFlow<String?> = _locationError

    private val _imageUris = MutableStateFlow<List<Uri>>(emptyList()) // Store Uri objects from picker
    val imageUris: StateFlow<List<Uri>> = _imageUris
    private val _imageUrisError = MutableStateFlow<String?>(null)
    val imageUrisError: StateFlow<String?> = _imageUrisError


    private val _isAdPosting = MutableStateFlow(false)
    val isAdPosting: StateFlow<Boolean> = _isAdPosting

    private val _adPostSuccess = MutableStateFlow<Boolean?>(null) // null = not attempted, true = success, false = fail
    val adPostSuccess: StateFlow<Boolean?> = _adPostSuccess

    fun onTitleChange(newTitle: String) { _title.value = newTitle; validateTitle() }
    fun onDescriptionChange(newDescription: String) { _description.value = newDescription; validateDescription() }
    fun onPriceChange(newPrice: String) { _price.value = newPrice; validatePrice() }
    fun onCategoryChange(newCategory: String) { _category.value = newCategory; validateCategory() }
    fun onConditionChange(newCondition: String) { _condition.value = newCondition; validateCondition() }
    fun onLocationChange(newLocation: String) { _location.value = newLocation; validateLocation() }
    fun onImagesSelected(uris: List<Uri>) { _imageUris.value = uris; validateImageUris() }


    private fun validateTitle(): Boolean { /* ... validation logic ... */ return true }
    private fun validateDescription(): Boolean { /* ... validation logic ... */ return true }
    private fun validatePrice(): Boolean { /* ... validation logic ... */ return true }
    private fun validateCategory(): Boolean { /* ... validation logic ... */ return true }
    private fun validateCondition(): Boolean { /* ... validation logic ... */ return true }
    private fun validateLocation(): Boolean { /* ... validation logic ... */ return true }
    private fun validateImageUris(): Boolean { /* ... validation logic ... */ return true }


    private fun validateAllFields(): Boolean {
        val titleValid = validateTitle()
        val descValid = validateDescription()
        val priceValid = validatePrice()
        val categoryValid = validateCategory()
        val conditionValid = validateCondition()
        val locationValid = validateLocation()
        val imagesValid = validateImageUris() // E.g., at least one image
        return titleValid && descValid && priceValid && categoryValid && conditionValid && locationValid && imagesValid
    }

    fun postAd() {
        if (!validateAllFields()) {
            return
        }
        viewModelScope.launch {
            _isAdPosting.value = true
            _adPostSuccess.value = null
            val currentUserProfile = profileRepository.getUserProfile() // Assuming this gets the current user
            if (currentUserProfile == null) {
                _isAdPosting.value = false
                _adPostSuccess.value = false // Or handle error more specifically
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
                imageUris = _imageUris.value.joinToString(",") { it.toString() }, // Convert List<Uri> to comma-separated String
                postedDate = System.currentTimeMillis()
                // status defaults to "Active"
            )
            try {
                listingRepository.createAd(ad)
                _adPostSuccess.value = true
            } catch (e: Exception) {
                // Log error, set error message for UI
                _adPostSuccess.value = false
            } finally {
                _isAdPosting.value = false
            }
        }
    }
}