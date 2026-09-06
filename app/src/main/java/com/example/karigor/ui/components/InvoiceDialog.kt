package com.example.karigor.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.karigor.data.local.BusinessEntity
import com.example.karigor.data.local.SaleEntity
import com.example.karigor.data.model.AppLanguage
import com.example.karigor.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun InvoiceDialog(
    language: AppLanguage,
    sale: SaleEntity,
    business: BusinessEntity?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isBn = language == AppLanguage.BANGLA
    val currency = "৳"

    val dateFormatted = SimpleDateFormat("dd MMMM, yyyy - hh:mm a", Locale.US).format(Date(sale.createdAt))

    fun shareReceipt() {
        val receiptText = """
            ========================================
                      ${business?.name ?: "Karigor Merchant"}
              ${business?.address ?: "Bangladesh"}
              Phone: ${business?.phone ?: ""}
            ========================================
            চালান নং / INVOICE: ${sale.invoiceNumber}
            তারিখ / Date: $dateFormatted
            ক্রেতা / Bill To: ${sale.customerName}
            ----------------------------------------
            উপমোট / Subtotal: $currency${String.format("%,.2f", sale.subtotal)}
            ছাড় / Discount: $currency${String.format("%,.2f", sale.discount)}
            ----------------------------------------
            সর্বমোট / TOTAL: $currency${String.format("%,.2f", sale.total)}
            পেমেন্ট / Payment: ${sale.paymentMethod} (PAID)
            ----------------------------------------
            ${sale.notes.let { if (it.isNotBlank()) "নোট: $it\n" else "" }}
            ধন্যবাদ! আবার আসবেন।
            Powered by Karigor (কারিগর)
            “বাংলাদেশের ব্যবসার এক স্মার্ট ঠিকানা।”
            ========================================
        """.trimIndent()

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, receiptText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "চালান শেয়ার বা প্রিন্ট করুন")
        context.startActivity(shareIntent)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(20.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Actions Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = { shareReceipt() },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = KarigorEmeraldContainer,
                            contentColor = KarigorEmeraldDark
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = if (isBn) "শেয়ার / প্রিন্ট" else "Share / Print", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Invoice Printable Canvas
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .border(1.dp, KarigorBorder, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFCFCFD)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = business?.name ?: "কারিগর মার্চেন্ট",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KarigorNavy
                                )
                                Text(
                                    text = business?.address ?: "ঢাকা, বাংলাদেশ",
                                    fontSize = 12.sp,
                                    color = KarigorSlateLight
                                )
                                Text(
                                    text = "মোবাইল: ${business?.phone ?: ""}",
                                    fontSize = 12.sp,
                                    color = KarigorSlateLight
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Surface(
                                    color = KarigorEmeraldDark,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "চালান / INVOICE",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = sale.invoiceNumber,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KarigorEmeraldPrimary
                                )
                                Text(
                                    text = dateFormatted,
                                    fontSize = 10.sp,
                                    color = KarigorSlateLight
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = KarigorBorder)
                        Spacer(modifier = Modifier.height(14.dp))

                        // Customer Details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = if (isBn) "ক্রেতার বিবরণ (Bill To):" else "Billed To:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KarigorSlateLight
                                )
                                Text(
                                    text = sale.customerName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KarigorNavy
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (isBn) "মূল্য পরিশোধের অবস্থা:" else "Payment Status:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KarigorSlateLight
                                )
                                Surface(
                                    color = KarigorEmeraldContainer,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "পরিশোধিত (${sale.paymentMethod})",
                                        color = KarigorEmeraldDark,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Financial Breakdown Table
                        Surface(
                            color = KarigorSurfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = if (isBn) "বিবরণ" else "Description", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = KarigorNavy)
                                Text(text = if (isBn) "মোট টাকা" else "Total (BDT)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = KarigorNavy)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "পণ্য বিক্রয় ও সার্ভিস বাবদ মোট",
                                fontSize = 13.sp,
                                color = KarigorNavy,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "$currency${String.format("%,.2f", sale.subtotal)}",
                                fontSize = 13.sp,
                                color = KarigorNavy,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        if (sale.discount > 0) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp, horizontal = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = if (isBn) "বিশেষ ছাড় (Discount)" else "Discount", fontSize = 12.sp, color = KarigorError)
                                Text(text = "-$currency${String.format("%,.2f", sale.discount)}", fontSize = 12.sp, color = KarigorError, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = KarigorBorder)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Grand Total
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isBn) "সর্বমোট প্রদেয়:" else "Grand Total:",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = KarigorEmeraldDark
                            )
                            Text(
                                text = "$currency${String.format("%,.2f", sale.total)}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = KarigorEmeraldDark
                            )
                        }

                        if (sale.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "বিশেষ দ্রষ্টব্য: ${sale.notes}",
                                fontSize = 11.sp,
                                color = KarigorSlateLight,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Footer watermark
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "কারিগর দ্বারা পরিচালিত • Powered by Karigor",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = KarigorEmeraldPrimary
                            )
                            Text(
                                text = "“বাংলাদেশের ব্যবসার এক স্মার্ট ঠিকানা।”",
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
