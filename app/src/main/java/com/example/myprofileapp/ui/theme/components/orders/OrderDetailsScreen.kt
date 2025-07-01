package com.example.myprofileapp.ui.theme.components.orders

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myprofileapp.R
import com.example.myprofileapp.utils.DateFormatterUtil


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(orderIds: List<Int>, allOrders: List<OrderItem>, onBackClick: () -> Unit) {
    val orders = allOrders.filter { it.id in orderIds }
    val firstOrder = orders.firstOrNull()
    val dateFormatterUtil = remember { DateFormatterUtil() }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Order Details", fontSize = 17.sp, fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    ),
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_back_arrow),
                                contentDescription = "Back Arrow",
                                modifier = Modifier
                                    .size(28.dp)
                                    .padding(6.dp)
                            )
                        }
                    },
                    modifier = Modifier
                        .shadow(4.dp)
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(vertical = 24.dp, horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Order Details",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Order No. ${firstOrder?.orderNumber}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color(0xFF6B7280)
                                )
                                Text(
                                    "Order No. ${firstOrder?.orderNumber}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color(0xFF6B7280)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Order Date",
                                    fontSize = 14.sp,
                                    color = Color(0xFF6B7280)
                                )
                                Text(
                                    text = dateFormatterUtil.format(
                                        firstOrder?.expectedDate ?: "N/A"
                                    ),
                                    fontSize = 14.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                        ),
                        border = BorderStroke(1.25.dp, Color(0xFF000000)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        Text(
                            text = "Download Invoice",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (orders.size == 1) {
                            Text(
                                text = "Item Details",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            )
                        } else {
                            Text(
                                text = "Item Details • ${orders.size}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            )
                        }
                    }
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Seller:  ",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF6B7280)
                        )
                        Text(
                            text = "${firstOrder?.seller}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF6B7280)
                        )
                    }
                }

                items(orders) { order ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 0.dp, vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
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

                            // Product Info
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        order.title,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Black,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        lineHeight = 18.sp
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        order.price,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.Black
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        "Quantity: ${order.quantity}",
                                        fontSize = 12.sp,
                                        color = Color(0xFF6B7280),
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        "Eligible for returns till Feb 20",
                                        fontSize = 12.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                }

                                AsyncImage(
                                    model = order.imageUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF9FAFB)),
                                    contentScale = ContentScale.Crop,
                                    error = painterResource(R.drawable.placeholder_image),
                                    placeholder = painterResource(R.drawable.placeholder_image)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFEFF6FF),
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(
                                        horizontal = 16.dp,
                                        vertical = 8.dp
                                    )
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.chat),
                                            contentDescription = "Chat Icon",
                                            modifier = Modifier.size(16.dp),
                                            tint = Color(0xFF3B82F6)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Chat",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.Black
                                        )
                                    }
                                }

                                Button(
                                    onClick = { },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                    ),
                                    border = BorderStroke(1.dp, Color(0xFFD1D5DB)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(
                                        horizontal = 16.dp,
                                        vertical = 8.dp
                                    )
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.resource_return),
                                            contentDescription = "Return Icon",
                                            modifier = Modifier.size(16.dp),
                                            tint = Color.Black
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Return",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Order Summary",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Item(s) Subtotal (Including VAT)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color(0xFF6B7280)
                                )
                                Text(
                                    text = "900",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color(0xFF6B7280)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Discount 15%:",
                                    fontSize = 14.sp,
                                    color = Color(0xFF6B7280)
                                )
                                Text(
                                    text = "135",
                                    fontSize = 14.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "726.75",
                                    fontSize = 16.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    "726.75",
                                    fontSize = 16.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(3.dp))
                }

                item {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Text(
                            "Shipping Address",
                            fontSize = 16.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Home",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "#712, Azizi Riviera 29,",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )
                        Text(
                            "Nad Al Sheeba 1, Dubai",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }

                item {
                    Column(modifier = Modifier.padding(6.dp))
                    {
                        Text(
                            "Payment Information",
                            fontSize = 16.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "ENBD Credit Card",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280),
                            fontWeight = FontWeight.Normal
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Ending with *4567",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }

                item {
                    Column(modifier = Modifier.padding(6.dp))
                    {
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable{},
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.review),
                                contentDescription = "Review Icon",
                                modifier = Modifier.size(17.dp),
                                tint = Color.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Give Review",
                                fontSize = 15.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable{},
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.contact_support),
                                contentDescription = "Contact Icon",
                                modifier = Modifier.size(17.dp),
                                tint = Color.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Contact Customer Care",
                                fontSize = 15.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}