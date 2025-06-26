package com.example.myprofileapp.ui.theme.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myprofileapp.R

data class OrderItem(
    val id: Int,
    val title: String,
    val price: String,
    val expectedDate: String,
    val orderNumber: String,
    val seller: String,
    val status: String,
    val statusColor: Color,
    val statusBackgroundColor: Color,
    val imageUrl : String
)

data class TabItem(
    val name: String,
    val count: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen() {

    val allOrders = listOf(
        OrderItem(
            id = 1,
            title = "Samsung 74 4K Smart 3D Blu-ray 1330watts Wireless Bluetooth",
            price = "AED 3,100",
            expectedDate = "2024-12-20",
            orderNumber = "P23491",
            seller = "Electrohub",
            status = "Delivered",
            statusColor = Color(0xFF02B21E),
            statusBackgroundColor = Color(0xFFE9FFE6),
            imageUrl = "https://homeapplianceswarehouse.pk/wp-content/uploads/2024/10/32-Inch-china-Samsung.jpg"
        ),
        OrderItem(
            id = 2,
            title = "Samsung 7.1 4K Smart 3D Blu-ray 1330watts Wireless Bluetooth",
            price = "AED 3,100",
            expectedDate = "2024-12-20",
            orderNumber = "P23492",
            seller = "Electrohub",
            status = "Processing",
            statusColor = Color(0xFFE7400D),
            statusBackgroundColor = Color(0xFFFFF1E1),
            imageUrl = "https://homeapplianceswarehouse.pk/wp-content/uploads/2024/10/32-Inch-china-Samsung.jpg"

        ),
        OrderItem(
            id = 3,
            title = "Samsung 74 4K Smart 3D Blu-ray 1330watts Wireless Bluetooth",
            price = "AED 3,100",
            expectedDate = "2024-12-20",
            orderNumber = "P23493",
            seller = "Electrohub",
            status = "Processing",
            statusColor = Color(0xFFE7400D),
            statusBackgroundColor = Color(0xFFFFF1E1),
            imageUrl = "https://homeapplianceswarehouse.pk/wp-content/uploads/2024/10/32-Inch-china-Samsung.jpg"

        ),
        OrderItem(
            id = 4,
            title = "Samsung 74 4K Smart 3D Blu-ray 1330watts Wireless Bluetooth",
            price = "AED 3,100",
            expectedDate = "2024-12-20",
            orderNumber = "P23494",
            seller = "TechStore",
            status = "Delivered",
            statusColor = Color(0xFF02B21E),
            statusBackgroundColor = Color(0xFFE9FFE6),
            imageUrl = "https://homeapplianceswarehouse.pk/wp-content/uploads/2024/10/32-Inch-china-Samsung.jpg"

        ),
        OrderItem(
            id = 5,
            title = "Samsung 74 4K Smart 3D Blu-ray 1330watts Wireless Bluetooth",
            price = "AED 3,100",
            expectedDate = "2024-12-20",
            orderNumber = "P23493",
            seller = "Electrohub",
            status = "Processing",
            statusColor = Color(0xFFE7400D),
            statusBackgroundColor = Color(0xFFFFF1E1),
            imageUrl = "https://homeapplianceswarehouse.pk/wp-content/uploads/2024/10/32-Inch-china-Samsung.jpg"

        ),

    )
    var selectedTab by remember { mutableIntStateOf(0) }
    val deliveredOrders = allOrders.filter { it.status == "Delivered" }
    val processingOrders = allOrders.filter { it.status == "Processing" }
    val cancelReturnOrders = allOrders.filter { it.status == "Cancelled" || it.status == "Returned" }

    var isStatusButtonVisible by remember { mutableStateOf(true) }
    var lastScrollOffset by remember { mutableIntStateOf(0) }
    val lazyListState = rememberLazyListState()


    val tabs = listOf(
        TabItem("All Orders", allOrders.size),
        TabItem("In Progress", processingOrders.size),
        TabItem("Cancel/Return", cancelReturnOrders.size),
        TabItem("Delivered", deliveredOrders.size)
        )

    val selectedOrders = when (selectedTab) {
        0 -> allOrders
        1 -> processingOrders
        2 -> cancelReturnOrders
        3 -> deliveredOrders
        else -> allOrders
    }

    LaunchedEffect(lazyListState) {
        var lastScrollPosition = 0

        snapshotFlow {
            lazyListState.firstVisibleItemIndex to lazyListState.firstVisibleItemScrollOffset
        }.collect { (index, offset) ->
            val currentScrollPosition = index * 1000 + offset

            if (currentScrollPosition > lastScrollPosition + 30) {
                isStatusButtonVisible = false
            } else if (currentScrollPosition < lastScrollPosition - 30) {
                isStatusButtonVisible = true
            }

            lastScrollPosition = currentScrollPosition
        }
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


    Scaffold(
        topBar = {
            OrdersTopBar()
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                AnimatedVisibility(
                    visible = isStatusButtonVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(400),
                    ) + fadeIn(animationSpec = tween(400)),
                    exit = slideOutVertically(
                        animationSpec = tween(400),
                        targetOffsetY = { it }
                    ) + fadeOut(animationSpec = tween(400))
                ) {
                    FloatingActionButton(
                        onClick = { },
                        modifier = Modifier
                            .size(width = 100.dp, height = 50.dp).border(
                                width = 1.dp,
                                color = Color(0xFFE7E6E6),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        shape = RoundedCornerShape(8.dp),
                        containerColor = Color.White,
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 0.dp,
                            focusedElevation = 0.dp,
                            hoveredElevation = 0.dp
                        )

                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.status),
                                contentDescription = "Status Icon",
                                modifier = Modifier.size(18.dp),
                                tint = Color(0xFF000000)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Status",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF000000)
                            )
                        }
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFFF5F5F5),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color.Red,
                        height = 2.dp
                    )
                },
                edgePadding = 8.dp,
                modifier = Modifier.shadow(4.dp, ambientColor = Color.Gray)
            ) {
                tabs.forEachIndexed { index, tab ->
                    val selected = selectedTab == index
                    Tab(
                        selected = selected,
                        onClick = { selectedTab = index },
                        selectedContentColor = Color.Black,
                        unselectedContentColor = Color(0xFF6B7280),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
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

            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = if (selectedTab == 0) 110.dp else 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(displayItems) { item ->
                    when (item) {
                        is OrderItem -> SingleOrderCard(order = item)
                        is List<*> -> GroupedOrderCard(orders = item)
                    }
                }
            }
        }
    }
}

@Composable
fun SingleOrderCard(order: OrderItem) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(2.dp, Color(0xFFEEEEEE))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OrderContent(order = order)
        }
    }
}

@Composable
fun GroupedOrderCard(orders: List<*>) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp, vertical = 4.dp),
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
                        order.statusBackgroundColor,
                        RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 9.dp)
            ) {
                Text(
                    order.status,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = order.statusColor
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

        if (order.status == "Delivered") {

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersTopBar() {
    CenterAlignedTopAppBar(
        title = { Text("Orders", fontSize = 17.sp) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White
        ),
        navigationIcon = {
            IconButton(onClick = { }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back_arrow),
                    contentDescription = "Back Arrow",
                    modifier = Modifier.size(28.dp).padding(6.dp)
                )
            }
        },
        actions = {
            IconButton(onClick = { }) {
                Icon(painter = painterResource(R.drawable.status), contentDescription = "Status filter", modifier = Modifier.size(32.dp).padding(bottom = 6.dp))
            }
            Spacer(modifier = Modifier.width(1.dp))
            IconButton(onClick = { }) {
                Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(32.dp).padding(bottom = 6.dp))
            }
        },
        modifier = Modifier.shadow(4.dp).height(88.dp)
    )
}

