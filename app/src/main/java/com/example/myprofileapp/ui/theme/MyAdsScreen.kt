package com.example.myprofileapp.ui.theme // Or com.example.myprofileapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email // Placeholder for Chat icon
import androidx.compose.material.icons.filled.FavoriteBorder // Placeholder for Favorite icon
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share // Placeholder for Share icon
import androidx.compose.material.icons.filled.CheckCircle // Placeholder for Verified icon
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myprofileapp.data.AdListing
import com.example.myprofileapp.model.MyAdsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAdsScreen(
    navController: NavController, // Assuming NavController is passed for back navigation
    viewModel: MyAdsViewModel = hiltViewModel(),

    // onNavigateToAdDetails: (listingId: Int) -> Unit,
    // onNavigateToEditAd: (listingId: Int) -> Unit
) {
    val myAds by viewModel.myAds.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is MyAdsViewModel.MyAdsEvent.AdDeleted -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = event.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
                is MyAdsViewModel.MyAdsEvent.Error -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = event.message,
                            duration = SnackbarDuration.Long,
                            withDismissAction = true
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Ads (${myAds.size})") }, // Display count of ads
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Placeholder for potential actions like "Filter" or "Sort" if relevant for "My Ads"
                    //     Icon(Icons.Filled.FilterList, contentDescription = "Filter")
                    // }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            if (isLoading && myAds.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (myAds.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("You haven't posted any ads yet.")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.onRefreshTriggered() }) {
                            Text("Refresh")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { navController.navigate(com.example.myprofileapp.navigation.Screen.CreateAd.route) }) {
                            Text("Post Your First Ad")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp), // Reduced horizontal padding
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(myAds, key = { ad -> ad.listingId }) { ad ->
                        DetailedAdListItem(
                            ad = ad,
                            onAdClicked = {
                                // onNavigateToAdDetails(ad.listingId)
                            },
                            onDeleteClicked = {
                                viewModel.deleteAd(ad)
                            },
                            onEditClicked = {
                                // onNavigateToEditAd(ad.listingId)
                            },
                            onChatClicked = {
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedAdListItem(
    ad: AdListing,
    onAdClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onEditClicked: () -> Unit, // For future "Edit Ad" functionality
    onChatClicked: () -> Unit  // For the "Chat" button
) {
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    if (showDeleteConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmationDialog = false },
            title = { Text("Delete Ad") },
            text = { Text("Are you sure you want to delete '${ad.title}'? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteClicked()
                        showDeleteConfirmationDialog = false
                    }
                ) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmationDialog = false }) { Text("Cancel") }
            }
        )
    }

    Card(
        onClick = onAdClicked,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp), // Slightly rounded corners
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(200.dp) // Fixed height for the image area
            ) {
                val imageUrisList = remember(ad.imageUris) { ad.imageUris.split(",").filter { it.isNotBlank() } }
                val firstImageUri = imageUrisList.firstOrNull()

                if (firstImageUri != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(firstImageUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = ad.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop // Crop to fill the bounds
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No Image", style = MaterialTheme.typography.titleSmall)
                    }
                }

                // "VERIFIED USER" Badge (Top-Left)
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = "Verified User",
                        tint = Color.White,
                        modifier = Modifier.size(14.sp.value.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "VERIFIED USER", // Placeholder - needs dynamic data
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Image Count (Bottom-Left of Image)
                if (imageUrisList.isNotEmpty()) {
                    Text(
                        text = "1/${imageUrisList.size}", // Assuming we always show the first image
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Share and Favorite Icons (Top-Right of Image)
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    IconButton(onClick = {  }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Filled.Share, contentDescription = "Share Ad", tint = Color.White)
                    }
                    IconButton(onClick = {  }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Filled.FavoriteBorder, contentDescription = "Favorite Ad", tint = Color.White)
                    }
                }
            }

            // Ad Details Section
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "AED ${String.format("%.0f", ad.price)}", // Format price
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = ad.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = ad.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoChip("Age: ${ad.condition}") // Assuming condition maps to age for now
                    InfoChip("Usage: ${ad.status}") // Assuming status maps to usage for now
                    // You might need separate fields in AdListing for "Age" and "Usage"
                }
                Spacer(Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = ad.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false) // Prevents pushing date text
                    )
                    Text(
                        text = formatPostedDate(ad.postedDate),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(12.dp)) // Space before buttons

                // Action Buttons (Chat, Edit, Delete)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround // Or Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onChatClicked,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Email, contentDescription = "Chat", modifier = Modifier.size(ButtonDefaults.IconSize))
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text("Chat")
                    }
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = onEditClicked) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Edit or More Options") // Using MoreVert for general options which can include edit
                    }
                    IconButton(onClick = { showDeleteConfirmationDialog = true }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete Ad", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun InfoChip(text: String) {
    Text(
        text = text,
        fontSize = 10.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

fun formatPostedDate(timestamp: Long): String {
    val currentTime = System.currentTimeMillis()
    val diff = currentTime - timestamp

    val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days = TimeUnit.MILLISECONDS.toDays(diff)

    return when {
        seconds < 60 -> "Posted just now"
        minutes < 60 -> "Posted $minutes minutes ago"
        hours < 24 -> "Posted $hours hours ago"
        days < 7 -> "Posted $days days ago"
        else -> SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(timestamp))
    }
}
