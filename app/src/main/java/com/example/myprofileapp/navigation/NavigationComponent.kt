package com.example.myprofileapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.myprofileapp.ui.theme.components.ProfileContent
import com.example.myprofileapp.model.CreateAdViewModel
import com.example.myprofileapp.model.ProfileViewModel
import com.example.myprofileapp.ui.theme.MyAdsScreen
import com.example.myprofileapp.ui.theme.ProfileSummaryScreen
import com.example.myprofileapp.ui.theme.postAdScreens.CreateAdScreen
import com.example.myprofileapp.ui.theme.postAdScreens.SelectCategoryScreen
import com.example.myprofileapp.ui.theme.postAdScreens.SelectCityScreen
import com.example.myprofileapp.ui.theme.postAdScreens.motorSubCategoryScreens.MotorsSubCategoryScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val profileViewModel: ProfileViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = Screen.EditProfile.route) {
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
                onEditClicked = { navController.popBackStack() }
            )
        }
        composable(Screen.MyAds.route) {
            MyAdsScreen(navController = navController)
        }

        createAdGraph(navController)
    }
}

// Extension function to define the nested navigation graph for ad creation
fun NavGraphBuilder.createAdGraph(navController: NavController) {
    navigation(startDestination = Screen.SelectCity.route, route = Screen.CreateAdFlow.route) {

        // Step 1: Select City
        composable(Screen.SelectCity.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.CreateAdFlow.route)
            }
            val adViewModel: CreateAdViewModel = hiltViewModel(parentEntry)
            SelectCityScreen(navController = navController, viewModel = adViewModel)
        }

        // Step 2: Select Category
        composable(Screen.SelectCategory.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.CreateAdFlow.route)
            }
            val adViewModel: CreateAdViewModel = hiltViewModel(parentEntry)
            SelectCategoryScreen(navController = navController, viewModel = adViewModel)
        }

        // Step 3: Select Motors Sub Category
        composable(Screen.SelectMotorsSubCategory.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.CreateAdFlow.route)
            }
            val adViewModel: CreateAdViewModel = hiltViewModel(parentEntry)
            MotorsSubCategoryScreen(navController = navController, viewModel = adViewModel)
        }

        // Step 4: Fill in Ad Details (The final form)
        composable(Screen.CreateAd.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.CreateAdFlow.route)
            }
            val adViewModel: CreateAdViewModel = hiltViewModel(parentEntry)
            CreateAdScreen(
                navController = navController,
                viewModel = adViewModel,
                onAdPostedSuccessfully = {
                    navController.navigate(Screen.MyAds.route) {
                        popUpTo(Screen.CreateAdFlow.route) { inclusive = true }
                    }
                }
            )
        }
    }
}