package com.example.myprofileapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myprofileapp.ui.theme.MyProfileScreen
import java.util.Calendar
import android.app.DatePickerDialog
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.font.FontWeight
import com.example.myprofileapp.data.DatabaseHelper
import com.example.myprofileapp.data.ProfileRepository
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myprofileapp.model.ProfileViewModel
import com.example.myprofileapp.model.ProfileViewModelFactory
import com.example.myprofileapp.navigation.Screen
import com.example.myprofileapp.ui.theme.ProfileSummaryScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup for ViewModel with repository
        val application = this.application // Get application instance
        val userProfileDao = DatabaseHelper.getDatabase(application).userProfileDao()
        val profileRepository = ProfileRepository(userProfileDao) // Use concrete class for instantiation
        val viewModelFactory = ProfileViewModelFactory(application, profileRepository)

        setContent {
            MyProfileScreen { // Assuming MyProfileScreen is a Composable that sets up the theme
                val navController = rememberNavController()
                val viewModel: ProfileViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = viewModelFactory
                )

                NavHost(navController = navController, startDestination = Screen.EditProfile.route) {
                    composable(Screen.EditProfile.route) {
                        ProfileContent(viewModel = viewModel, onSaveComplete = {
                            navController.navigate(Screen.ProfileSummary.route)
                        })
                    }
                    composable(Screen.ProfileSummary.route) {
                        ProfileSummaryScreen(viewModel = viewModel, onEditClicked = {
                            navController.popBackStack()
                        })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(viewModel: ProfileViewModel, onSaveComplete: () -> Unit) {
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
                title = { Text("My Profile", fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back action if necessary */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Column {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.saveProfileData() // This will now use the repository
                            // The loadProfileData within saveProfileData (or called after) will refresh state
                            onSaveComplete()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = isFormComplete && !isLoading // Disable button also when loading
                ) {
                    if (isLoading && isFormComplete) { // Show progress in button if saving
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text(text = "Save Changes")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp)) // Keep if needed for layout
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            if (isLoading && firstName.isBlank()) { // Show global loading indicator only on initial load
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Text("Profile Name", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("This is displayed on your profile", color = Color.Gray, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = firstName,
                    onValueChange = { viewModel.updateFirstName(it) },
                    label = { Text("First Name") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading // Disable fields when loading
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = lastName,
                    onValueChange = { viewModel.updateLastName(it) },
                    label = { Text("Last Name") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text("Account details", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("This is not visible to other users", color = Color.Gray, fontSize = 14.sp)

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
                    onValueChange = {  },
                    readOnly = true, // Make it read-only, value changed by picker
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !isLoading) { datePickerDialog.show() },
                    placeholder = { Text("MM/DD/YYYY") },
                    label = { Text("Date of Birth") }, // Added label for consistency
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }, enabled = !isLoading) {
                            Icon(
                                painter = painterResource(id = R.drawable.event), // Ensure this drawable exists
                                contentDescription = "Calendar Icon"
                            )
                        }
                    },
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Nationality",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = nationality,
                    onValueChange = { viewModel.updateNationality(it) },
                    label = { Text("Nationality") }, // Added label
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Gender", fontWeight = FontWeight.Bold)
                // Consider using a RadioButtonGroup Composable if this section grows
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = gender == "Male",
                        onClick = { viewModel.updateGender("Male") },
                        enabled = !isLoading
                    )
                    Text(text = "Male", modifier = Modifier.padding(end = 16.dp))

                    RadioButton(
                        selected = gender == "Female",
                        onClick = { viewModel.updateGender("Female") },
                        enabled = !isLoading
                    )
                    Text(text = "Female")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = gender == "Prefer not to say",
                        onClick = { viewModel.updateGender("Prefer not to say") },
                        enabled = !isLoading
                    )
                    Text(text = "Prefer not to say")
                }
            }
        }
    }
}
