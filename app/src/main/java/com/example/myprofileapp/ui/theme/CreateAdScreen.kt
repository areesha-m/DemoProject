package com.example.myprofileapp.ui.theme

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage // For displaying images
import com.example.myprofileapp.model.CreateAdViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAdScreen(
    viewModel: CreateAdViewModel = hiltViewModel(),
    onAdPostedSuccessfully: () -> Unit,
    navController: NavController, // Add this
) {
    val title by viewModel.title.collectAsState()
    val titleError by viewModel.titleError.collectAsState()

    val description by viewModel.description.collectAsState()
    val descriptionError by viewModel.descriptionError.collectAsState()

    val price by viewModel.price.collectAsState()
    val priceError by viewModel.priceError.collectAsState()

    val category by viewModel.category.collectAsState()
    val categories = viewModel.categories // Assuming this is a simple List<String> in VM
    val categoryError by viewModel.categoryError.collectAsState()
    var categoryExpanded by remember { mutableStateOf(false) }

    val condition by viewModel.condition.collectAsState()
    val conditions = viewModel.conditions // Assuming this is a simple List<String> in VM
    val conditionError by viewModel.conditionError.collectAsState()
    var conditionExpanded by remember { mutableStateOf(false) }

    val location by viewModel.location.collectAsState()
    val locationError by viewModel.locationError.collectAsState()

    val selectedImages by viewModel.imageUris.collectAsState()
    val imageUrisError by viewModel.imageUrisError.collectAsState()

    val isAdPosting by viewModel.isAdPosting.collectAsState()
    val adPostSuccess by viewModel.adPostSuccess.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Image Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri>? ->
        uris?.let {
            if (it.isNotEmpty()) {
                viewModel.onImagesSelected(it)
            }
        }
    }

    LaunchedEffect(adPostSuccess) {
        when (adPostSuccess) {
            true -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Ad posted successfully!",
                        duration = SnackbarDuration.Short
                    )
                }
                onAdPostedSuccessfully() // Navigate away or reset form
            }
            false -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Failed to post ad. Please try again.",
                        duration = SnackbarDuration.Long
                    )
                }
            }
            null -> { /* Initial state, do nothing */ }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Create New Ad") },
                navigationIcon = { // Added back button
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()) // Make the whole column scrollable
        ) {
            // Title TextField
            OutlinedTextField(
                value = title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Ad Title") },
                isError = titleError != null,
                supportingText = { titleError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isAdPosting
            )
            Spacer(Modifier.height(8.dp))

            // Description TextField
            OutlinedTextField(
                value = description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Description") },
                isError = descriptionError != null,
                supportingText = { descriptionError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth().height(120.dp), // Multi-line
                enabled = !isAdPosting
            )
            Spacer(Modifier.height(8.dp))

            // Price TextField
            OutlinedTextField(
                value = price,
                onValueChange = viewModel::onPriceChange,
                label = { Text("Price") },
                isError = priceError != null,
                supportingText = { priceError?.let { Text(it) } },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isAdPosting
            )
            Spacer(Modifier.height(8.dp))

            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {}, // Handled by DropdownMenuItem
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(), // Important for ExposedDropdownMenuBox
                    isError = categoryError != null,
                    enabled = !isAdPosting
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                viewModel.onCategoryChange(selectionOption)
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }
            categoryError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp)) }
            Spacer(Modifier.height(8.dp))

            // Condition Dropdown
            ExposedDropdownMenuBox(
                expanded = conditionExpanded,
                onExpandedChange = { conditionExpanded = !conditionExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = condition,
                    onValueChange = {}, // Handled by DropdownMenuItem
                    readOnly = true,
                    label = { Text("Condition") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = conditionExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    isError = conditionError != null,
                    enabled = !isAdPosting
                )
                ExposedDropdownMenu(
                    expanded = conditionExpanded,
                    onDismissRequest = { conditionExpanded = false }
                ) {
                    conditions.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                viewModel.onConditionChange(selectionOption)
                                conditionExpanded = false
                            }
                        )
                    }
                }
            }
            conditionError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp)) }
            Spacer(Modifier.height(8.dp))

            // Location TextField
            OutlinedTextField(
                value = location,
                onValueChange = viewModel::onLocationChange,
                label = { Text("Location") },
                isError = locationError != null,
                supportingText = { locationError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isAdPosting
            )
            Spacer(Modifier.height(16.dp))

            // Image Picker Button
            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                enabled = !isAdPosting
            ) {
                Text("Select Images") // User can select multiple based on GetMultipleContents
            }
            imageUrisError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            Spacer(Modifier.height(8.dp))

            // Display Selected Images
            if (selectedImages.isNotEmpty()) {
                Text("Selected Images:", style = MaterialTheme.typography.titleSmall)
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState()) // Make images scrollable horizontally
                        .padding(vertical = 8.dp)
                ) {
                    selectedImages.forEachIndexed { index, uri ->
                        Box(modifier = Modifier.padding(end = 8.dp)) {
                            AsyncImage(
                                model = uri,
                                contentDescription = "Selected Image $index",
                                modifier = Modifier.size(100.dp)
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))

            // Post Ad Button
            Button(
                onClick = { viewModel.postAd() },
                enabled = !isAdPosting, // ViewModel's postAd should handle validation internally
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                if (isAdPosting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Post Ad")
                }
            }
        }
    }
}
