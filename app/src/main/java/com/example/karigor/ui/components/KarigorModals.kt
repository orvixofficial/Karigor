package com.example.karigor.ui.components

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
import androidx.compose.ui.window.Dialog
import com.example.karigor.data.local.NotificationEntity
import com.example.karigor.data.local.ProductEntity
import com.example.karigor.data.model.AppLanguage
import com.example.karigor.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddProductDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (name: String, sku: String, category: String, sellingPrice: Double, costPrice: Double, stock: Int, lowStockThreshold: Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var sku by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var sellingPriceStr by remember { mutableStateOf("") }
    var costPriceStr by remember { mutableStateOf("") }
    var stockStr by remember { mutableStateOf("") }
    var thresholdStr by remember { mutableStateOf("5") }

    val isBn = language == AppLanguage.BANGLA

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (isBn) "নতুন পণ্য যুক্ত করুন" else "Add New Product",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = KarigorEmeraldDark
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (isBn) "পণ্যের নাম *" else "Product Name *") },
                    modifier = Modifier.fillMaxWidth().testTag("product_name_input"),
                    singleLine = true
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = sku,
                        onValueChange = { sku = it },
                        label = { Text("SKU / কোড") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text(if (isBn) "ক্যাটাগরি" else "Category") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = sellingPriceStr,
                        onValueChange = { sellingPriceStr = it },
                        label = { Text(if (isBn) "বিক্রয়মূল্য (৳) *" else "Selling Price *") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = costPriceStr,
                        onValueChange = { costPriceStr = it },
                        label = { Text(if (isBn) "ক্রয়মূল্য (৳)" else "Cost Price") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = stockStr,
                        onValueChange = { stockStr = it },
                        label = { Text(if (isBn) "প্রারম্ভিক স্টক *" else "Initial Stock *") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = thresholdStr,
                        onValueChange = { thresholdStr = it },
                        label = { Text(if (isBn) "সতর্কতা সীমা" else "Low Alert Limit") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isBn) "বাতিল" else "Cancel")
                    }
                    Button(
                        onClick = {
                            val sell = sellingPriceStr.toDoubleOrNull() ?: 0.0
                            val cost = costPriceStr.toDoubleOrNull() ?: (sell * 0.7)
                            val stk = stockStr.toIntOrNull() ?: 0
                            val th = thresholdStr.toIntOrNull() ?: 5
                            onConfirm(name, sku, category.ifBlank { "সাধারণ" }, sell, cost, stk, th)
                        },
                        enabled = name.isNotBlank() && sellingPriceStr.isNotBlank() && stockStr.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = KarigorEmeraldDark),
                        modifier = Modifier.weight(1f).testTag("save_product_btn")
                    ) {
                        Text(if (isBn) "সংরক্ষণ করুন" else "Save")
                    }
                }
            }
        }
    }
}

@Composable
fun AdjustStockDialog(
    language: AppLanguage,
    product: ProductEntity,
    onDismiss: () -> Unit,
    onConfirm: (changeAmount: Int, reason: String) -> Unit
) {
    var changeStr by remember { mutableStateOf("1") }
    var isAddition by remember { mutableStateOf(true) }
    var reason by remember { mutableStateOf("নতুন চালান বা রিস্টক") }

    val isBn = language == AppLanguage.BANGLA
    val reasons = listOf("নতুন চালান বা রিস্টক", "বিক্রিজনিত রিটার্ন", "নষ্ট বা ড্যামেজ বাদ", "স্টক নিরীক্ষা বা সমন্বয়")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (isBn) "স্টক সমন্বয়: ${product.name}" else "Adjust Stock: ${product.name}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = KarigorEmeraldDark
                )
                Text(
                    text = "বর্তমান মজুদ: ${product.stock} টি",
                    fontSize = 13.sp,
                    color = KarigorSlateLight
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(
                        onClick = { isAddition = true },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (isAddition) KarigorEmeraldPrimary else KarigorSurfaceVariant,
                            contentColor = if (isAddition) Color.White else KarigorNavy
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+ স্টক বৃদ্ধি")
                    }
                    FilledTonalButton(
                        onClick = { isAddition = false },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (!isAddition) KarigorError else KarigorSurfaceVariant,
                            contentColor = if (!isAddition) Color.White else KarigorNavy
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("- স্টক হ্রাস")
                    }
                }

                OutlinedTextField(
                    value = changeStr,
                    onValueChange = { changeStr = it },
                    label = { Text("সমন্বয়ের পরিমাণ (সংখ্যা)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text("সমন্বয়ের কারণ নির্বাচন করুন:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                reasons.forEach { r ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { reason = r },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = reason == r, onClick = { reason = r })
                        Text(r, fontSize = 13.sp)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text(if (isBn) "বাতিল" else "Cancel")
                    }
                    Button(
                        onClick = {
                            val qty = changeStr.toIntOrNull() ?: 1
                            val finalChange = if (isAddition) qty else -qty
                            onConfirm(finalChange, reason)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = KarigorEmeraldDark),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isBn) "সমন্বয় নিশ্চিত করুন" else "Confirm")
                    }
                }
            }
        }
    }
}

@Composable
fun AddExpenseDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (category: String, amount: Double, date: String, notes: String) -> Unit
) {
    var category by remember { mutableStateOf("দোকান ভাড়া (Rent)") }
    var amountStr by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val isBn = language == AppLanguage.BANGLA
    val categories = listOf("দোকান ভাড়া (Rent)", "কর্মচারী বেতন (Salaries)", "বিদ্যুৎ বিল (Electricity)", "বিজ্ঞাপন ও মার্কেটিং (Marketing)", "পরিবহন খরচ (Transport)", "প্যাকেজিং ও অন্যান্য (Packaging)")

    val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (isBn) "খরচ যুক্ত করুন" else "Record Operational Expense",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = KarigorError
                )

                Text("খরচের খাত নির্বাচন করুন:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                categories.take(4).forEach { cat ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { category = cat },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = category == cat, onClick = { category = cat })
                        Text(cat, fontSize = 13.sp)
                    }
                }

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("টাকার পরিমাণ (৳) *") },
                    modifier = Modifier.fillMaxWidth().testTag("expense_amount_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("বিবরণ / নোট") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text(if (isBn) "বাতিল" else "Cancel")
                    }
                    Button(
                        onClick = {
                            val amt = amountStr.toDoubleOrNull() ?: 0.0
                            onConfirm(category, amt, todayDate, notes)
                        },
                        enabled = amountStr.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = KarigorError),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isBn) "খরচ সংরক্ষণ করুন" else "Save Expense")
                    }
                }
            }
        }
    }
}

@Composable
fun AddCustomerDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String, email: String, address: String, notes: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val isBn = language == AppLanguage.BANGLA

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (isBn) "নতুন গ্রাহক নিবন্ধন" else "Add New Customer",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = KarigorEmeraldDark
                )

                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("গ্রাহকের নাম *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("মোবাইল নম্বর *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("ঠিকানা") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("নোট (যেমন: VIP, নিয়মিত ক্রেতা)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text(if (isBn) "বাতিল" else "Cancel")
                    }
                    Button(
                        onClick = { onConfirm(name, phone, email, address, notes) },
                        enabled = name.isNotBlank() && phone.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = KarigorEmeraldDark),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isBn) "যুক্ত করুন" else "Save")
                    }
                }
            }
        }
    }
}

@Composable
fun AddSupplierDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (name: String, contactPerson: String, phone: String, category: String, address: String, suppliedProducts: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var contactPerson by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("পাইকারি সাপ্লায়ার") }
    var address by remember { mutableStateOf("") }
    var suppliedProducts by remember { mutableStateOf("") }

    val isBn = language == AppLanguage.BANGLA

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (isBn) "নতুন সাপ্লায়ার যুক্ত করুন" else "Add New Supplier",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = KarigorEmeraldDark
                )

                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("প্রতিষ্ঠানের নাম *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = contactPerson, onValueChange = { contactPerson = it }, label = { Text("যোগাযোগকারী ব্যক্তি") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("মোবাইল নম্বর *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = suppliedProducts, onValueChange = { suppliedProducts = it }, label = { Text("সরবরাহকৃত পণ্যসমূহ") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("ঠিকানা / মার্কেট") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text(if (isBn) "বাতিল" else "Cancel")
                    }
                    Button(
                        onClick = { onConfirm(name, contactPerson, phone, category, address, suppliedProducts) },
                        enabled = name.isNotBlank() && phone.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = KarigorEmeraldDark),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isBn) "সংরক্ষণ করুন" else "Save")
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingBusinessDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (name: String, category: String, ownerName: String, phone: String, email: String, address: String, description: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Retail & Fashion") }
    var ownerName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val isBn = language == AppLanguage.BANGLA
    val categories = listOf("Retail & Fashion", "Grocery & Supermarket", "Food & Restaurant", "Electronics & Gadget", "Wholesale & Trading")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (isBn) "নতুন ব্যবসা প্রোফাইল তৈরি করুন" else "Create New Business Profile",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = KarigorEmeraldDark
                )
                Text(
                    text = if (isBn) "কারিগর ক্লাউডে আপনার প্রতিষ্ঠানের ডিজিটাল পরিচিতি গড়ে তুলুন।" else "Set up your multi-tenant digital business workspace.",
                    fontSize = 12.sp,
                    color = KarigorSlateLight
                )

                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("ব্যবসার নাম *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = ownerName, onValueChange = { ownerName = it }, label = { Text("মালিকের নাম *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("অফিসিয়াল মোবাইল নম্বর *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("দোকান / অফিসের ঠিকানা") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("ব্যবসার বিবরণ ও স্লোগান") }, modifier = Modifier.fillMaxWidth(), maxLines = 2)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text(if (isBn) "বাতিল" else "Cancel")
                    }
                    Button(
                        onClick = { onConfirm(name, category, ownerName, phone, email, address, description) },
                        enabled = name.isNotBlank() && ownerName.isNotBlank() && phone.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = KarigorEmeraldDark),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isBn) "ব্যবসা শুরু করুন" else "Create Business")
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationsDialog(
    language: AppLanguage,
    notifications: List<NotificationEntity>,
    onDismiss: () -> Unit,
    onMarkAllRead: () -> Unit
) {
    val isBn = language == AppLanguage.BANGLA

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.7f).padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBn) "নোটিফিকেশন ও অ্যালার্ট" else "Notifications & Alerts",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = KarigorNavy
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onMarkAllRead) {
                        Text(if (isBn) "সব পড়া হয়েছে মার্ক করুন" else "Mark all read", fontSize = 12.sp, color = KarigorEmeraldPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (notifications.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(if (isBn) "কোনো নোটিফিকেশন নেই।" else "No notifications.", color = KarigorSlateLight)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(notifications) { notif ->
                            Surface(
                                color = if (notif.isRead) Color(0xFFF8FAFC) else KarigorEmeraldContainer.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = when (notif.type) {
                                            "LOW_STOCK" -> Icons.Default.Warning
                                            "SALE" -> Icons.Default.CheckCircle
                                            "NEW_ORDER" -> Icons.Default.ShoppingBag
                                            else -> Icons.Default.Notifications
                                        },
                                        contentDescription = null,
                                        tint = when (notif.type) {
                                            "LOW_STOCK" -> KarigorGoldDark
                                            "SALE" -> KarigorEmeraldDark
                                            "NEW_ORDER" -> Color(0xFF0369A1)
                                            else -> KarigorEmeraldPrimary
                                        },
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(notif.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = KarigorNavy)
                                        Text(notif.message, fontSize = 12.sp, color = KarigorSlateLight)
                                        Text(
                                            SimpleDateFormat("dd MMM, hh:mm a", Locale.US).format(Date(notif.createdAt)),
                                            fontSize = 10.sp,
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
