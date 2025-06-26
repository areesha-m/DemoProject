package com.example.myprofileapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    val firstName: String,
    val lastName: String,
    val dob: String,
    val nationality: String,
    val gender: String
)
