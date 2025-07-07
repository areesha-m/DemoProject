package com.example.myprofileapp.ui.theme.components.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun OrderListContent(
    displayItems: List<Any>,
    selectedTab: Int,
    modifier: Modifier = Modifier,
    navController: NavController,
    lazyListState: LazyListState
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp,
            bottom = if (selectedTab == 0) 90.dp else 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        state = lazyListState
    ) {
        items(displayItems) { item ->
            when (item) {
                is OrderItem -> SingleOrderCard(order = item, navController = navController)
                is List<*> -> GroupedOrderCard(orders = item, navController = navController)
            }
        }
    }
}