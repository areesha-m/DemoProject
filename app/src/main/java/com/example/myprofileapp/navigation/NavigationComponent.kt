package com.example.myprofileapp.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myprofileapp.ui.theme.components.ProfileContent
import com.example.myprofileapp.model.CreateAdViewModel
import com.example.myprofileapp.model.ProfileViewModel
import com.example.myprofileapp.ui.theme.MyAdsScreen
import com.example.myprofileapp.ui.theme.ProfileSummaryScreen
import com.example.myprofileapp.ui.theme.components.OrdersScreen
import com.example.myprofileapp.ui.theme.postAdScreens.CreateAdScreen
import com.example.myprofileapp.ui.theme.postAdScreens.SelectCategoryScreen
import com.example.myprofileapp.ui.theme.postAdScreens.SelectCityScreen
import com.example.myprofileapp.ui.theme.postAdScreens.motorSubCategoryScreens.MotorsSubCategoryScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val adViewModel: CreateAdViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = Screen.OrdersScreen.route) {
        composable(Screen.EditProfile.route) {
            ProfileContent(
                viewModel = profileViewModel,
                navController = navController,
                onSaveComplete = {
                    navController.navigate(Screen.ProfileSummary.route)
                }
            )
        }
        composable(Screen.ProfileSummary.route) {
            ProfileSummaryScreen(
                viewModel = profileViewModel,
                onEditClicked = {
                    navController.navigate(Screen.EditProfile.route)
                }
            )
        }

        composable(Screen.SelectCity.route) {
            SelectCityScreen(navController = navController, viewModel = adViewModel)
        }

        composable(Screen.SelectCategory.route) {
            SelectCategoryScreen(navController = navController, viewModel = adViewModel)
        }

        composable(Screen.SelectMotorsSubCategory.route) {
            MotorsSubCategoryScreen(navController = navController, viewModel = adViewModel)
        }


        composable(Screen.MyAds.route) {
            MyAdsScreen(navController = navController)
        }

        composable(Screen.CreateAd.route) {
            CreateAdScreen(
                navController = navController,
                viewModel = adViewModel,
                onAdPostedSuccessfully = {
                    navController.navigate(Screen.MyAds.route) {
                    }
                }
            )
        }

        composable(Screen.OrdersScreen.route) {
            OrdersScreen()
        }
    }
}

