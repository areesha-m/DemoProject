package com.example.myprofileapp.model

import com.example.myprofileapp.network.CountryApiService
import com.example.myprofileapp.network.CountryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import android.util.Log
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


class CountryRepository @Inject constructor(
    private val api: CountryApiService
) {
    suspend fun fetchCountries(): List<Country> = withContext(Dispatchers.IO) {
        try {
            Log.d("CountryRepository", "Starting API call...")
            val response = api.getCountries()
            Log.d("CountryRepository", "API call successful, received ${response.size} countries")

            response.map(CountryResponse::toCountry)
        } catch (e: UnknownHostException) {
            Log.e("CountryRepository", "Network error - Unknown host", e)
            throw NetworkException("No internet connection. Please check your network.", e)
        } catch (e: SocketTimeoutException) {
            Log.e("CountryRepository", "Network error - Timeout", e)
            throw NetworkException("Request timed out. Please try again.", e)
        } catch (e: HttpException) {
            Log.e("CountryRepository", "HTTP error - ${e.code()}", e)
            throw NetworkException("Server error (${e.code()}). Please try again later.", e)
        } catch (e: IOException) {
            Log.e("CountryRepository", "IO error", e)
            throw NetworkException("Network error. Please check your connection.", e)
        } catch (e: Exception) {
            Log.e("CountryRepository", "Unexpected error", e)
            throw NetworkException("An unexpected error occurred. Please try again.", e)
        }
    }
}

class NetworkException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
