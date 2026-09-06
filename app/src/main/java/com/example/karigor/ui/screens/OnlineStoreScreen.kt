package com.example.karigor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.karigor.data.local.BusinessEntity
import com.example.karigor.data.local.OrderEntity
import com.example.karigor.data.model.AppLanguage
import com.example.karigor.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OnlineStoreScreen(
    language: AppLanguage,
    business: BusinessEntity?,
    orders: List<OrderEntity>,
    onPreviewStorefront: () -> Unit,
    onUpdateOrderStatus: (String, String) -> Unit
) {
    val isBn = language == AppLanguage.BANGLA
    val currency = "৳"

    val storeSlug = business?.slug ?: "shop"
    val storeUrl = "https://karigor.com.bd/store/$storeSlug"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KarigorBackgroundLight)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Store Status Card
        Card(
            colors = CardDefaults.cardColors(containerColor = KarigorEmeraldDark),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isBn) "অনলাইন ই-কমার্স স্টোর" else "Online eCommerce Store",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isBn) "আপনার ক্লাউড ডিজিটাল শো-রুম" else "Your digital storefront is LIVE",
                            color = KarigorEmeraldLight,
                            fontSize = 12.sp
                        )
                    }

                    Surface(
                        color = KarigorGold,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(KarigorNavy))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("LIVE", color = KarigorNavy, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = Color.White.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isBn) "স্টোর লিংক:" else "Storefront URL:",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp
                            )
                            Text(
                                text = storeUrl,
                                color = KarigorGold,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        FilledTonalButton(
                            onClick = onPreviewStorefront,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = KarigorGold,
                                contentColor = KarigorNavy
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("preview_store_btn")
                        ) {
                            Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = if (isBn) "কাস্টমার ভিউ" else "Customer View", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Orders Management
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isBn) "অনলাইন অর্ডারসমূহ (${orders.size})" else "Online Orders (${orders.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = KarigorNavy
                )
                Text(
                    text = if (isBn) "গ্রাহকদের করা অর্ডার ডেলিভারি ট্র্যাক করুন" else "Manage incoming customer web orders",
                    fontSize = 12.sp,
                    color = KarigorSlateLight
                )
            }
        }

        if (orders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(48.dp), tint = KarigorSlateLight)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isBn) "এখনও কোনো নতুন অনলাইন অর্ডার আসেনি।" else "No online orders received yet.",
                        color = KarigorSlateLight,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    TextButton(onClick = onPreviewStorefront) {
                        Text(text = if (isBn) "কাস্টমার স্টোর ওপেন করে টেস্ট অর্ডার দিন" else "Open Storefront to place a test order", color = KarigorEmeraldPrimary)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(orders) { order ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${order.orderNumber} • ${order.customerName}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = KarigorNavy
                                    )
                                    Text(
                                        text = "মোবাইল: ${order.customerPhone}",
                                        fontSize = 12.sp,
                                        color = KarigorSlateLight
                                    )
                                    Text(
                                        text = "ঠিকানা: ${order.customerAddress}",
                                        fontSize = 11.sp,
                                        color = KarigorSlateLight
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "$currency${String.format("%,.0f", order.totalAmount)}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = KarigorEmeraldDark
                                    )
                                    Surface(
                                        color = when (order.status) {
                                            "New" -> KarigorGoldContainer
                                            "Confirmed" -> KarigorEmeraldContainer
                                            "Shipped" -> Color(0xFFE0F2FE)
                                            else -> KarigorSurfaceVariant
                                        },
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = order.status,
                                            color = when (order.status) {
                                                "New" -> KarigorGoldDark
                                                "Confirmed" -> KarigorEmeraldDark
                                                "Shipped" -> Color(0xFF0369A1)
                                                else -> KarigorNavy
                                            },
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = KarigorBorder)
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "পণ্যের বিবরণ: ${order.itemsSummary}",
                                fontSize = 12.sp,
                                color = KarigorNavy
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Status workflow buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("New", "Confirmed", "Shipped", "Delivered").forEach { status ->
                                    val isCurrent = order.status == status
                                    FilledTonalButton(
                                        onClick = { onUpdateOrderStatus(order.id, status) },
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.filledTonalButtonColors(
                                            containerColor = if (isCurrent) KarigorEmeraldPrimary else KarigorSurfaceVariant,
                                            contentColor = if (isCurrent) Color.White else KarigorNavy
                                        ),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(status, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
