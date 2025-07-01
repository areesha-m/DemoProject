package com.example.myprofileapp.ui.theme.components.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset

data class TabItem(
    val name: String,
    val count: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTabs(
    tabs: List<TabItem>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = Color(0xFFF5F5F5),
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = Color.Red,
                height = 2.dp
            )
        },
        edgePadding = 8.dp,
        modifier = Modifier.shadow(4.dp, ambientColor = Color.Gray)
    ) {
        tabs.forEachIndexed { index, tab ->
            val selected = selectedTabIndex == index
            Tab(
                selected = selected,
                onClick = { onTabSelected(index) },
                selectedContentColor = Color.Black,
                unselectedContentColor = Color(0xFF6B7280),
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                    .height(45.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        tab.name,
                        fontSize = 14.5.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = if (selected) Color.Black else Color(0xFF6B7280)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(Color(0xFFE7E8EC), RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${tab.count}",
                            fontSize = 13.sp,
                            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (selected) Color.Black else Color(0xFF6B7280),
                            modifier = Modifier.offset(y = (-4).dp)
                        )
                    }
                }
            }
        }
    }
}
