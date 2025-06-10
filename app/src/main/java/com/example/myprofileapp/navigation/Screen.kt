package com.example.myprofileapp.navigation

sealed class Screen(val route: String) {
    data object EditProfile : Screen("edit_profile")
    data object ProfileSummary : Screen("profile_summary")
    data object CreateAd : Screen("create_ad") // For creating a new advertisement
    data object MyAds : Screen("my_ads")       // For viewing the user's own advertisements

}