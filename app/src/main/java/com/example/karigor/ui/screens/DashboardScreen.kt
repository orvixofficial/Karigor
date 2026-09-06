package com.example.karigor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.karigor.data.local.ProductEntity
import com.example.karigor.data.local.SaleEntity
import com.example.karigor.data.model.AppLanguage
import com.example.karigor.data.model.AppStrings
import com.example.karigor.ui.Screen
import com.example.karigor.ui.components.MetricCard
import com.example.karigor.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    language: AppLanguage,
    products: List<ProductEntity>,
    sales: List<SaleEntity>,
    expensesTotal: Double,
    orderCount: Int,
    customerCount: Int,
    onNavigate: (Screen) -> Unit,
    onNewSaleClick: () -> Unit,
    onAddProductClick: () -> Unit,
    onRecordExpenseClick: () -> Unit,
    onInvoiceClick: (SaleEntity) -> Unit
) {
    val totalRevenue = sales.sumOf { it.total }
    val netProfit = totalRevenue - expensesTotal
    val lowStockProducts = products.filter { it.stock <= it.lowStockThreshold }

    val isBn = language == AppLanguage.BANGLA
    val currency = "৳"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(KarigorBackgroundLight)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quick Action Bar
        item {
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
                        Column {
                            Text(
                                text = if (isBn) "দ্রুত কার্যক্রম (Quick Actions)" else "Quick Actions",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = if (isBn) "এক ক্লিকেই ব্যবসা পরিচালনা করুন" else "Manage your commerce in 1-click",
                                color = KarigorEmeraldLight,
                                fontSize = 12.sp
                            )
                        }
                        FilledTonalButton(
                            onClick = onNewSaleClick,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = KarigorGold,
                                contentColor = KarigorNavy
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("dashboard_new_sale_fab")
                        ) {
                            Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = if (isBn) "নতুন বিক্রি" else "New Sale", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onAddProductClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.AddBox, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = if (isBn) "+ পণ্য" else "+ Product", fontSize = 12.sp, color = Color.White)
                        }

                        Button(
                            onClick = onRecordExpenseClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Receipt, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = if (isBn) "+ খরচ" else "+ Expense", fontSize = 12.sp, color = Color.White)
                        }

                        Button(
                            onClick = { onNavigate(Screen.AI_ASSISTANT) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = KarigorGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = if (isBn) "এআই" else "AI Tools", fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        // Primary KPI Metrics Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = AppStrings.get("monthly_sales", language),
                        value = "$currency${String.format("%,.0f", totalRevenue)}",
                        subtitle = "${sales.size} ${if (isBn) "টি চালান" else "Invoices"}",
                        icon = Icons.Default.TrendingUp,
                        containerColor = Color.White,
                        iconTint = KarigorEmeraldPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    MetricCard(
                        title = AppStrings.get("total_expenses", language),
                        value = "$currency${String.format("%,.0f", expensesTotal)}",
                        subtitle = if (isBn) "ভাড়া, বিদ্যুৎ ও অন্যান্য" else "Rent, utilities & ops",
                        icon = Icons.Default.TrendingDown,
                        containerColor = Color.White,
                        iconTint = KarigorError,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = AppStrings.get("estimated_profit", language),
                        value = "$currency${String.format("%,.0f", netProfit)}",
                        subtitle = if (netProfit >= 0) (if (isBn) "লাভজনক অবস্থানে" else "Profitable") else (if (isBn) "ঘাটতি" else "Deficit"),
                        icon = Icons.Default.MonetizationOn,
                        containerColor = if (netProfit >= 0) KarigorEmeraldContainer.copy(alpha = 0.5f) else KarigorSurfaceVariant,
                        iconTint = if (netProfit >= 0) KarigorEmeraldDark else KarigorError,
                        modifier = Modifier.weight(1f)
                    )

                    MetricCard(
                        title = AppStrings.get("low_stock", language),
                        value = "${lowStockProducts.size}",
                        subtitle = if (isBn) "পণ্য রিস্টক প্রয়োজন" else "Needs restocking",
                        icon = Icons.Default.Warning,
                        containerColor = if (lowStockProducts.isNotEmpty()) KarigorGoldContainer.copy(alpha = 0.6f) else Color.White,
                        iconTint = if (lowStockProducts.isNotEmpty()) KarigorGoldDark else KarigorSlateLight,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.INVENTORY) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = AppStrings.get("products", language),
                        value = "${products.size}",
                        subtitle = if (isBn) "মোট সক্রিয় পণ্য" else "Active items",
                        icon = Icons.Default.Inventory2,
                        containerColor = Color.White,
                        iconTint = KarigorInfo,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.PRODUCTS) }
                    )

                    MetricCard(
                        title = AppStrings.get("total_orders", language),
                        value = "$orderCount",
                        subtitle = if (isBn) "অনলাইন স্টোর" else "Online store",
                        icon = Icons.Default.ShoppingBag,
                        containerColor = Color.White,
                        iconTint = KarigorEmeraldPrimary,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.ONLINE_STORE) }
                    )
                }
            }
        }

        // Visual Sales Trend Chart
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isBn) "বিক্রির ট্রেন্ড ও পারফরম্যান্স" else "Sales Performance Trend",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = KarigorNavy
                            )
                            Text(
                                text = if (isBn) "সাপ্তাহিক ও মাসিক বিক্রয় প্রবাহ" else "Weekly revenue visualization",
                                fontSize = 12.sp,
                                color = KarigorSlateLight
                            )
                        }

                        Surface(
                            color = KarigorEmeraldContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (isBn) "সক্রিয়" else "Active",
                                color = KarigorEmeraldDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stylized Bangladesh Weekly Sales Bars
                    val days = if (isBn) listOf("শনি", "রবি", "সোম", "মঙ্গল", "বুধ", "বৃহঃ", "শুক্র")
                    else listOf("Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri")
                    val heights = listOf(0.45f, 0.70f, 0.55f, 0.90f, 0.65f, 0.80f, 1.0f)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        days.forEachIndexed { idx, day ->
                            val heightFraction = heights[idx]
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(22.dp)
                                        .fillMaxHeight(heightFraction)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(if (idx == 6) KarigorGold else KarigorEmeraldPrimary)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = day,
                                    fontSize = 11.sp,
                                    color = KarigorSlateLight,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Low-stock Alerts section if any
        if (lowStockProducts.isNotEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = KarigorGoldContainer.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = KarigorGoldDark)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBn) "স্টক শেষ হওয়ার সতর্কবার্তা (${lowStockProducts.size})" else "Low Stock Alerts (${lowStockProducts.size})",
                                fontWeight = FontWeight.Bold,
                                color = KarigorGoldDark,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        lowStockProducts.take(3).forEach { prod ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(prod.name, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = KarigorNavy)
                                Surface(
                                    color = KarigorError.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "${prod.stock} ${if (isBn) "টি বাকি" else "left"}",
                                        color = KarigorError,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Recent Sales List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBn) "সাম্প্রতিক বিক্রি ও চালান" else "Recent Sales & Invoices",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = KarigorNavy
                )
                TextButton(onClick = { onNavigate(Screen.SALES) }) {
                    Text(text = if (isBn) "সব দেখুন" else "View All", color = KarigorEmeraldPrimary)
                }
            }
        }

        if (sales.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isBn) "এখনও কোনো বিক্রি রেকর্ড করা হয়নি।" else "No sales recorded yet.",
                        color = KarigorSlateLight,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(sales.take(5)) { sale ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onInvoiceClick(sale) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(KarigorEmeraldContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Receipt,
                                    contentDescription = null,
                                    tint = KarigorEmeraldDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = sale.customerName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = KarigorNavy
                                )
                                Text(
                                    text = "${sale.invoiceNumber} • ${sale.paymentMethod}",
                                    fontSize = 12.sp,
                                    color = KarigorSlateLight
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "$currency${String.format("%,.0f", sale.total)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = KarigorEmeraldDark
                            )
                            Surface(
                                color = KarigorEmeraldContainer,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = if (isBn) "পরিশোধিত" else "Paid",
                                    color = KarigorEmeraldDark,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
