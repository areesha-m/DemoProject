package com.example.myprofileapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.sp
import com.example.myprofileapp.ui.theme.MyProfileScreen
import java.util.Calendar
import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myprofileapp.model.ProfileViewModel
import com.example.myprofileapp.navigation.Screen
import com.example.myprofileapp.ui.theme.ProfileSummaryScreen
import kotlinx.coroutines.launch
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myprofileapp.ui.theme.CreateAdScreen
import com.example.myprofileapp.ui.theme.MyAdsScreen
import androidx.navigation.NavController
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyProfileScreen {
                val navController = rememberNavController()
                val viewModel: ProfileViewModel = hiltViewModel()

                NavHost(navController = navController, startDestination = Screen.EditProfile.route) {
                    composable(Screen.EditProfile.route) {
                        ProfileContent(
                            viewModel = viewModel,
                            navController = navController,
                            onSaveComplete = {
                                navController.navigate(Screen.ProfileSummary.route)
                            }
                        )
                    }

                    composable(Screen.ProfileSummary.route) {
                        ProfileSummaryScreen(viewModel = viewModel, onEditClicked = {
                            navController.popBackStack()
                        })
                    }
                    composable(Screen.CreateAd.route) {
                        CreateAdScreen(
                            navController = navController,
                            onAdPostedSuccessfully = {

                                navController.navigate(Screen.MyAds.route) {
                                    popUpTo(Screen.CreateAd.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(Screen.MyAds.route) {
                        MyAdsScreen(
                            navController = navController

                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(viewModel: ProfileViewModel, navController: NavController, onSaveComplete: () -> Unit) {
    val firstName by viewModel.firstName.collectAsState()
    val lastName by viewModel.lastName.collectAsState()
    val dob by viewModel.dob.collectAsState()
    val nationality by viewModel.nationality.collectAsState()
    val gender by viewModel.gender.collectAsState()
    val isFormComplete by viewModel.isFormComplete.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Profile", fontSize = 15.sp) },
                navigationIcon = {
                    IconButton(onClick = {  }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back_arrow), // Ensure this drawable exists
                            contentDescription = "Back Arrow",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                modifier = Modifier.shadow(3.dp)
            )
        },

        bottomBar = {
            Column {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.saveProfileData()
                            onSaveComplete()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = !isFormComplete && !isLoading,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    )
                    ) {
                    if (isLoading && isFormComplete) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text(text = "Save Changes", fontWeight = FontWeight.Medium)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()), //

        verticalArrangement = Arrangement.Top
        ) {
            if (isLoading && firstName.isBlank()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Text("Profile Name", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("This is displayed on your profile", color = Color.Gray, fontSize = 13.sp)

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = firstName,
                    onValueChange = { viewModel.updateFirstName(it) },
                    label = { Text("First Name", fontSize = 12.sp) },
                    textStyle = TextStyle(fontSize = 14.sp),
                    modifier = Modifier
                        .fillMaxWidth().height(54.dp),
                    singleLine = true,
                    enabled = !isLoading,
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.LightGray,
                        unfocusedIndicatorColor = Color.LightGray,
                        disabledIndicatorColor = Color.LightGray,
                        cursorColor = Color.Black
                    )

                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = lastName,
                    onValueChange = { viewModel.updateLastName(it) },
                    label = { Text("Last Name", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    textStyle = TextStyle(fontSize = 14.sp),
                    singleLine = true,
                    enabled = !isLoading,
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.LightGray,
                        unfocusedIndicatorColor = Color.LightGray,
                        disabledIndicatorColor = Color.LightGray,
                        cursorColor = Color.Black
                    )

                )

                Spacer(modifier = Modifier.height(32.dp))

                Text("Account details", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("This is not visible to other users", color = Color.Gray, fontSize = 13.sp)

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_calendar_month), // Ensure this drawable exists
                        contentDescription = "Calendar Icon",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Date of birth", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                val calendar = Calendar.getInstance()
                val datePickerDialog = DatePickerDialog(
                    context,
                    { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                        viewModel.updateDob("${selectedMonth + 1}/$selectedDayOfMonth/$selectedYear")
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )

                OutlinedTextField(
                    value = dob,
                    onValueChange = { },
                    readOnly = true,
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clickable(enabled = !isLoading) { datePickerDialog.show() },
                    placeholder = { Text("MM/DD/YYYY") },
                    label = { Text("Date of Birth", fontSize = 12.sp) },
                    textStyle = TextStyle(fontSize = 14.sp),
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }, enabled = !isLoading) {
                            Icon(
                                painter = painterResource(id = R.drawable.event),
                                contentDescription = "Calendar Icon",
                                tint = Color.LightGray
                            )
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.LightGray,
                        unfocusedIndicatorColor = Color.LightGray,
                        disabledIndicatorColor = Color.LightGray,
                        cursorColor = Color.Black
                    ),
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.nationality),
                        contentDescription = "Language Icon",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Nationality", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

               OutlinedTextField(
                    value = nationality,
                    onValueChange = { viewModel.updateNationality(it) },
                    label = { Text("Nationality", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    textStyle = TextStyle(fontSize = 14.sp),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                       focusedContainerColor = Color.Transparent,
                       unfocusedContainerColor = Color.Transparent,
                       disabledContainerColor = Color.Transparent,
                       focusedIndicatorColor = Color.LightGray,
                        unfocusedIndicatorColor = Color.LightGray,
                       disabledIndicatorColor = Color.LightGray,
                       cursorColor = Color.Black
                    )

               )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.gender),
                        contentDescription = "User Icon",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Gender", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = gender == "Male",
                        onClick = { viewModel.updateGender("Male") },
                        enabled = !isLoading,
                        modifier = Modifier.scale(0.8f)
                    )
                    Text(text = "Male", fontSize = 13.sp, fontWeight = if (gender == "Male") FontWeight.Bold else FontWeight.Normal, modifier = Modifier.padding(end = 16.dp))

                    RadioButton(
                        selected = gender == "Female",
                        onClick = { viewModel.updateGender("Female") },
                        enabled = !isLoading,
                        modifier = Modifier.scale(0.8f)
                    )
                    Text(text = "Female", fontSize = 13.sp, fontWeight = if (gender == "Female") FontWeight.Bold else FontWeight.Normal)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = gender == "Prefer not to say",
                        onClick = { viewModel.updateGender("Prefer not to say") },
                        enabled = !isLoading,
                        modifier = Modifier.scale(0.8f)
                    )
                    Text(text = "Prefer not to say", fontSize = 13.sp, fontWeight = if (gender == "Prefer not to say") FontWeight.Bold else FontWeight.Normal)
                }
                Spacer(modifier = Modifier.height(60.dp))

                Row(verticalAlignment = Alignment.CenterVertically,     horizontalArrangement = Arrangement.spacedBy(8.dp), // Add spacing between buttons
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            navController.navigate(Screen.CreateAd.route)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        )
                    ) {
                        Text(text = "Create New Ad")
                    }

                    Button(
                        onClick = {
                            navController.navigate(Screen.MyAds.route)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        )

                    ) {
                        Text(text = "View My Ads")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            }
        }
}

