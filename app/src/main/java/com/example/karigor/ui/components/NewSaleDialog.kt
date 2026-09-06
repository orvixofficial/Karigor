package com.example.karigor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.karigor.data.local.CustomerEntity
import com.example.karigor.data.local.ProductEntity
import com.example.karigor.data.model.AppLanguage
import com.example.karigor.data.repository.KarigorRepository
import com.example.karigor.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSaleDialog(
    language: AppLanguage,
    products: List<ProductEntity>,
    customers: List<CustomerEntity>,
    cartItems: List<KarigorRepository.CartItem>,
    selectedCustomer: CustomerEntity?,
    discount: Double,
    paymentMethod: String,
    onAddToCart: (ProductEntity) -> Unit,
    onRemoveFromCart: (ProductEntity) -> Unit,
    onCustomerSelect: (CustomerEntity?) -> Unit,
    onDiscountChange: (Double) -> Unit,
    onPaymentMethodChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSubmitSale: (String, String) -> Unit
) {
    var walkInCustomerName by remember { mutableStateOf(selectedCustomer?.name ?: "") }
    var notes by remember { mutableStateOf("") }
    var productSearch by remember { mutableStateOf("") }
    var discountInput by remember { mutableStateOf(if (discount > 0) discount.toString() else "") }

    val isBn = language == AppLanguage.BANGLA
    val currency = "৳"

    val subtotal = cartItems.sumOf { it.product.sellingPrice * it.quantity }
    val currentDiscount = discountInput.toDoubleOrNull() ?: 0.0
    val grandTotal = (subtotal - currentDiscount).coerceAtLeast(0.0)

    val paymentMethods = listOf("Cash", "bKash / Nagad", "Bank Transfer", "Other")

    val availableProducts = products.filter {
        it.stock > 0 && (it.name.contains(productSearch, ignoreCase = true) || it.sku.contains(productSearch, ignoreCase = true))
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            shape = RoundedCornerShape(20.dp),
            color = KarigorBackgroundLight
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isBn) "নতুন বিক্রি (POS Checkout)" else "New POS Sale",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = KarigorEmeraldDark
                        )
                        Text(
                            text = if (isBn) "পণ্য যোগ করুন ও স্বয়ংক্রিয় চালান তৈরি করুন" else "Add items and generate invoice",
                            fontSize = 12.sp,
                            color = KarigorSlateLight
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Two columns / Sections layout
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Left Column: Catalog selection
                    Card(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isBn) "পণ্য নির্বাচন করুন" else "Select Products",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = KarigorNavy
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = productSearch,
                                onValueChange = { productSearch = it },
                                placeholder = { Text(if (isBn) "খুঁজুন..." else "Search...", fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(availableProducts) { product ->
                                    val inCartQty = cartItems.find { it.product.id == product.id }?.quantity ?: 0
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { onAddToCart(product) },
                                        color = if (inCartQty > 0) KarigorEmeraldContainer.copy(alpha = 0.5f) else KarigorSurfaceVariant,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(product.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                                                Text(
                                                    "$currency${String.format("%,.0f", product.sellingPrice)} • স্টক: ${product.stock}",
                                                    fontSize = 11.sp,
                                                    color = KarigorSlateLight
                                                )
                                            }
                                            if (inCartQty > 0) {
                                                Surface(
                                                    color = KarigorEmeraldDark,
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Text(
                                                        text = "x$inCartQty",
                                                        color = Color.White,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            } else {
                                                Icon(Icons.Default.Add, contentDescription = "Add", tint = KarigorEmeraldPrimary, modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Right Column: Cart & Checkout
                    Card(
                        modifier = Modifier
                            .weight(1.3f)
                            .fillMaxHeight(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxHeight()
                        ) {
                            Text(
                                text = if (isBn) "অর্ডার তালিকা ও বিলিং" else "Cart & Billing",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = KarigorNavy
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Customer name
                            OutlinedTextField(
                                value = walkInCustomerName,
                                onValueChange = { walkInCustomerName = it },
                                label = { Text(if (isBn) "ক্রেতার নাম (ঐচ্ছিক)" else "Customer Name") },
                                placeholder = { Text(if (isBn) "নগদ ক্রেতা (Walk-in)" else "Walk-in Customer") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Cart items
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (cartItems.isEmpty()) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(20.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = if (isBn) "কার্ট খালি। বাম পাশ থেকে পণ্য নির্বাচন করুন।" else "Cart empty. Click products on left.",
                                                fontSize = 12.sp,
                                                color = KarigorSlateLight
                                            )
                                        }
                                    }
                                } else {
                                    items(cartItems) { item ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(KarigorSurfaceVariant, RoundedCornerShape(8.dp))
                                                .padding(6.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(item.product.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                                                Text("$currency${String.format("%,.0f", item.product.sellingPrice * item.quantity)}", fontSize = 11.sp, color = KarigorEmeraldDark, fontWeight = FontWeight.Bold)
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                IconButton(onClick = { onRemoveFromCart(item.product) }, modifier = Modifier.size(24.dp)) {
                                                    Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(14.dp))
                                                }
                                                Text("${item.quantity}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                IconButton(onClick = { onAddToCart(item.product) }, modifier = Modifier.size(24.dp)) {
                                                    Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(14.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Payment method selector
                            Text(
                                text = if (isBn) "পেমেন্ট মাধ্যম:" else "Payment Method:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = KarigorNavy
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                paymentMethods.forEach { method ->
                                    val isSelected = paymentMethod == method
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable { onPaymentMethodChange(method) },
                                        color = if (isSelected) KarigorEmeraldPrimary else KarigorSurfaceVariant
                                    ) {
                                        Text(
                                            text = when (method) {
                                                "Cash" -> if (isBn) "নগদ" else "Cash"
                                                "bKash / Nagad" -> "বিকাশ/নগদ"
                                                "Bank Transfer" -> if (isBn) "ব্যাংক" else "Bank"
                                                else -> if (isBn) "অন্যান্য" else "Other"
                                            },
                                            color = if (isSelected) Color.White else KarigorNavy,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(vertical = 6.dp),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Discount input
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = discountInput,
                                    onValueChange = {
                                        discountInput = it
                                        onDiscountChange(it.toDoubleOrNull() ?: 0.0)
                                    },
                                    label = { Text(if (isBn) "ছাড় (৳)" else "Discount (৳)") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                    Text(text = if (isBn) "সর্বমোট:" else "Grand Total:", fontSize = 11.sp, color = KarigorSlateLight)
                                    Text(
                                        text = "$currency${String.format("%,.0f", grandTotal)}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = KarigorEmeraldDark
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Checkout Button
                            Button(
                                onClick = {
                                    onSubmitSale(walkInCustomerName, notes)
                                },
                                enabled = cartItems.isNotEmpty(),
                                colors = ButtonDefaults.buttonColors(containerColor = KarigorEmeraldDark),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("submit_sale_btn")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isBn) "বিক্রি সম্পন্ন করুন ($currency${String.format("%,.0f", grandTotal)})" else "Complete Sale ($currency${String.format("%,.0f", grandTotal)})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
