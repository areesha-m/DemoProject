package com.example.myprofileapp.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ad_listings",
    foreignKeys = [
        ForeignKey(
            entity = UserProfile::class,
            parentColumns = ["id"], // Field name in UserProfile
            childColumns = ["userId"], // Field name in AdListing
            onDelete = ForeignKey.CASCADE // If a UserProfile is deleted, their ads are also deleted
        )
    ],
    indices = [Index(value = ["userId"])] // Index for faster queries on userId
)
data class AdListing(
    @PrimaryKey(autoGenerate = true)
    val listingId: Int = 0,
    val userId: Int, // Foreign key referencing UserProfile.id
    var title: String,
    var description: String,
    var category: String, // e.g., "Electronics", "Furniture". Consider an Enum or separate table later.
    var price: Double,
    var condition: String, // e.g., "New", "Used", "Refurbished". Consider an Enum.
    var location: String, // Simple text for now.
    var imageUris: String, // Comma-separated string of image URIs. Use a TypeConverter for List<String> for a cleaner solution later.
    var postedDate: Long, // Timestamp (e.g., System.currentTimeMillis())
    var status: String = "Active" // e.g., "Active", "Sold", "Expired". Consider an Enum.
)