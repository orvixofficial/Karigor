package com.example.karigor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.karigor.data.local.InventoryLogEntity
import com.example.karigor.data.local.ProductEntity
import com.example.karigor.data.model.AppLanguage
import com.example.karigor.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun InventoryScreen(
    language: AppLanguage,
    products: List<ProductEntity>,
    inventoryLogs: List<InventoryLogEntity>,
    onAdjustStockClick: (ProductEntity) -> Unit
) {
    val isBn = language == AppLanguage.BANGLA
    val currency = "৳"

    var selectedTab by remember { mutableStateOf(0) } // 0: Stock Levels, 1: Audit Log

    val totalValuation = products.sumOf { it.stock * it.costPrice }
    val totalUnits = products.sumOf { it.stock }
    val lowStockCount = products.count { it.stock <= it.lowStockThreshold }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KarigorBackgroundLight)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Summary Header Card
        Card(
            colors = CardDefaults.cardColors(containerColor = KarigorEmeraldDark),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isBn) "ক্লাউড ইনভেন্টরি ও গুদাম স্টক" else "Cloud Inventory Valuation",
                    color = KarigorEmeraldLight,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$currency${String.format("%,.0f", totalValuation)}",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = if (isBn) "মোট মজুদ: $totalUnits টি" else "Total Units: $totalUnits",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp
                    )
                    Text(
                        text = if (isBn) "স্টক কম: $lowStockCount টি" else "Low Stock: $lowStockCount",
                        color = if (lowStockCount > 0) KarigorGold else Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        fontWeight = if (lowStockCount > 0) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // Tab Row (Stock vs Log)
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = KarigorEmeraldPrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text(if (isBn) "পণ্যের স্টক মাত্রা" else "Stock Levels", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text(if (isBn) "স্টক অডিট হিস্টোরি" else "Stock Audit Trail", fontWeight = FontWeight.Bold) }
            )
        }

        if (selectedTab == 0) {
            // Stock Levels List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(products) { product ->
                    val isLow = product.stock <= product.lowStockThreshold

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = KarigorNavy
                                )
                                Text(
                                    text = "SKU: ${product.sku} • থ্রেশহোল্ড: ${product.lowStockThreshold} টি",
                                    fontSize = 12.sp,
                                    color = KarigorSlateLight
                                )
                                Text(
                                    text = "ইউনিট ক্রয়মূল্য: $currency${String.format("%,.0f", product.costPrice)}",
                                    fontSize = 12.sp,
                                    color = KarigorSlateLight
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Surface(
                                    color = if (isLow) KarigorGoldContainer else KarigorEmeraldContainer,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "${product.stock} ${if (isBn) "টি অবশিষ্ট" else "pcs"}",
                                        color = if (isLow) KarigorGoldDark else KarigorEmeraldDark,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Button(
                                    onClick = { onAdjustStockClick(product) },
                                    colors = ButtonDefaults.buttonColors(containerColor = KarigorEmeraldPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = if (isBn) "সমন্বয়" else "Adjust", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Audit Log
            if (inventoryLogs.isEmpty()) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (isBn) "এখনও কোনো স্টক সমন্বয়ের লগ নেই।" else "No inventory logs recorded.",
                        color = KarigorSlateLight
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(inventoryLogs) { log ->
                        val isPositive = log.changeAmount > 0
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = log.productName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = KarigorNavy
                                    )
                                    Text(
                                        text = "${log.reason} • ${SimpleDateFormat("dd MMM, hh:mm a", Locale.US).format(Date(log.createdAt))}",
                                        fontSize = 12.sp,
                                        color = KarigorSlateLight
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${if (isPositive) "+" else ""}${log.changeAmount}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = if (isPositive) KarigorEmeraldPrimary else KarigorError
                                    )
                                    Text(
                                        text = "নতুন মজুদ: ${log.newQuantity}",
                                        fontSize = 11.sp,
                                        color = KarigorSlateLight
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
