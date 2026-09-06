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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.karigor.data.local.SaleEntity
import com.example.karigor.data.model.AppLanguage
import com.example.karigor.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SalesScreen(
    language: AppLanguage,
    sales: List<SaleEntity>,
    onNewSaleClick: () -> Unit,
    onInvoiceClick: (SaleEntity) -> Unit
) {
    val isBn = language == AppLanguage.BANGLA
    val currency = "৳"

    val totalRevenue = sales.sumOf { it.total }
    val avgOrderValue = if (sales.isNotEmpty()) totalRevenue / sales.size else 0.0

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewSaleClick,
                containerColor = KarigorEmeraldDark,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("sales_pos_fab")
            ) {
                Icon(Icons.Default.AddShoppingCart, contentDescription = "New Sale POS")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(KarigorBackgroundLight)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header stats
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
                                text = if (isBn) "মোট বিক্রয় ও রাজস্ব" else "Total Revenue",
                                fontSize = 13.sp,
                                color = KarigorSlateLight
                            )
                            Text(
                                text = "$currency${String.format("%,.0f", totalRevenue)}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = KarigorEmeraldDark
                            )
                        }

                        FilledTonalButton(
                            onClick = onNewSaleClick,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = KarigorGold,
                                contentColor = KarigorNavy
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = if (isBn) "নতুন বিক্রি" else "New Sale", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = KarigorBorder)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isBn) "মোট চালান: ${sales.size} টি" else "Total Invoices: ${sales.size}",
                            fontSize = 12.sp,
                            color = KarigorSlateLight
                        )
                        Text(
                            text = if (isBn) "গড় অর্ডার মান: $currency${String.format("%,.0f", avgOrderValue)}" else "Avg Order: $currency${String.format("%,.0f", avgOrderValue)}",
                            fontSize = 12.sp,
                            color = KarigorSlateLight
                        )
                    }
                }
            }

            // Sales Register Header
            Text(
                text = if (isBn) "বিক্রয় রেজিস্টার ও চালান হিস্টোরি" else "Sales Register & Invoice History",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = KarigorNavy
            )

            if (sales.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(48.dp), tint = KarigorSlateLight)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isBn) "কোনো বিক্রির রেকর্ড পাওয়া যায়নি। নতুন বিক্রি শুরু করুন।" else "No sales recorded yet. Create your first sale.",
                            color = KarigorSlateLight,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(sales) { sale ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onInvoiceClick(sale) }
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(KarigorEmeraldContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Receipt,
                                                contentDescription = null,
                                                tint = KarigorEmeraldDark,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column {
                                            Text(
                                                text = sale.customerName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = KarigorNavy
                                            )
                                            Text(
                                                text = "${sale.invoiceNumber} • ${SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(Date(sale.createdAt))}",
                                                fontSize = 11.sp,
                                                color = KarigorSlateLight
                                            )
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "$currency${String.format("%,.0f", sale.total)}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = KarigorEmeraldDark
                                        )
                                        Surface(
                                            color = KarigorEmeraldContainer,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = sale.paymentMethod,
                                                color = KarigorEmeraldDark,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                if (sale.discount > 0 || sale.notes.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        if (sale.discount > 0) {
                                            Text(
                                                text = if (isBn) "ছাড়: $currency${String.format("%,.0f", sale.discount)}" else "Discount: $currency${String.format("%,.0f", sale.discount)}",
                                                fontSize = 11.sp,
                                                color = KarigorError
                                            )
                                        }
                                        if (sale.notes.isNotBlank()) {
                                            Text(
                                                text = "নোট: ${sale.notes}",
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
    }
}
