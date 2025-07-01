package com.example.myprofileapp.ui.theme.components.orders

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myprofileapp.R

data class OrderStatus(
    val type: String,
    val color: Color,
    val backgroundColor: Color

) {
    companion object {
        val delivered = OrderStatus(
            type = "Delivered",
            color = Color(0xFF02B21E),
            backgroundColor = Color(0xFFE9FFE6)
        )
        val processing = OrderStatus(
            type = "Processing",
            color = Color(0xFFE7400D),
            backgroundColor = Color(0xFFFFF1E1)
        )
        val cancelled = OrderStatus(
            type = "Cancelled/Returned",
            color = Color(0xFFD32F2F),
            backgroundColor = Color(0xFFFFEBEE)
        )
    }
}

data class OrderItem(
    val id: Int,
    val title: String,
    val price: String,
    val expectedDate: String,
    val orderNumber: String,
    val seller: String,
    val status: OrderStatus,
    val imageUrl : String,
    val quantity: Int
)

val allOrders = listOf(
    OrderItem(
        id = 1,
        title = "Samsung 74 4K Smart 3D Blu-ray 1330watts Wireless Bluetooth",
        price = "AED 3,100",
        expectedDate = "2024-12-20",
        orderNumber = "P23491",
        seller = "Electrohub",
        status = OrderStatus.delivered,
        imageUrl = "https://homeapplianceswarehouse.pk/wp-content/uploads/2024/10/32-Inch-china-Samsung.jpg",
        quantity = 1,

        ),
    OrderItem(
        id = 2,
        title = "Samsung 7.1 4K Smart 3D Blu-ray 1330watts Wireless Bluetooth",
        price = "AED 3,100",
        expectedDate = "2024-12-20",
        orderNumber = "P23492",
        seller = "Electrohub",
        status = OrderStatus.processing,
        imageUrl = "https://homeapplianceswarehouse.pk/wp-content/uploads/2024/10/32-Inch-china-Samsung.jpg",
        quantity = 1,

        ),
    OrderItem(
        id = 3,
        title = "Samsung 74 4K Smart 3D Blu-ray 1330watts Wireless Bluetooth",
        price = "AED 3,100",
        expectedDate = "2024-12-20",
        orderNumber = "P23493",
        seller = "Electrohub",
        status = OrderStatus.processing,
        imageUrl = "https://homeapplianceswarehouse.pk/wp-content/uploads/2024/10/32-Inch-china-Samsung.jpg",
        quantity = 1,

        ),
    OrderItem(
        id = 4,
        title = "Samsung 74 4K Smart 3D Blu-ray 1330watts Wireless Bluetooth",
        price = "AED 3,100",
        expectedDate = "2024-12-20",
        orderNumber = "P23494",
        seller = "TechStore",
        status = OrderStatus.delivered,
        imageUrl = "https://homeapplianceswarehouse.pk/wp-content/uploads/2024/10/32-Inch-china-Samsung.jpg" ,
        quantity = 1,

    ),
    OrderItem(
        id = 5,
        title = "Samsung 74 4K Smart 3D Blu-ray 1330watts Wireless Bluetooth",
        price = "AED 3,100",
        expectedDate = "2024-12-20",
        orderNumber = "P23493",
        seller = "Electrohub",
        status = OrderStatus.processing,
        imageUrl = "https://homeapplianceswarehouse.pk/wp-content/uploads/2024/10/32-Inch-china-Samsung.jpg",
        quantity = 1,
        ),
    OrderItem(
        id = 6,
        title = "Samsung 74 4K Smart 3D Blu-ray 1330watts Wireless Bluetooth",
        price = "AED 3,100",
        expectedDate = "2024-12-20",
        orderNumber = "P23493",
        seller = "TechStore",
        status = OrderStatus.cancelled,
        imageUrl = "https://homeapplianceswarehouse.pk/wp-content/uploads/2024/10/32-Inch-china-Samsung.jpg",
        quantity = 1,
        ),

    )

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrdersRoot() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "orders") {
        composable("orders") {
            OrdersScreen(navController = navController)
        }
        composable("orderDetails?ids={ids}") { backStackEntry ->
            val idsParam = backStackEntry.arguments?.getString("ids")
            val ids = idsParam?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList()
            OrderDetailsScreen(
                orderIds = ids,
                allOrders = allOrders,
                onBackClick = { navController.popBackStack() }
                )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(navController: NavController) {

    var selectedTab by remember { mutableIntStateOf(0) }
    val deliveredOrders = allOrders.filter { it.status == OrderStatus.delivered }
    val processingOrders = allOrders.filter { it.status == OrderStatus.processing }
    val cancelReturnOrders = allOrders.filter { it.status == OrderStatus.cancelled }

    var isFilterDrawerOpen by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf<String?>("0") }

    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val tabs = listOf(
        TabItem("All Orders", allOrders.size),
        TabItem("In Progress", processingOrders.size),
        TabItem("Cancel/Return", cancelReturnOrders.size),
        TabItem("Delivered", deliveredOrders.size)
    )

    val selectedOrders = remember(selectedTab, selectedFilter, searchQuery) {
        val filtered = when (selectedTab) {
            0 -> when (selectedFilter) {
                "0" -> allOrders
                "1" -> processingOrders
                "2" -> cancelReturnOrders
                "3" -> deliveredOrders
                else -> allOrders
            }
            1 -> processingOrders
            2 -> cancelReturnOrders
            3 -> deliveredOrders
            else -> allOrders
        }
        if (searchQuery.isNotBlank()) {
            filtered.filter { it.title.contains(searchQuery, ignoreCase = true) }
        } else filtered

    }

    val displayItems = remember(selectedOrders) {
        val result = mutableListOf<Any>()
        val addedOrderIds = mutableSetOf<Int>()
        for (order in selectedOrders) {
            if (addedOrderIds.contains(order.id)) {
                continue
            }

            val matchingOrders = selectedOrders.filter {
                it.seller == order.seller && it.status == order.status
            }

            if (matchingOrders.size > 1) {
                result.add(matchingOrders)
                addedOrderIds.addAll(matchingOrders.map { it.id })
            } else {
                result.add(order)
                addedOrderIds.add(order.id)
            }
        }
        result
    }

    OrdersScreenLayout(
        onFilterClick = { isFilterDrawerOpen = !isFilterDrawerOpen },
        onBackClick = { },
        onSearchClick = {
            isSearching = !isSearching
            if (!isSearching)
                searchQuery = ""
        },
        tabsContent = {
            OrderTabs(
                tabs = tabs,
                selectedTabIndex = selectedTab,
                onTabSelected = { index -> selectedTab = index }
            )
        },
        orderListContent = { modifier ->
            OrderListContent(
                displayItems = displayItems,
                selectedTab = selectedTab,
                modifier = modifier,
                navController = navController
            )
        },
        isFilterDrawerOpen = isFilterDrawerOpen,
        onCloseFilterDrawer = {
            isFilterDrawerOpen = false
            selectedFilter = "0"
        },
        selectedFilter = selectedFilter,
        onFilterSelected = { status ->
            selectedFilter = status
            isFilterDrawerOpen = false
            selectedTab = when (status) {
                "0" -> 0 // All Orders
                "1" -> 1 // In Progress
                "2" -> 2 // Cancel/Return
                "3" -> 3 // Delivered
                else -> 0
            }
        },
        searchQuery = searchQuery,
        onSearchQueryChange = { searchQuery = it },
        isSearching = isSearching,
    )
}

@Composable
fun SingleOrderCard(order: OrderItem, navController: NavController) {
    Card(
        onClick = {
            navController.navigate("orderDetails?ids=${order.id}")
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(2.dp, Color(0xFFEEEEEE)),

    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OrderContent(order = order)
        }
    }
}

@Composable
fun GroupedOrderCard(orders: List<*>, navController: NavController) {
    Card(
        onClick = {
            val ids = orders.filterIsInstance<OrderItem>().joinToString(",") { it.id.toString() }
            navController.navigate("orderDetails?ids=$ids")
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(2.dp, Color(0xFFEEEEEE))
    ) {
        Column(    modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 2.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF0F8FF))
                    .padding(horizontal = 18.dp, vertical = 10.dp)

            ) {
                Box(contentAlignment = Alignment.CenterStart) {
                    Text(
                        "${orders.size} Items from the Seller",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF000000),
                        textAlign = TextAlign.Start
                    )
                }
            }
            orders.forEachIndexed { index, order ->
                Column(modifier = Modifier.padding(14.dp)) {
                    OrderContent(order = order as OrderItem)
                }
            }
        }
    }
}

@Composable
fun OrderContent(order: OrderItem) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .background(
                        order.status.backgroundColor,
                        RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 9.dp)
            ) {
                Text(
                    order.status.type,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = order.status.color
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    order.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF111827),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    order.price,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111827)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    "Expected date of delivery • ${order.expectedDate}",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280),
                    lineHeight = 20.sp
                )

                Text(
                    "Order #${order.orderNumber} • ${order.expectedDate}",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280),
                    lineHeight = 20.sp
                )

                Text(
                    "Seller: ${order.seller}",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280),
                    lineHeight = 20.sp
                )
            }
            AsyncImage(
                model = order.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF3F4F6)),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.placeholder_image),
                placeholder = painterResource(R.drawable.placeholder_image)
            )
        }
    }

    Spacer(modifier = Modifier.height(5.dp))

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF0F8FF),
            ),
            shape = RoundedCornerShape(10.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.chat),
                    contentDescription = "Chat Icon",
                    modifier = Modifier.size(17.dp),
                    tint = Color(0xFF2196F3)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Chat",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF000000)
                )
            }
        }

        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF5F4F4),
            ),
            shape = RoundedCornerShape(10.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.details),
                    contentDescription = "Details Icon",
                    modifier = Modifier.size(17.dp),
                    tint = Color(0xFF000000)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Details",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF000000)
                )
            }
        }

        if (order.status.type == "Delivered") {

            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFFFFF),
                ),
                border = BorderStroke(0.75.dp, Color(0xFFC3CBDA)),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.review),
                        contentDescription = "Review Icon",
                        modifier = Modifier.size(17.dp),
                        tint = Color(0xFF000000)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Review",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF000000)
                    )
                }
            }
        }
    }
}