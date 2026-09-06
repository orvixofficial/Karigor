package com.example.karigor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.karigor.data.model.AppLanguage
import com.example.karigor.ui.theme.*

@Composable
fun LandingScreen(
    language: AppLanguage,
    onStartClick: () -> Unit,
    onDemoClick: () -> Unit
) {
    val isBn = language == AppLanguage.BANGLA

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(KarigorBackgroundLight)
    ) {
        // Hero Section
        item {
            Surface(
                color = KarigorEmeraldDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(KarigorGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "কা",
                            color = KarigorNavy,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "কারিগর",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )

                    Text(
                        text = "KARIGOR CLOUD BUSINESS PLATFORM",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = KarigorGold,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "“বাংলাদেশের ব্যবসার এক স্মার্ট ঠিকানা।”",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Build. Manage. Grow.",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = KarigorEmeraldLight
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "এক জায়গা থেকে আপনার ব্যবসার পণ্য, বিক্রি, ইনভেন্টরি, গ্রাহক, চালান (Invoice) এবং ব্যবসায়িক পারফরম্যান্স পরিচালনা করুন সম্পূর্ণ আধুনিক ক্লাউডে।",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onStartClick,
                            colors = ButtonDefaults.buttonColors(containerColor = KarigorGold, contentColor = KarigorNavy),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("landing_start_btn")
                        ) {
                            Text(text = if (isBn) "শুরু করুন" else "Get Started", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        OutlinedButton(
                            onClick = onDemoClick,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("landing_demo_btn")
                        ) {
                            Text(text = if (isBn) "ডেমো দেখুন" else "View Demo", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }

        // Key Value Propositions
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (isBn) "কেন কারিগর বেছে নিবেন?" else "Why Choose Karigor?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = KarigorNavy
                )

                FeatureCard(
                    icon = Icons.Default.PointOfSale,
                    title = if (isBn) "স্মার্ট পিওএস ও ডিজিটাল চালান" else "Fast POS & Instant Invoicing",
                    desc = if (isBn) "দোকানে বা অনলাইনে তাৎক্ষণিক বিক্রি করুন, ছাড় হিসাব করুন এবং গ্রাহককে প্রফেশনাল ডিজিটাল চালান শেয়ার করুন।" else "Process cash/bKash sales instantly and generate professional branded invoices.",
                    iconBg = KarigorEmeraldContainer,
                    iconTint = KarigorEmeraldDark
                )

                FeatureCard(
                    icon = Icons.Default.Warehouse,
                    title = if (isBn) "স্বয়ংক্রিয় স্টক ও ইনভেন্টরি ট্র্যাকিং" else "Real-time Cloud Inventory",
                    desc = if (isBn) "পণ্য বিক্রি হলেই স্টক স্বয়ংক্রিয়ভাবে আপডেট হয় এবং স্টক শেষ হওয়ার আগেই সতর্কবার্তা পাবেন।" else "Automatic stock deductions and proactive low stock warning alerts.",
                    iconBg = KarigorGoldContainer,
                    iconTint = KarigorGoldDark
                )

                FeatureCard(
                    icon = Icons.Default.Storefront,
                    title = if (isBn) "রেডিমেড অনলাইন ই-কমার্স স্টোর" else "Instant Online Storefront",
                    desc = if (isBn) "কোনো কোডিং ছাড়াই কয়েক ক্লিকেই তৈরি করুন আপনার ব্র্যান্ডেড অনলাইন শপ এবং অর্ডার গ্রহণ করুন।" else "Launch your public digital store in minutes and manage incoming web orders.",
                    iconBg = Color(0xFFE0F2FE),
                    iconTint = Color(0xFF0369A1)
                )

                FeatureCard(
                    icon = Icons.Default.Psychology,
                    title = if (isBn) "কারিগর এআই বিজনেস সহকারী" else "Private AI Business Co-Pilot",
                    desc = if (isBn) "আপনার নিজের ব্যবসার রিয়েল ডেটা বিশ্লেষণ করে এআই দেবে বিক্রয় বাড়ানোর পরামর্শ ও সোশ্যাল মিডিয়া মার্কেটিং কন্টেন্ট।" else "Data-driven business advisory and high-converting marketing copywriting.",
                    iconBg = Color(0xFFF3E8FF),
                    iconTint = Color(0xFF7E22CE)
                )
            }
        }

        // Sectors
        item {
            Surface(
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (isBn) "বাংলাদেশের প্রতিটি ব্যবসার জন্য উপযুক্ত" else "Built for Bangladesh SME Sectors",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = KarigorNavy
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    val sectors = listOf(
                        "👗 ফ্যাশন ও বুটিক হাউস" to "পোশাক, শাড়ি, পাঞ্জাবি ও জুয়েলারি",
                        "🛒 সুপারশপ ও মুদি দোকান" to "দৈনন্দিন নিত্যপ্রয়োজনীয় পণ্য ও গ্রোসারি",
                        "📱 ইলেকট্রনিক্স ও মোবাইল শপ" to "গ্যাজেট ও ডিজিটাল এক্সেসরিজ",
                        "📦 অনলাইন শপ ও এফ-কমার্স" to "ফেসবুক ও সোশ্যাল মিডিয়া উদ্যোক্তা",
                        "🏭 পাইকারি ও সাপ্লায়ার প্রতিষ্ঠান" to "বি২বি কাঁচামাল ও ম্যানুফ্যাকচারিং"
                    )

                    sectors.forEach { (title, sub) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = KarigorEmeraldPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = KarigorNavy)
                                Text(sub, fontSize = 11.sp, color = KarigorSlateLight)
                            }
                        }
                    }
                }
            }
        }

        // CTA Footer
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onStartClick,
                    colors = ButtonDefaults.buttonColors(containerColor = KarigorEmeraldDark),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isBn) "কারিগর ড্যাশবোর্ডে প্রবেশ করুন" else "Launch Karigor Dashboard",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "কারিগর — বাংলাদেশের ব্যবসার এক স্মার্ট ঠিকানা।",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = KarigorEmeraldDark
                )
                Text(
                    text = "© 2026 Karigor Cloud Platform. All rights reserved.",
                    fontSize = 11.sp,
                    color = KarigorSlateLight
                )
            }
        }
    }
}

@Composable
private fun FeatureCard(
    icon: ImageVector,
    title: String,
    desc: String,
    iconBg: Color,
    iconTint: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = KarigorNavy)
                Spacer(modifier = Modifier.height(4.dp))
                Text(desc, fontSize = 12.sp, color = KarigorSlateLight, lineHeight = 17.sp)
            }
        }
    }
}
