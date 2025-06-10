package com.example.myprofileapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow // Optional: Use Flow for reactive updates from "My Ads" screen

@Dao
interface AdListingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAd(adListing: AdListing): Long // Returns the rowId of the inserted item

    @Update
    suspend fun updateAd(adListing: AdListing)

    @Query("SELECT * FROM ad_listings WHERE listingId = :listingId")
    suspend fun getAdById(listingId: Int): AdListing?

    @Query("SELECT * FROM ad_listings WHERE userId = :userId ORDER BY postedDate DESC")
    suspend fun getUserAds(userId: Int): List<AdListing>

    // Optional: For reactive updates on the "My Ads" screen
    @Query("SELECT * FROM ad_listings WHERE userId = :userId ORDER BY postedDate DESC")
    fun getUserAdsFlow(userId: Int): Flow<List<AdListing>>

    @Query("SELECT * FROM ad_listings WHERE status = 'Active' ORDER BY postedDate DESC")
    suspend fun getAllActiveAds(): List<AdListing>

    // Optional: For reactive updates on a general Browse screen
    @Query("SELECT * FROM ad_listings WHERE status = 'Active' ORDER BY postedDate DESC")
    fun getAllActiveAdsFlow(): Flow<List<AdListing>>

    @Query("DELETE FROM ad_listings WHERE listingId = :listingId")
    suspend fun deleteAdById(listingId: Int)
}