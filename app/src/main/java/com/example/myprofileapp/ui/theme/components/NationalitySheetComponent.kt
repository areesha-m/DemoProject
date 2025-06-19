package com.example.myprofileapp.ui.theme.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myprofileapp.model.CountryViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NationalitySelector(
    modifier: Modifier = Modifier,
    selectedNationality: String,
    onNationalitySelected: (String) -> Unit,
    enabled: Boolean = true,
) {
    val countryViewModel: CountryViewModel = hiltViewModel()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isSheetOpen by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(selectedNationality) {
        countryViewModel.updateSelectedCountry(selectedNationality)
        Log.d("NationalitySelector", "Updated selected country to: $selectedNationality")
    }

    LaunchedEffect(isSheetOpen) {
        if (isSheetOpen) {
            coroutineScope.launch {
                sheetState.show()
            }
        }
    }

    if (isSheetOpen) {
        ModalBottomSheet(
            dragHandle = null,
            sheetState = sheetState,
            onDismissRequest = {
                coroutineScope.launch {
                    sheetState.hide()
                }.invokeOnCompletion {
                    isSheetOpen = false
                }
            },
            shape = RoundedCornerShape(
                topStart = 10.dp,
                topEnd = 10.dp,
            )
        ) {
            NationalitySheetContent(
                onCountrySelected = { selected ->
                    onNationalitySelected(selected)
                    coroutineScope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        isSheetOpen = false
                    }
                },
                viewModel = countryViewModel
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                isSheetOpen = true
            }
    ) {
        OutlinedTextField(
            value = selectedNationality,
            onValueChange = {},
            label = { Text("Nationality", fontSize = 15.sp) },
            readOnly = true,
            enabled = false,
            textStyle = TextStyle(fontSize = 16.sp),
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.LightGray,
                unfocusedIndicatorColor = Color.LightGray,
                disabledIndicatorColor = Color.LightGray,
                cursorColor = Color.Black,
                disabledTextColor = Color.Black,
                disabledLabelColor = Color.Gray
            )
        )
    }
}

@Composable
private fun NationalitySheetContent(
    onCountrySelected: (String) -> Unit,
    viewModel: CountryViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    val countryNamesFromViewModel by viewModel.countryNames.collectAsState()
    val filteredCountries = remember(searchQuery, countryNamesFromViewModel) {
        if (searchQuery.isBlank()) {
            countryNamesFromViewModel
        } else {
            countryNamesFromViewModel.filter { it.contains(searchQuery, ignoreCase = true) }
        }
    }
    val loadCountries by viewModel._loadCountries.collectAsState()
    val isError by viewModel.isError.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Spacer(Modifier.height(13.dp))

        SearchField(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it }
        )

        Spacer(Modifier.height(14.dp))

        when {
            loadCountries -> {
                LoadingState()
            }
            isError -> {
                ErrorState(
                    errorMessage = errorMessage,
                    onRetry = {
                        viewModel.clearError()
                        viewModel.fetchCountries()
                    }
                )
            }
            else -> {
                CountryList(
                    countries = filteredCountries,
                    onCountrySelected = onCountrySelected
                )
            }
        }
    }
}

@Composable
private fun SearchField(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        placeholder = { Text("Search Country") },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
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
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(8.dp))
            Text("Loading countries...", color = Color.Gray)
        }
    }
}

@Composable
private fun ErrorState(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "⚠️",
            fontSize = 40.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Oops! Something went wrong",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = errorMessage.ifBlank { "Failed to load countries" },
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            )
        ) {
            Text("Try Again", color = Color.White)
        }
    }
}

@Composable
private fun CountryList(
    countries: List<String>,
    onCountrySelected: (String) -> Unit
) {
    LazyColumn(modifier = Modifier.heightIn(max = 750.dp)) {
        items(items = countries, key = { it }) { country ->
            Text(
                text = country,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCountrySelected(country) }
                    .padding(vertical = 17.dp)
            )
            Divider()
        }
    }
}