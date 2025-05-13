package com.example.myprofileapp.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myprofileapp.model.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSummaryScreen(viewModel: ProfileViewModel, onEditClicked: () -> Unit) {
    val firstName by viewModel.firstName.collectAsState()
    val lastName by viewModel.lastName.collectAsState()
    val dob by viewModel.dob.collectAsState()
    val nationality by viewModel.nationality.collectAsState()
    val gender by viewModel.gender.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile Summary", fontSize = 16.sp) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Text("Name: $firstName $lastName")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Date of Birth: $dob")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Nationality: $nationality")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Gender: $gender")
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onEditClicked) {
                    Text("Edit Profile")
                }
            }
        }
    }

}
