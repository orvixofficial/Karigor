package com.example.karigor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.window.Dialog
import com.example.karigor.data.local.BusinessEntity
import com.example.karigor.data.local.ProductEntity
import com.example.karigor.data.model.AppLanguage
import com.example.karigor.ui.theme.*

@Composable
fun StorefrontPreviewScreen(
    language: AppLanguage,
    business: BusinessEntity?,
    products: List<ProductEntity>,
    onBack: () -> Unit,
    onPlaceOrder: (String, String, String, String, Double, String) -> Unit
) {
    val isBn = language == AppLanguage.BANGLA
    val currency = "৳"

    // Customer cart state
    val customerCart = remember { mutableStateMapOf<String, Int>() }
    var showCheckoutDialog by remember { mutableStateOf(false) }
    var orderSubmittedSuccess by remember { mutableStateOf(false) }

    val totalItems = customerCart.values.sum()
    val totalAmount = products.sumOf { product ->
        val qty = customerCart[product.id] ?: 0
        product.sellingPrice * qty
    }

    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var customerAddress by remember { mutableStateOf("") }
    var customerNotes by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = {
            if (totalItems > 0) {
                Surface(
                    color = Color.White,
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "$totalItems টি পণ্য ব্যাগে আছে",
                                fontSize = 12.sp,
                                color = KarigorSlateLight
                            )
                            Text(
                                text = "$currency${String.format("%,.0f", totalAmount)}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = KarigorEmeraldDark
                            )
                        }

                        Button(
                            onClick = { showCheckoutDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = KarigorEmeraldDark),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("checkout_order_btn")
                        ) {
                            Icon(Icons.Default.ShoppingBag, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "অর্ডার সম্পন্ন করুন", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(KarigorBackgroundLight)
                .padding(innerPadding)
        ) {
            // Customer Storefront Top Bar
            Surface(
                color = KarigorNavy,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = business?.name ?: "অনলাইন স্টোর",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${business?.category} • ${business?.address ?: "বাংলাদেশ"}",
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 12.sp
                            )
                        }
                        Surface(
                            color = KarigorGold,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "STOREFRONT",
                                color = KarigorNavy,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "“${business?.description ?: "বাংলাদেশের প্রিমিয়াম অনলাইন শপ। সারা দেশে দ্রুত হোম ডেলিভারি।"}”",
                        color = KarigorEmeraldLight,
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            // Products Catalog
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "আমাদের উপলব্ধ পণ্যসমূহ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = KarigorNavy
                    )
                }

                items(products.filter { it.stock > 0 }) { product ->
                    val inCartQty = customerCart[product.id] ?: 0

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KarigorNavy
                                )
                                Text(
                                    text = product.category,
                                    fontSize = 12.sp,
                                    color = KarigorSlateLight
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$currency${String.format("%,.0f", product.sellingPrice)}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KarigorEmeraldDark
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (inCartQty > 0) {
                                    IconButton(
                                        onClick = {
                                            if (inCartQty > 1) {
                                                customerCart[product.id] = inCartQty - 1
                                            } else {
                                                customerCart.remove(product.id)
                                            }
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Remove")
                                    }
                                    Text(
                                        text = "$inCartQty",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp)
                                    )
                                    IconButton(
                                        onClick = {
                                            if (inCartQty < product.stock) {
                                                customerCart[product.id] = inCartQty + 1
                                            }
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Add")
                                    }
                                } else {
                                    FilledTonalButton(
                                        onClick = { customerCart[product.id] = 1 },
                                        colors = ButtonDefaults.filledTonalButtonColors(
                                            containerColor = KarigorEmeraldPrimary,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("ব্যাগে নিন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Checkout Customer Details Dialog
    if (showCheckoutDialog) {
        Dialog(onDismissRequest = { showCheckoutDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ডেলিভারি তথ্য পূরণ করুন",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = KarigorNavy
                    )
                    Text(
                        text = "ক্যাশ অন ডেলিভারি (COD) প্রযোজ্য",
                        fontSize = 12.sp,
                        color = KarigorEmeraldPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("আপনার নাম") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it },
                        label = { Text("মোবাইল নম্বর (বিকাশ/কল)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customerAddress,
                        onValueChange = { customerAddress = it },
                        label = { Text("সম্পূর্ণ ডেলিভারি ঠিকানা") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("মোট প্রদেয়:", fontWeight = FontWeight.Bold)
                        Text("$currency${String.format("%,.0f", totalAmount)}", fontWeight = FontWeight.Bold, color = KarigorEmeraldDark, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val itemsSummary = customerCart.map { (pid, qty) ->
                                val p = products.find { it.id == pid }
                                "${p?.name ?: "Item"} x $qty"
                            }.joinToString(", ")

                            onPlaceOrder(customerName.ifBlank { "অনলাইন কাস্টমার" }, customerPhone, customerAddress, itemsSummary, totalAmount, customerNotes)
                            customerCart.clear()
                            showCheckoutDialog = false
                            orderSubmittedSuccess = true
                        },
                        enabled = customerPhone.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = KarigorEmeraldDark),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("অর্ডার কনফার্ম করুন", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (orderSubmittedSuccess) {
        AlertDialog(
            onDismissRequest = { orderSubmittedSuccess = false },
            confirmButton = {
                Button(
                    onClick = {
                        orderSubmittedSuccess = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = KarigorEmeraldDark)
                ) {
                    Text("মার্চেন্ট প্যানেলে ফিরে যান")
                }
            },
            title = { Text("আলহামদুলিল্লাহ! অর্ডার সফল হয়েছে।", fontWeight = FontWeight.Bold, color = KarigorEmeraldDark) },
            text = { Text("আপনার অর্ডারটি সফলভাবে ব্যবসায়ীর কাছে পাঠানো হয়েছে। মার্চেন্ট ড্যাশবোর্ডে এর নোটিফিকেশন পৌঁছে গেছে।") }
        )
    }
}
