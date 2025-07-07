package com.example.myprofileapp.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myprofileapp.data.AdListing
import com.example.myprofileapp.data.IListingRepository
import com.example.myprofileapp.data.IProfileRepository // To get current user ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyAdsViewModel @Inject constructor(
    application: Application,
    private val listingRepository: IListingRepository,
    private val profileRepository: IProfileRepository
) : AndroidViewModel(application) {

    private val _myAds = MutableStateFlow<List<AdListing>>(emptyList())
    val myAds: StateFlow<List<AdListing>> = _myAds

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // For one-time events like "Ad Deleted" or "Error"
    private val _eventFlow = MutableSharedFlow<MyAdsEvent>()
    val eventFlow: SharedFlow<MyAdsEvent> = _eventFlow.asSharedFlow()

    private var currentUserId: Int? = null

    init {
        viewModelScope.launch {
            // Get user ID once and store it
            val user = profileRepository.getUserProfile()
            currentUserId = user?.id
            fetchMyAds() // Initial fetch
        }
    }

    fun fetchMyAds(forceRefresh: Boolean = false) {
        // If not forcing refresh and already loading, or no user ID, don't proceed
        if ((_isLoading.value && !forceRefresh) || currentUserId == null) {
            if(currentUserId == null) Log.e("MyAdsViewModel", "Cannot fetch ads, user ID is null.")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            currentUserId?.let { userId ->
                listingRepository.getAdsForUserFlow(userId)
                    .catch { exception ->
                        Log.e("MyAdsViewModel", "Error fetching ads: ${exception.message}", exception)
                        _eventFlow.emit(MyAdsEvent.Error("Failed to load ads: ${exception.localizedMessage}"))
                        _isLoading.value = false
                        _myAds.value = emptyList() // Clear ads on error
                    }
                    .collectLatest { ads ->
                        _myAds.value = ads
                        _isLoading.value = false
                    }
            } ?: run {
                // This case should ideally be handled by the initial check, but as a fallback:
                _myAds.value = emptyList()
                _isLoading.value = false
                Log.w("MyAdsViewModel", "User ID not available to fetch ads.")
            }
        }
    }

    fun deleteAd(adListing: AdListing) {
        viewModelScope.launch {
            try {
                // Optimistically remove from UI first or show a loading state for the item
                // For simplicity, we'll rely on the Flow to update after deletion.
                // You might want to add a specific loading state for the item being deleted.

                listingRepository.deleteAd(adListing.listingId) // Assuming IListingRepository has deleteAd
                _eventFlow.emit(MyAdsEvent.AdDeleted("Ad '${adListing.title}' deleted successfully."))
                // The list will automatically update if getAdsForUserFlow is used and DB changes.
                // If not using Flow for collection or if you want an immediate explicit refresh:
                // fetchMyAds(forceRefresh = true) // Uncomment if needed
            } catch (e: Exception) {
                Log.e("MyAdsViewModel", "Error deleting ad: ${e.message}", e)
                _eventFlow.emit(MyAdsEvent.Error("Failed to delete ad: ${e.localizedMessage}"))
            }
        }
    }

    fun onRefreshTriggered() {
        fetchMyAds(forceRefresh = true)
    }

    // Sealed class for UI events
    sealed class MyAdsEvent {
        data class AdDeleted(val message: String) : MyAdsEvent()
        data class Error(val message: String) : MyAdsEvent()
    }
}