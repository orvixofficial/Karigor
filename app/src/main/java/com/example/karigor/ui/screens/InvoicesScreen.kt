package com.example.karigor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.karigor.data.local.SaleEntity
import com.example.karigor.data.model.AppLanguage
import com.example.karigor.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun InvoicesScreen(
    language: AppLanguage,
    sales: List<SaleEntity>,
    onInvoiceClick: (SaleEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val isBn = language == AppLanguage.BANGLA
    val currency = "৳"

    val filteredSales = sales.filter {
        it.invoiceNumber.contains(searchQuery, ignoreCase = true) ||
                it.customerName.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KarigorBackgroundLight)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isBn) "চালান ও ইনভয়েস তালিকা" else "Invoices & Billing Records",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = KarigorNavy
                )
                Text(
                    text = if (isBn) "মোট ${sales.size}টি চালান সংরক্ষিত" else "${sales.size} invoices archived",
                    fontSize = 12.sp,
                    color = KarigorSlateLight
                )
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(if (isBn) "চালান নম্বর বা ক্রেতার নাম দিয়ে খুঁজুন..." else "Search invoice # or customer...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = KarigorSlateLight) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = KarigorEmeraldPrimary,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // List
        if (filteredSales.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isBn) "কোনো চালান পাওয়া যায়নি।" else "No invoices found.",
                    color = KarigorSlateLight
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredSales) { sale ->
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
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Description, contentDescription = null, tint = KarigorEmeraldPrimary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = sale.invoiceNumber,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = KarigorNavy
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "ক্রেতা: ${sale.customerName}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = KarigorNavy
                                )
                                Text(
                                    text = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(Date(sale.createdAt)),
                                    fontSize = 11.sp,
                                    color = KarigorSlateLight
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "$currency${String.format("%,.0f", sale.total)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = KarigorEmeraldDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    color = KarigorEmeraldContainer,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = if (isBn) "চালান দেখুন" else "View / Print",
                                        color = KarigorEmeraldDark,
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
    }
}
