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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myprofileapp.navigation.Screen
import com.example.myprofileapp.ui.theme.ProfileSummaryScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyProfileScreen {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Screen.EditProfile.route) {
                    composable(Screen.EditProfile.route) {
                        ProfileContent(onSaveComplete = {
                            navController.navigate(Screen.ProfileSummary.route)
                        })
                    }
                    composable(Screen.ProfileSummary.route) {
                        ProfileSummaryScreen(onEditClicked = {
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
fun ProfileContent(onSaveComplete: () -> Unit) {
    val viewModel: ProfileViewModel = viewModel()
    val firstName by viewModel.firstName.collectAsState()
    val lastName by viewModel.lastName.collectAsState()
    val dob by viewModel.dob.collectAsState()
    val nationality by viewModel.nationality.collectAsState()
    val gender by viewModel.gender.collectAsState()
    val isFormComplete by viewModel.isFormComplete.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()  // Collecting the loading state

    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Profile", fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = {  }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Column {
                Button(
                    onClick = { viewModel.saveProfileData()
                        onSaveComplete()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = isFormComplete
                ) {
                    Text(text = "Save Changes")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

        }
    ) {
        innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            if (isLoading) {
                // Show loading spinner while data is being fetched
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {

                Text("Profile Name", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("This is displayed on your profile", color = Color.Gray, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = firstName,
                    onValueChange = { viewModel.updateFirstName(it) },
                    label = { Text("First Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = lastName,
                    onValueChange = { viewModel.updateLastName(it) },
                    label = { Text("Last Name") },
                    modifier = Modifier.fillMaxWidth()
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
                        painter = painterResource(id = R.drawable.ic_calendar_month),
                        contentDescription = "Calendar Icon",
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text("Date of birth", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                // 🛠 Date of Birth Field with Calendar
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(
                    context,
                    { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                        viewModel.updateDob("${selectedMonth + 1}/$selectedDayOfMonth/$selectedYear")
                    },
                    year, month, day
                )

                OutlinedTextField(
                    value = dob,
                    onValueChange = { newValue ->
                        viewModel.updateDob(newValue)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() },
                    placeholder = { Text("MM/DD/YYYY") },
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(
                                painter = painterResource(id = R.drawable.event),
                                contentDescription = "Calendar Icon"
                            )
                        }
                    }
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
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Gender", fontWeight = FontWeight.Bold)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = gender == "Male",
                        onClick = { viewModel.updateGender("Male") }
                    )
                    Text(text = "Male", modifier = Modifier.padding(end = 16.dp))

                    RadioButton(
                        selected = gender == "Female",
                        onClick = { viewModel.updateGender("Female") }
                    )
                    Text(text = "Female")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = gender == "Prefer not to say",
                        onClick = { viewModel.updateGender("Prefer not to say") }
                    )
                    Text(text = "Prefer not to say")
                }
            }
        }
    }
}
