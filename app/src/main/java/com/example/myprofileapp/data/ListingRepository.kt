package com.example.myprofileapp.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

interface IListingRepository {
    suspend fun createAd(adListing: AdListing): Long
    suspend fun getAdsForUser(userId: Int): List<AdListing>
    fun getAdsForUserFlow(userId: Int): Flow<List<AdListing>> // For reactive updates
    // Add other methods like getAdDetails, updateAd, deleteAd, getAllActiveAds etc. as needed
    suspend fun deleteAd(listingId: Int) // <<< ENSURE THIS LINE EXISTS

}

@Singleton
class ListingRepository @Inject constructor(
    private val adListingDao: AdListingDao
) : IListingRepository {

    override suspend fun createAd(adListing: AdListing): Long {
        return withContext(Dispatchers.IO) {
            adListingDao.insertAd(adListing)
        }
    }

    override suspend fun getAdsForUser(userId: Int): List<AdListing> {
        return withContext(Dispatchers.IO) {
            adListingDao.getUserAds(userId)
        }
    }

    override fun getAdsForUserFlow(userId: Int): Flow<List<AdListing>> {
        // Flow runs on its own dispatcher context provided by Room
        return adListingDao.getUserAdsFlow(userId)
    }
    override suspend fun deleteAd(listingId: Int) { // <<< ENSURE THIS METHOD IS IMPLEMENTED
        withContext(Dispatchers.IO) {
            adListingDao.deleteAdById(listingId)
        }
    }

}