package com.example.myprofileapp.ui.theme.postAdScreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.myprofileapp.R
import com.example.myprofileapp.model.CreateAdViewModel
import com.example.myprofileapp.navigation.Screen
import androidx.compose.ui.draw.shadow
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextAlign

data class AdCategory(
    val name: String,
    val icon: Painter
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCategoryScreen(
    navController: NavController,
    viewModel: CreateAdViewModel = hiltViewModel()
) {
    val categories = listOf(
        AdCategory("Motors", painterResource(id = R.drawable.motors)),
        AdCategory("Jobs", painterResource(id = R.drawable.jobs)),
        AdCategory("Property for Sale", painterResource(id = R.drawable.property_for_sale)),
        AdCategory("Property for Rent", painterResource(id = R.drawable.property_for_rent)),
        AdCategory("Community", painterResource(id = R.drawable.community)),
        AdCategory("Classifieds", painterResource(id = R.drawable.classifieds))
    )
    var isJobCardExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {  },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Outlined.KeyboardArrowLeft, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Hello, what are you listing today?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Select the area that best suits your ad.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(32.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(categories) { category ->
                    if (category.name == "Jobs") {
                    JobCard(
                        expanded = isJobCardExpanded,
                        onCardClick = { isJobCardExpanded = !isJobCardExpanded },
                        onOptionClick = { option ->
                            viewModel.onCategorySelected("Jobs - $option")
                            navController.navigate(Screen.CreateAd.route)
                        }
                    )
                    } else {
                    CategoryCard(category = category, onClick = {
                        viewModel.onCategorySelected(category.name)
                        navController.navigate(Screen.CreateAd.route)
                    })
                    }
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryCard(category: AdCategory, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1.4f)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color(0x33330000), // light translucent black
                spotColor = Color(0x33000000),
                clip = false
            )
    ) {
        Card(
            onClick = onClick,
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // avoid double shadows
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = category.icon,
                    contentDescription = category.name,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.secondary // Use icon's original color (red)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun JobCard(
    expanded: Boolean,
    onCardClick: () -> Unit,
    onOptionClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1.4f)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color(0x33330000), // light translucent black
                spotColor = Color(0x33000000),
                clip = false
            )
    ) {
    Card(
        onClick = onCardClick,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // avoid double shadows
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .animateContentSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!expanded) {
                Icon(
                    painter = painterResource(id = R.drawable.jobs),
                    contentDescription = "Jobs",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.height(15.dp))
                Text(
                    text = "Jobs",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "I'm hiring",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOptionClick("I'm hiring") }
                            .padding(vertical = 12.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                    HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                    Text(
                        text = "I want a job",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOptionClick("I want a job") }
                            .padding(vertical = 12.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
    }
}

@Composable
fun JobOption(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 3.dp, horizontal = 3.dp),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Medium
    )
}
