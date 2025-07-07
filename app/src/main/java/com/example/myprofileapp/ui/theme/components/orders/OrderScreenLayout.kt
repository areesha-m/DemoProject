package com.example.myprofileapp.ui.theme.components.orders

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myprofileapp.R
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FabPosition
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreenLayout(
    onFilterClick: () -> Unit,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    tabsContent: @Composable () -> Unit,
    orderListContent: @Composable (Modifier) -> Unit,
    isFilterSheetOpen: Boolean,
    onCloseFilterSheet: () -> Unit,
    selectedFilter: String?,
    onFilterSelected: (String) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSearching: Boolean,
    showFilterFab: Boolean,
    onFabClick: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        if (isSearching) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                IconButton (onClick = {}){
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = "Search",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                TextField(
                                    value = searchQuery,
                                    onValueChange = onSearchQueryChange,
                                    placeholder = {
                                        Text(
                                            text = "Search orders...",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Normal
                                        )
                                    },
                                    textStyle = LocalTextStyle.current.copy(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Normal
                                    ),
                                    singleLine = true,
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        disabledIndicatorColor = Color.Transparent,
                                        cursorColor = Color.Red
                                    ),
                                    modifier = Modifier.weight(1f)
                                )

                                IconButton(
                                    onClick = {
                                        onSearchClick()
                                        onSearchQueryChange("")
                                }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.cross),
                                        contentDescription = "Clear Search",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        } else {
                            Text("Orders", fontSize = 17.sp, modifier = Modifier.padding(vertical = 6.dp))
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    ),
                    navigationIcon = {
                        if (!isSearching)
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_back_arrow),
                                    contentDescription = "Back Arrow",
                                )
                            }
                    },
                    actions = {
                        if (!isSearching) {
                            IconButton(onClick = { onSearchClick() }) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search",
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .shadow(4.dp)
                        //.height(105.dp)
                )
            },
            containerColor = Color(0xFFF5F5F5),
            floatingActionButton = {
                AnimatedVisibility(
                    visible = showFilterFab,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
                    modifier = Modifier.padding(bottom = 4.dp, end = 4.dp)
                ) {
                    ExtendedFloatingActionButton(
                        onClick = onFabClick,
                        icon = { Icon(
                            painter = painterResource(R.drawable.status),
                            contentDescription = "Status filter",
                            modifier = Modifier
                        ) },
                        text = { Text("Status", fontSize = 16.sp) },
                        shape = RoundedCornerShape(10.dp),
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),
                        modifier = Modifier.border(
                            width = 1.dp,
                            color = Color(0xFFC0C0C0),
                            shape = RoundedCornerShape(10.dp)
                        )
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.Center
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                tabsContent()
                orderListContent(Modifier.fillMaxSize())
            }
            if (isFilterSheetOpen) {

                ModalBottomSheet(
                    onDismissRequest = { onCloseFilterSheet() },
                    sheetState = sheetState,
                    dragHandle = null,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    containerColor = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            "Filter Orders",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 24.sp,
                            modifier = Modifier.fillMaxWidth().padding(start = 12.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        listOf("0", "1", "2", "3").forEachIndexed { index, status ->
                            Column(modifier = Modifier.fillMaxWidth()) {
                                TextButton(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        onFilterSelected(status)
                                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                                            if (!sheetState.isVisible) {
                                                onCloseFilterSheet()
                                            }
                                        }
                                    }
                                ) {
                                    Text(
                                        text = when (status) {
                                            "0" -> "All Orders"
                                            "1" -> "In Progress"
                                            "2" -> "Cancel/Return"
                                            "3" -> "Delivered"
                                            else -> ""
                                        },
                                        color = Color.Black,
                                        fontWeight = if (selectedFilter == status) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 16.sp,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Start
                                    )
                                }
                                if (index < 3) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 5.dp),
                                        thickness = 1.dp,
                                        color = Color.LightGray
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

