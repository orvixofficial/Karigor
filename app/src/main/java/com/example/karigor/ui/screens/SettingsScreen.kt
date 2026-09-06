package com.example.karigor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.karigor.data.local.BusinessEntity
import com.example.karigor.data.model.AppLanguage
import com.example.karigor.ui.theme.*

@Composable
fun SettingsScreen(
    language: AppLanguage,
    business: BusinessEntity?,
    onToggleLanguage: () -> Unit,
    onNewBusinessClick: () -> Unit
) {
    val isBn = language == AppLanguage.BANGLA

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(KarigorBackgroundLight)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = if (isBn) "সেটিংস ও প্রতিষ্ঠান প্রোফাইল" else "Settings & Profile",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = KarigorNavy
            )
        }

        // Business Profile Details
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isBn) "ডিজিটাল ব্যবসা তথ্য" else "Business Profile",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = KarigorEmeraldDark
                    )
                    HorizontalDivider(color = KarigorBorder)

                    SettingRow("প্রতিষ্ঠানের নাম", business?.name ?: "N/A")
                    SettingRow("মালিকের নাম", business?.ownerName ?: "N/A")
                    SettingRow("ব্যবসার ধরন / ক্যাটাগরি", business?.category ?: "Retail")
                    SettingRow("মোবাইল নম্বর", business?.phone ?: "N/A")
                    SettingRow("ঠিকানা", business?.address ?: "বাংলাদেশ")
                    SettingRow("অনলাইন স্টোর স্লাগ", "/store/${business?.slug ?: "shop"}")
                    SettingRow("কারেন্সি", "BDT (৳)")
                    SettingRow("সাবস্ক্রিপশন স্ট্যাটাস", "${business?.subscriptionTier ?: "BUSINESS"} (Active)")

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onNewBusinessClick,
                        colors = ButtonDefaults.buttonColors(containerColor = KarigorEmeraldDark),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AddBusiness, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isBn) "+ নতুন প্রতিষ্ঠান যোগ করুন" else "+ Add Another Business")
                    }
                }
            }
        }

        // App Preferences
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isBn) "অ্যাপ পছন্দসমূহ" else "App Preferences",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = KarigorEmeraldDark
                    )
                    HorizontalDivider(color = KarigorBorder)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("অ্যাপের ভাষা (Language)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text(if (isBn) "বর্তমান ভাষা: বাংলা" else "Current: English", fontSize = 12.sp, color = KarigorSlateLight)
                        }

                        FilledTonalButton(
                            onClick = onToggleLanguage,
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = KarigorEmeraldContainer, contentColor = KarigorEmeraldDark)
                        ) {
                            Text(if (isBn) "Switch to English" else "বাংলায় পরিবর্তন করুন")
                        }
                    }
                }
            }
        }

        // Cloud Security & Platform
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "ক্লাউড প্ল্যাটফর্ম ও ডাটা নিরাপত্তা",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = KarigorEmeraldDark
                    )
                    HorizontalDivider(color = KarigorBorder)

                    Text("• মাল্টি-টেন্যান্ট সিকিউরিটি ও আইসোলেশন সক্রিয়", fontSize = 12.sp, color = KarigorNavy)
                    Text("• অফলাইন-ফার্স্ট ক্যাশিং ও অটো ক্লাউড সিঙ্ক রেডি", fontSize = 12.sp, color = KarigorNavy)
                    Text("• জেমিনি এআই প্রাইভেট বিজনেস এনালিটিক্স রেডি", fontSize = 12.sp, color = KarigorNavy)

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "কারিগর সংস্করণ: v2.4 (SaaS Super Platform)\n“বাংলাদেশের ব্যবসার এক স্মার্ট ঠিকানা।”",
                        fontSize = 11.sp,
                        color = KarigorSlateLight,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = KarigorSlateLight)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = KarigorNavy)
    }
}
