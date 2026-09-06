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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.karigor.data.local.ProductEntity
import com.example.karigor.data.model.AppLanguage
import com.example.karigor.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    language: AppLanguage,
    products: List<ProductEntity>,
    onAddProductClick: () -> Unit,
    onAdjustStockClick: (ProductEntity) -> Unit,
    onDeleteProduct: (ProductEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var statusFilter by remember { mutableStateOf("All") }

    val isBn = language == AppLanguage.BANGLA
    val currency = "৳"

    val categories = remember(products) {
        listOf("All") + products.map { it.category }.distinct()
    }

    val filteredProducts = products.filter { product ->
        val matchesSearch = product.name.contains(searchQuery, ignoreCase = true) ||
                product.sku.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || product.category == selectedCategory
        val matchesStatus = when (statusFilter) {
            "Low Stock" -> product.stock <= product.lowStockThreshold
            "Out of Stock" -> product.stock <= 0
            "Active" -> product.stock > product.lowStockThreshold
            else -> true
        }
        matchesSearch && matchesCategory && matchesStatus
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProductClick,
                containerColor = KarigorEmeraldDark,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("add_product_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(KarigorBackgroundLight)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isBn) "পণ্য ব্যবস্থাপনা (Products)" else "Product Catalog",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = KarigorNavy
                    )
                    Text(
                        text = if (isBn) "মোট ${products.size}টি পণ্য তালিকাভুক্ত" else "${products.size} products listed",
                        fontSize = 12.sp,
                        color = KarigorSlateLight
                    )
                }

                FilledTonalButton(
                    onClick = onAddProductClick,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = KarigorEmeraldContainer,
                        contentColor = KarigorEmeraldDark
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (isBn) "নতুন পণ্য" else "Add Item", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(if (isBn) "পণ্যের নাম বা SKU দিয়ে খুঁজুন..." else "Search by name or SKU...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = KarigorSlateLight) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = KarigorEmeraldPrimary,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("product_search_input"),
                singleLine = true
            )

            // Category Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        label = { Text(if (cat == "All") (if (isBn) "সকল" else "All") else cat) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = KarigorEmeraldPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Products List
            if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Inventory, contentDescription = null, modifier = Modifier.size(48.dp), tint = KarigorSlateLight)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isBn) "কোনো পণ্য পাওয়া যায়নি" else "No products found",
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
                    items(filteredProducts) { product ->
                        val isLowStock = product.stock <= product.lowStockThreshold
                        val isOutOfStock = product.stock <= 0
                        val margin = product.sellingPrice - product.costPrice

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(14.dp),
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
                                                .size(44.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(KarigorEmeraldContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Category,
                                                contentDescription = null,
                                                tint = KarigorEmeraldDark
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = product.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = KarigorNavy
                                            )
                                            Text(
                                                text = "${product.category} • SKU: ${if (product.sku.isNotEmpty()) product.sku else "N/A"}",
                                                fontSize = 12.sp,
                                                color = KarigorSlateLight
                                            )
                                        }
                                    }

                                    // Stock Status Badge
                                    Surface(
                                        color = when {
                                            isOutOfStock -> KarigorError.copy(alpha = 0.15f)
                                            isLowStock -> KarigorGoldContainer
                                            else -> KarigorEmeraldContainer
                                        },
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = when {
                                                isOutOfStock -> if (isBn) "স্টক শেষ" else "Out of Stock"
                                                isLowStock -> if (isBn) "স্টক কম (${product.stock})" else "Low (${product.stock})"
                                                else -> if (isBn) "মজুদ: ${product.stock} টি" else "Stock: ${product.stock}"
                                            },
                                            color = when {
                                                isOutOfStock -> KarigorError
                                                isLowStock -> KarigorGoldDark
                                                else -> KarigorEmeraldDark
                                            },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = KarigorBorder)
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "$currency${String.format("%,.0f", product.sellingPrice)}",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = KarigorEmeraldDark
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "(ক্রয়: $currency${String.format("%,.0f", product.costPrice)})",
                                                fontSize = 12.sp,
                                                color = KarigorSlateLight
                                            )
                                        }
                                        Text(
                                            text = if (isBn) "মুনাফা: $currency${String.format("%,.0f", margin)} প্রতি ইউনিটে" else "Margin: $currency${String.format("%,.0f", margin)}/unit",
                                            fontSize = 11.sp,
                                            color = if (margin >= 0) KarigorEmeraldPrimary else KarigorError
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        OutlinedButton(
                                            onClick = { onAdjustStockClick(product) },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(if (isBn) "স্টক সমন্বয়" else "Adjust", fontSize = 12.sp)
                                        }

                                        IconButton(
                                            onClick = { onDeleteProduct(product) },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = KarigorError)
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
