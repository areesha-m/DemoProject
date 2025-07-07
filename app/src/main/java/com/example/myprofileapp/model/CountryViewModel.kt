package com.example.myprofileapp.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log
import com.example.myprofileapp.utils.NetworkChecker
import kotlinx.coroutines.CoroutineExceptionHandler

@HiltViewModel
class CountryViewModel @Inject constructor(
    internal val repository: CountryRepository,
    private val networkChecker: NetworkChecker
) : ViewModel() {

    private val _countryNames = MutableStateFlow<List<String>>(emptyList())
    val countryNames: StateFlow<List<String>> = _countryNames

    internal val _loadCountries = MutableStateFlow(false)

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    val selectedCountry = MutableStateFlow("")

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("CountryViewModel", "Caught exception in CoroutineExceptionHandler: ${throwable.message}", throwable)
        _loadCountries.value = false
        _isError.value = true
        _errorMessage.value = when (throwable) {
            is NetworkException -> throwable.message ?: "Network error occurred"
            else -> "An unexpected error occurred: ${throwable.localizedMessage ?: "Unknown error"}"
        }
        _countryNames.value = emptyList()
    }

    init {
        fetchCountries()
    }

    fun fetchCountries() {
        viewModelScope.launch(exceptionHandler) {
            _loadCountries.value = true
            _isError.value = false
            _errorMessage.value = ""

            if (!networkChecker.hasInternetConnection()) {
                _loadCountries.value = false
                _isError.value = true
                _errorMessage.value = "No internet connection. Please check your network."
                _countryNames.value = emptyList()
                return@launch
            }

            try {
                Log.d("CountryViewModel", "Starting to fetch countries...")
                val countries = repository.fetchCountries()
                _countryNames.value = countries.map { it.name }.sorted()
                Log.d("CountryViewModel", "Successfully fetched ${countries.size} countries")
            } catch (e: Exception) {
                Log.e("CountryViewModel", "Error in fetchCountries (inside launch block): ${e.message}", e)
                _isError.value = true
                _errorMessage.value = when (e) {
                    is NetworkException -> e.message ?: "Network error occurred"
                    else -> "An unexpected error occurred"
                }
                _countryNames.value = emptyList()
            } finally {
                _loadCountries.value = false
            }
        }
    }

    fun updateSelectedCountry(name: String) {
        selectedCountry.value = name
    }

    fun clearError() {
        _isError.value = false
        _errorMessage.value = ""
    }
}