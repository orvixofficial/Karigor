package com.example.karigor.ui.screens

import androidx.compose.foundation.background
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
import com.example.karigor.data.model.AppLanguage
import com.example.karigor.ui.theme.*

data class WholesaleListing(
    val id: String,
    val title: String,
    val hubName: String,
    val location: String,
    val wholesalePrice: Double,
    val moq: String,
    val category: String,
    val verified: Boolean = true
)

@Composable
fun MarketplaceScreen(
    language: AppLanguage
) {
    val isBn = language == AppLanguage.BANGLA
    val currency = "৳"

    var activeRfqListing by remember { mutableStateOf<WholesaleListing?>(null) }
    var rfqSubmittedAlert by remember { mutableStateOf(false) }

    val listings = listOf(
        WholesaleListing("1", "১০০% প্রিমিয়াম সুতি ফেব্রিক থান (রোল)", "নারায়ণগঞ্জ টেক্সটাইল ডিরেক্ট", "নারায়ণগঞ্জ সদর", 320.0, "৫০ গজ (MOQ)", "ফেব্রিক ও সুতা"),
        WholesaleListing("2", "কাস্টম প্রিন্টেড শপিং ব্যাগ ও কার্টুন বক্স", "চকবাজার প্যাকেজিং ইন্ডাস্ট্রিজ", "চকবাজার, ঢাকা", 18.0, "১০০০ পিস (MOQ)", "প্যাকেজিং"),
        WholesaleListing("3", "এক্সপোর্ট কোয়ালিটি পোলো টি-শার্ট (লট)", "গাজীপুর অ্যাপারেলস হোলসেল", "কোনাবাড়ী, গাজীপুর", 280.0, "১০০ পিস (MOQ)", "তৈরি পোশাক"),
        WholesaleListing("4", "অর্গানিক বাসমতি ও মিনিকেট চাল (৫০ কেজি বস্তা)", "দিনাজপুর অটো রাইস মিলস", "দিনাজপুর", 3450.0, "১০ বস্তা (MOQ)", "মুদি ও খাদ্য"),
        WholesaleListing("5", "পোর্টেবল ব্লুটুথ থার্মাল পিওএস প্রিন্টার", "স্টেডিয়াম মার্কেট ইলেকট্রনিক্স হাব", "পল্টন, ঢাকা", 2600.0, "৫ পিস (MOQ)", "যন্ত্রপাতি")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KarigorBackgroundLight)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isBn) "কারিগর বি২বি পাইকারি মার্কেটপ্লেস" else "Karigor B2B Wholesale Marketplace",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isBn) "সরাসরি মিল ও প্রস্তুতকারক থেকে পাইকারি মূল্যে কাঁচামাল কিনুন" else "Source raw materials directly from verified manufacturers",
                            color = KarigorEmeraldLight,
                            fontSize = 12.sp
                        )
                    }
                    Surface(
                        color = KarigorGold,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "B2B NETWORK",
                            color = KarigorNavy,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Text(
            text = if (isBn) "যাচাইকৃত পাইকারি লিস্টিং (Verified Hubs)" else "Verified Wholesale Listings",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = KarigorNavy
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(listings) { item ->
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
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(item.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = KarigorNavy)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.Verified, contentDescription = "Verified", tint = KarigorEmeraldPrimary, modifier = Modifier.size(16.dp))
                                }
                                Text("${item.hubName} • ${item.location}", fontSize = 12.sp, color = KarigorSlateLight)
                            }

                            Surface(
                                color = KarigorEmeraldContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = item.category,
                                    color = KarigorEmeraldDark,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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
                                Text(
                                    text = "পাইকারি দর: $currency${String.format("%,.0f", item.wholesalePrice)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = KarigorEmeraldDark
                                )
                                Text(
                                    text = "ন্যূনতম অর্ডার: ${item.moq}",
                                    fontSize = 12.sp,
                                    color = KarigorSlateLight
                                )
                            }

                            FilledTonalButton(
                                onClick = { activeRfqListing = item },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = KarigorGold,
                                    contentColor = KarigorNavy
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.RequestQuote, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = if (isBn) "দরদাম / কোটেশন (RFQ)" else "Request Quote", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // RFQ Request Quote Dialog
    activeRfqListing?.let { listing ->
        var requestedQty by remember { mutableStateOf("100") }
        var merchantNote by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { activeRfqListing = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "কোটেশন রিকোয়েস্ট পাঠান (RFQ)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = KarigorEmeraldDark
                    )
                    Text(
                        text = "${listing.title} (${listing.hubName})",
                        fontSize = 12.sp,
                        color = KarigorSlateLight
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = requestedQty,
                        onValueChange = { requestedQty = it },
                        label = { Text("প্রয়োজনীয় পরিমাণ (MOQ: ${listing.moq})") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = merchantNote,
                        onValueChange = { merchantNote = it },
                        label = { Text("বিশেষ নির্দেশনা বা দরদামের বার্তা") },
                        placeholder = { Text("যেমন: নিয়মিত মাসিক সাপ্লাই প্রয়োজন...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            activeRfqListing = null
                            rfqSubmittedAlert = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = KarigorEmeraldDark),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("কোটেশন অনুরোধ প্রেরণ করুন", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (rfqSubmittedAlert) {
        AlertDialog(
            onDismissRequest = { rfqSubmittedAlert = false },
            confirmButton = {
                Button(
                    onClick = { rfqSubmittedAlert = false },
                    colors = ButtonDefaults.buttonColors(containerColor = KarigorEmeraldDark)
                ) {
                    Text("ঠিক আছে")
                }
            },
            title = { Text("কোটেশন রিকোয়েস্ট পাঠানো হয়েছে!", fontWeight = FontWeight.Bold, color = KarigorEmeraldDark) },
            text = { Text("আপনার প্রতিষ্ঠানের প্রোফাইল এবং চাহিদা সরাসরি সরবরাহকারীর কাছে পৌঁছে দেওয়া হয়েছে। তারা দ্রুত আপনার সাথে যোগাযোগ করবে।") }
        )
    }
}
