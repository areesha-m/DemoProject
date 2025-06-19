package com.example.myprofileapp.ui.theme.components

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myprofileapp.R
import com.example.myprofileapp.model.ProfileViewModel
import com.example.myprofileapp.navigation.Screen
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun ProfileContent(
    viewModel: ProfileViewModel,
    navController: NavController,
    onSaveComplete: () -> Unit
) {
    val uiState = ProfileContentUiState.fromViewModel(viewModel)
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            ProfileTopBar()
        },
        bottomBar = {
            ProfileBottomBar(
                isFormComplete = uiState.isFormComplete,
                isLoading = uiState.isLoading,
                onSaveClicked = {
                    coroutineScope.launch {
                        viewModel.saveProfileData()
                        onSaveComplete()
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading && uiState.firstName.isBlank()) {
            LoadingScreen()
        } else {
            ProfileForm(
                uiState = uiState,
                viewModel = viewModel,
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTopBar() {
    CenterAlignedTopAppBar(
        title = { Text("My Profile", fontSize = 17.sp) },
        navigationIcon = {
            IconButton(onClick = { }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back_arrow),
                    contentDescription = "Back Arrow",
                    modifier = Modifier.size(22.dp)
                )
            }
        },
        modifier = Modifier.shadow(3.dp)
    )
}

@Composable
private fun ProfileBottomBar(
    isFormComplete: Boolean,
    isLoading: Boolean,
    onSaveClicked: () -> Unit
) {
    Column {
        Button(
            onClick = onSaveClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            enabled = isFormComplete && !isLoading,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        ) {
            if (isLoading && isFormComplete) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(text = "Save Changes", fontWeight = FontWeight.Medium)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ProfileForm(
    uiState: ProfileContentUiState,
    viewModel: ProfileViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {
        ProfileNameSection(
            firstName = uiState.firstName,
            lastName = uiState.lastName,
            isLoading = uiState.isLoading,
            onFirstNameChange = viewModel::updateFirstName,
            onLastNameChange = viewModel::updateLastName
        )

        Spacer(modifier = Modifier.height(32.dp))

        AccountDetailsSection(
            dob = uiState.dob,
            nationality = uiState.nationality,
            gender = uiState.gender,
            isLoading = uiState.isLoading,
            onDobChange = viewModel::updateDob,
            onNationalityChange = viewModel::updateNationality,
            onGenderChange = viewModel::updateGender
        )

        Spacer(modifier = Modifier.height(200.dp))

        NavigationButtons(
            navController = navController,
            isLoading = uiState.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ProfileNameSection(
    firstName: String,
    lastName: String,
    isLoading: Boolean,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit
) {
    Text("Profile Name", fontWeight = FontWeight.Bold, fontSize = 16.sp)
    Text("This is displayed on your profile", color = Color.Gray, fontSize = 14.sp)

    Spacer(modifier = Modifier.height(10.dp))

    CustomTextField(
        value = firstName,
        onValueChange = onFirstNameChange,
        label = "First Name",
        enabled = !isLoading
    )

    Spacer(modifier = Modifier.height(24.dp))

    CustomTextField(
        value = lastName,
        onValueChange = onLastNameChange,
        label = "Last Name",
        enabled = !isLoading
    )
}

@Composable
private fun AccountDetailsSection(
    dob: String,
    nationality: String,
    gender: String,
    isLoading: Boolean,
    onDobChange: (String) -> Unit,
    onNationalityChange: (String) -> Unit,
    onGenderChange: (String) -> Unit
) {
    Text("Account details", fontWeight = FontWeight.Bold, fontSize = 16.sp)
    Text("This is not visible to other users", color = Color.Gray, fontSize = 14.sp)

    Spacer(modifier = Modifier.height(16.dp))

    DateOfBirthField(
        dob = dob,
        onDobChange = onDobChange,
        enabled = !isLoading
    )

    Spacer(modifier = Modifier.height(16.dp))

    NationalityField(
        nationality = nationality,
        onNationalityChange = onNationalityChange,
        enabled = !isLoading
    )

    Spacer(modifier = Modifier.height(16.dp))

    GenderSelection(
        selectedGender = gender,
        onGenderChange = onGenderChange,
        enabled = !isLoading
    )
}

@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 14.sp) },
        textStyle = TextStyle(fontSize = 16.sp),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        singleLine = true,
        enabled = enabled,
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
}

@Composable
private fun DateOfBirthField(
    dob: String,
    onDobChange: (String) -> Unit,
    enabled: Boolean
) {
    val context = LocalContext.current

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
        Text("Date of birth", fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            onDobChange("${selectedMonth + 1}/$selectedDayOfMonth/$selectedYear")
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
            .height(60.dp)
            .clickable(enabled = enabled) {
                datePickerDialog.show()
            },
        placeholder = { Text("MM/DD/YYYY") },
        label = { Text("Date of Birth", fontSize = 14.sp) },
        textStyle = TextStyle(fontSize = 16.sp),
        trailingIcon = {
            IconButton(
                onClick = { datePickerDialog.show() },
                enabled = enabled
            ) {
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
        enabled = enabled
    )
}

@Composable
private fun NationalityField(
    nationality: String,
    onNationalityChange: (String) -> Unit,
    enabled: Boolean
) {
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
        Text("Nationality", fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }

    NationalitySelector(
        selectedNationality = nationality,
        onNationalitySelected = onNationalityChange,
        enabled = enabled
    )
}

@Composable
private fun GenderSelection(
    selectedGender: String,
    onGenderChange: (String) -> Unit,
    enabled: Boolean
) {
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
        Text("Gender", fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }

    val genderOptions = listOf("Male", "Female", "Prefer not to say")

    genderOptions.chunked(2).forEach { rowItems ->
        Row(verticalAlignment = Alignment.CenterVertically) {
            rowItems.forEach { gender ->
                RadioButton(
                    selected = selectedGender == gender,
                    onClick = { onGenderChange(gender) },
                    enabled = enabled,
                    modifier = Modifier.scale(0.8f)
                )
                Text(
                    text = gender,
                    fontSize = 16.sp,
                    fontWeight = if (selectedGender == gender) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun NavigationButtons(
    navController: NavController,
    isLoading: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = {
                navController.navigate(Screen.CreateAdFlow.route)
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
}

// UI State data class to manage profile content state
data class ProfileContentUiState(
    val firstName: String,
    val lastName: String,
    val dob: String,
    val nationality: String,
    val gender: String,
    val isLoading: Boolean,
    val isFormComplete: Boolean
) {
    companion object {
        @Composable
        fun fromViewModel(viewModel: ProfileViewModel): ProfileContentUiState {
            val firstName by viewModel.firstName.collectAsState()
            val lastName by viewModel.lastName.collectAsState()
            val dob by viewModel.dob.collectAsState()
            val nationality by viewModel.nationality.collectAsState()
            val gender by viewModel.gender.collectAsState()
            val isLoading by viewModel.isLoading.collectAsState()
            val isFormComplete by viewModel.isFormComplete.collectAsState()

            return ProfileContentUiState(
                firstName = firstName,
                lastName = lastName,
                dob = dob,
                nationality = nationality,
                gender = gender,
                isLoading = isLoading,
                isFormComplete = isFormComplete
            )
        }
    }
}