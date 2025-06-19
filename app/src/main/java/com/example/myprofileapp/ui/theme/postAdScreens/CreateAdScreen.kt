package com.example.myprofileapp.ui.theme.postAdScreens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myprofileapp.model.CreateAdViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAdScreen(
    navController: NavController,
    viewModel: CreateAdViewModel, // Note: No longer has default hiltViewModel(), it's passed from the graph
    onAdPostedSuccessfully: () -> Unit
) {
    val title by viewModel.title.collectAsState()
    val description by viewModel.description.collectAsState()
    val price by viewModel.price.collectAsState()
    val condition by viewModel.condition.collectAsState()
    val conditions = viewModel.conditions
    var conditionExpanded by remember { mutableStateOf(false) }
    val location by viewModel.location.collectAsState()
    val selectedImages by viewModel.imageUris.collectAsState()

    // Values from previous steps
    val selectedCity by viewModel.city.collectAsState()
    val selectedCategory by viewModel.category.collectAsState()

    val isAdPosting by viewModel.isAdPosting.collectAsState()
    val adPostSuccess by viewModel.adPostSuccess.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri>? ->
        uris?.let { if (it.isNotEmpty()) viewModel.onImagesSelected(it) }
    }

    LaunchedEffect(adPostSuccess) {
        if (adPostSuccess == true) {
            onAdPostedSuccessfully()
        } else if (adPostSuccess == false) {
            scope.launch { snackbarHostState.showSnackbar("Failed to post ad. Please check all fields.") }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Enter Ad Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                .verticalScroll(rememberScrollState())
        ) {
            // Display selected City and Category
            Row(modifier = Modifier.padding(bottom = 16.dp)) {
                Text(text = "Posting in: ", fontWeight = FontWeight.SemiBold)
                Text(text = "$selectedCity > $selectedCategory", color = MaterialTheme.colorScheme.primary)
            }

            OutlinedTextField(
                value = title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Ad Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isAdPosting
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                enabled = !isAdPosting
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = price,
                onValueChange = viewModel::onPriceChange,
                label = { Text("Price (AED)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isAdPosting
            )
            Spacer(Modifier.height(8.dp))

            // Condition Dropdown
            ExposedDropdownMenuBox(
                expanded = conditionExpanded,
                onExpandedChange = { conditionExpanded = !it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = condition,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Condition") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = conditionExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    enabled = !isAdPosting
                )
                ExposedDropdownMenu(expanded = conditionExpanded, onDismissRequest = { conditionExpanded = false }) {
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
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = location,
                onValueChange = viewModel::onLocationChange,
                label = { Text("Location Details (e.g. building, street)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isAdPosting
            )
            Spacer(Modifier.height(16.dp))

            Button(onClick = { imagePickerLauncher.launch("image/*") }, enabled = !isAdPosting) {
                Text("Select Images")
            }
            Spacer(Modifier.height(8.dp))

            if (selectedImages.isNotEmpty()) {
                Row(modifier = Modifier.horizontalScroll(rememberScrollState()).padding(vertical = 8.dp)) {
                    selectedImages.forEachIndexed { index, uri ->
                        Box(modifier = Modifier.padding(end = 8.dp)) {
                            AsyncImage(model = uri, contentDescription = "Selected Image $index", modifier = Modifier.size(100.dp))
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { viewModel.postAd() },
                enabled = !isAdPosting,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                if (isAdPosting) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Post Ad")
                }
            }
        }
    }
}
