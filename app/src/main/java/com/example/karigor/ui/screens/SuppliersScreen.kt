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
import com.example.karigor.data.local.SupplierEntity
import com.example.karigor.data.model.AppLanguage
import com.example.karigor.ui.theme.*

@Composable
fun SuppliersScreen(
    language: AppLanguage,
    suppliers: List<SupplierEntity>,
    onAddSupplierClick: () -> Unit,
    onDeleteSupplier: (SupplierEntity) -> Unit
) {
    val isBn = language == AppLanguage.BANGLA
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "ফ্যাব্রিক ও টেক্সটাইল", "প্যাকেজিং ও কার্টুন", "পাইকারি পণ্য", "লজিস্টিকস ও কুরিয়ার", "মেশিনারি ও যন্ত্রপাতি")

    val filteredSuppliers = suppliers.filter {
        selectedCategory == "All" || it.category.contains(selectedCategory, ignoreCase = true)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddSupplierClick,
                containerColor = KarigorEmeraldDark,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("add_supplier_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Supplier")
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
                        text = if (isBn) "সাপ্লায়ার ও পাইকারি ডিরেক্টরি" else "Suppliers & B2B Directory",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = KarigorNavy
                    )
                    Text(
                        text = if (isBn) "কাঁচামাল, প্যাকেজিং ও পাইকারি সরবরাহকারী" else "Raw materials, packaging & wholesale partners",
                        fontSize = 12.sp,
                        color = KarigorSlateLight
                    )
                }

                FilledTonalButton(
                    onClick = onAddSupplierClick,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = KarigorEmeraldContainer,
                        contentColor = KarigorEmeraldDark
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (isBn) "নতুন সাপ্লায়ার" else "Add Supplier", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (suppliers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isBn) "কোনো সাপ্লায়ার সংরক্ষিত নেই।" else "No suppliers recorded yet.",
                        color = KarigorSlateLight
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredSuppliers) { supplier ->
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
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(KarigorEmeraldContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.LocalShipping,
                                                contentDescription = null,
                                                tint = KarigorEmeraldDark,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(supplier.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = KarigorNavy)
                                            Text("যোগাযোগ: ${supplier.contactPerson} (${supplier.phone})", fontSize = 12.sp, color = KarigorSlateLight)
                                            if (supplier.address.isNotBlank()) {
                                                Text("ঠিকানা: ${supplier.address}", fontSize = 11.sp, color = KarigorSlateLight)
                                            }
                                        }
                                    }

                                    IconButton(onClick = { onDeleteSupplier(supplier) }, modifier = Modifier.size(32.dp)) {
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
                                        text = "সরবরাহকৃত পণ্য: ${supplier.suppliedProducts}",
                                        fontSize = 12.sp,
                                        color = KarigorEmeraldDark,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Surface(
                                        color = KarigorGoldContainer.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = supplier.category,
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
