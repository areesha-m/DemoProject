package com.example.myprofileapp.ui.theme.components.orders

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myprofileapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreenLayout(
    onFilterClick: () -> Unit,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    tabsContent: @Composable () -> Unit,
    orderListContent: @Composable (Modifier) -> Unit,
    isFilterDrawerOpen: Boolean,
    onCloseFilterDrawer: () -> Unit,
    selectedFilter: String?,
    onFilterSelected: (String) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSearching: Boolean,
) {
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
                            IconButton(onClick = onFilterClick) {
                                Icon(
                                    painter = painterResource(R.drawable.status),
                                    contentDescription = "Status filter",
                                    modifier = Modifier
                                )
                            }
                            Spacer(modifier = Modifier.width(1.dp))
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
                        .height(105.dp)
                )
            },
            containerColor = Color(0xFFF5F5F5)
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                tabsContent()
                orderListContent(Modifier.fillMaxSize())
            }

            RightSideDrawer(
                isOpen = isFilterDrawerOpen,
                onClose = onCloseFilterDrawer,
                topPadding = 88.dp
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Text("Filter Orders", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(start = 16.dp))

                Spacer(modifier = Modifier.height(16.dp))

                listOf("0", "1", "2", "3").forEach { status ->
                    TextButton(onClick = {
                        onFilterSelected(status)
                    }) {
                        when (status) {
                            "0" -> Text(text = "- All Orders",
                                color = Color.Black,
                                fontWeight = if (selectedFilter == status) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 16.sp
                            )
                            "1" -> Text(text = "- In Progress",
                                color = Color.Black,
                                fontWeight = if (selectedFilter == status) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 16.sp
                            )
                            "2" -> Text(text = "- Cancel/Return",
                                color = Color.Black,
                                fontWeight = if (selectedFilter == status) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 16.sp
                            )
                            "3" -> Text(text = "- Delivered",
                                color = Color.Black,
                                fontWeight = if (selectedFilter == status) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RightSideDrawer(
    isOpen: Boolean,
    topPadding: Dp = 0.dp,
    onClose: () -> Unit,
    drawerWidth: Dp = 250.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        if (isOpen) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable { onClose() }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(drawerWidth)
                .align(Alignment.CenterEnd)
                .padding(top = topPadding)

        ) {
            AnimatedVisibility(
                visible = isOpen,
                enter = slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ),
                exit = slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(300)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    content()
                }
            }
        }
    }
}