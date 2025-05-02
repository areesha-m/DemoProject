package com.example.myprofileapp.navigation

sealed class Screen(val route: String) {
    object EditProfile : Screen("edit_profile")
    object ProfileSummary : Screen("profile_summary")
}
