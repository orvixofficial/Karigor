package com.example.karigor.ui.screens

import androidx.compose.foundation.background
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
import com.example.karigor.data.local.CustomerEntity
import com.example.karigor.data.local.SaleEntity
import com.example.karigor.data.model.AppLanguage
import com.example.karigor.ui.theme.*

@Composable
fun CustomersScreen(
    language: AppLanguage,
    customers: List<CustomerEntity>,
    sales: List<SaleEntity>,
    onAddCustomerClick: () -> Unit,
    onDeleteCustomer: (CustomerEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val isBn = language == AppLanguage.BANGLA
    val currency = "৳"

    val filteredCustomers = customers.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.phone.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCustomerClick,
                containerColor = KarigorEmeraldDark,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("add_customer_fab")
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Add Customer")
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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isBn) "গ্রাহক ব্যবস্থাপনা (Customers)" else "Customer Directory",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = KarigorNavy
                    )
                    Text(
                        text = if (isBn) "মোট ${customers.size} জন নিবন্ধিত গ্রাহক" else "${customers.size} customers recorded",
                        fontSize = 12.sp,
                        color = KarigorSlateLight
                    )
                }

                FilledTonalButton(
                    onClick = onAddCustomerClick,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = KarigorEmeraldContainer,
                        contentColor = KarigorEmeraldDark
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (isBn) "নতুন গ্রাহক" else "Add Customer", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(if (isBn) "গ্রাহকের নাম বা মোবাইল নম্বর খুঁজুন..." else "Search customer name or phone...") },
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

            // Customers list
            if (filteredCustomers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isBn) "কোনো গ্রাহক পাওয়া যায়নি।" else "No customers found.",
                        color = KarigorSlateLight
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredCustomers) { customer ->
                        val customerSales = sales.filter { it.customerId == customer.id || it.customerName.equals(customer.name, ignoreCase = true) }
                        val totalSpent = customerSales.sumOf { it.total }

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
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Row(modifier = Modifier.weight(1f)) {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(KarigorEmeraldContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = null,
                                                tint = KarigorEmeraldDark,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = customer.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = KarigorNavy
                                            )
                                            Text(
                                                text = "মোবাইল: ${customer.phone}",
                                                fontSize = 12.sp,
                                                color = KarigorSlateLight
                                            )
                                            if (customer.address.isNotBlank()) {
                                                Text(
                                                    text = "ঠিকানা: ${customer.address}",
                                                    fontSize = 11.sp,
                                                    color = KarigorSlateLight
                                                )
                                            }
                                        }
                                    }

                                    IconButton(
                                        onClick = { onDeleteCustomer(customer) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = KarigorError)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = KarigorBorder)
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "মোট ক্রয়: $currency${String.format("%,.0f", totalSpent)} (${customerSales.size} বার)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = KarigorEmeraldDark
                                    )

                                    if (customer.notes.isNotBlank()) {
                                        Surface(
                                            color = KarigorGoldContainer.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = customer.notes,
                                                color = KarigorGoldDark,
                                                fontSize = 10.sp,
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
    }
}
