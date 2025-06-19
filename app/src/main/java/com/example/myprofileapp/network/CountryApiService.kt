package com.example.myprofileapp.network

import com.example.myprofileapp.model.Country
import retrofit2.http.GET

interface CountryApiService {
    @GET("independent?status=true&fields=name,cca2")
    suspend fun getCountries(): List<CountryResponse>
}

data class CountryResponse(
    val name: Name,
    val cca2: String? = null // Add this field since you're requesting it
) {
    data class Name(val common: String)

    fun toCountry() = Country(name.common)
}