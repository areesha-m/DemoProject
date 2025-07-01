package com.example.myprofileapp.navigation

sealed class Screen(val route: String) {
    object EditProfile : Screen("edit_profile")
    object ProfileSummary : Screen("profile_summary")
    object MyAds : Screen("my_ads")
    object SelectCity : Screen("select_city")
    object SelectCategory : Screen("select_category")
    object SelectMotorsSubCategory : Screen("select_motors_subcategory")
    object CreateAd : Screen("create_ad_details_form")
    object OrdersScreen : Screen("orders_screen")
    object OrderDetails : Screen("order_details")
}

