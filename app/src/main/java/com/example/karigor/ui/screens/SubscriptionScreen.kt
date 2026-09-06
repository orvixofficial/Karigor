package com.example.karigor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.karigor.data.local.BusinessEntity
import com.example.karigor.data.model.AppLanguage
import com.example.karigor.ui.theme.*

data class PricingPlan(
    val id: String,
    val name: String,
    val nameEn: String,
    val price: String,
    val billingCycle: String,
    val isPopular: Boolean,
    val features: List<String>
)

@Composable
fun SubscriptionScreen(
    language: AppLanguage,
    business: BusinessEntity?
) {
    val isBn = language == AppLanguage.BANGLA
    var selectedPlanId by remember { mutableStateOf(business?.subscriptionTier ?: "BUSINESS") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val plans = listOf(
        PricingPlan(
            "FREE",
            "ফ্রি ট্রায়াল (Free)",
            "Free Starter",
            "৳০",
            "আজীবন ফ্রি",
            false,
            listOf("১টি প্রতিষ্ঠান", "২৫টি পণ্য লিস্টিং", "সাধারণ পিওএস ক্যাশ বিক্রি", "বেসিক ইনভেন্টরি ট্র্যাকিং")
        ),
        PricingPlan(
            "STARTER",
            "স্টার্টার (Starter)",
            "Starter",
            "৳৭৫০",
            "প্রতি মাসে",
            false,
            listOf("১টি প্রতিষ্ঠান", "২৫০টি পণ্য লিস্টিং", "আনলিমিটেড চালান/ইনভয়েস", "লাইভ অনলাইন ই-কমার্স স্টোর", "বিকাশ ও নগদ পেমেন্ট রসিদ")
        ),
        PricingPlan(
            "BUSINESS",
            "বিজনেস (Business)",
            "Business Pro",
            "৳১,৯৫০",
            "প্রতি মাসে",
            true,
            listOf("৩টি প্রতিষ্ঠান ম্যানেজমেন্ট", "আনলিমিটেড পণ্য ও ইনভেন্টরি", "কারিগর এআই বিজনেস সহকারী", "এআই সোশ্যাল মার্কেটিং জেনারেটর", "বি২বি পাইকারি নেটওয়ার্ক অ্যাক্সেস")
        ),
        PricingPlan(
            "PRO",
            "প্রো / এন্টারপ্রাইজ",
            "Enterprise",
            "৳৪,৫০০",
            "প্রতি মাসে",
            false,
            listOf("আনলিমিটেড প্রতিষ্ঠান", "মাল্টি-ইউজার রোলস ও পারমিশন", "অগ্রাধিকারমূলক বি২বি সাপোর্ট", "ডেডিকেটেড একাউন্ট ম্যানেজার", "কাস্টম ইআরপি ইন্টিগ্রেশন")
        )
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
                Text(
                    text = if (isBn) "কারিগর সাবস্ক্রিপশন ও প্যাকেজ" else "Karigor SaaS Subscription",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isBn) "আপনার ব্যবসার আকার অনুযায়ী সেরা ক্লাউড প্যাকেজ বেছে নিন" else "Choose the ideal cloud plan to scale your SME",
                    color = KarigorEmeraldLight,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "বর্তমান সক্রিয় প্ল্যান: ${business?.subscriptionTier ?: "BUSINESS"}",
                    color = KarigorGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(plans) { plan ->
                val isCurrent = selectedPlanId == plan.id

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (plan.isPopular) Color.White else Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (plan.isPopular) 4.dp else 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (plan.isPopular) Modifier.border(2.dp, KarigorGold, RoundedCornerShape(16.dp))
                            else Modifier
                        )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isBn) plan.name else plan.nameEn,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = KarigorNavy
                                )
                                Text(
                                    text = "${plan.price} / ${plan.billingCycle}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp,
                                    color = KarigorEmeraldDark
                                )
                            }

                            if (plan.isPopular) {
                                Surface(
                                    color = KarigorGold,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = KarigorNavy, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "সর্বাধিক জনপ্রিয়", color = KarigorNavy, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = KarigorBorder)
                        Spacer(modifier = Modifier.height(10.dp))

                        plan.features.forEach { feature ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = KarigorEmeraldPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(feature, fontSize = 12.sp, color = KarigorNavy)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                selectedPlanId = plan.id
                                showSuccessDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCurrent) KarigorEmeraldContainer else KarigorEmeraldDark,
                                contentColor = if (isCurrent) KarigorEmeraldDark else Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isCurrent) (if (isBn) "বর্তমানে সক্রিয়" else "Current Plan") else (if (isBn) "প্ল্যান পরিবর্তন করুন" else "Select Plan"),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            confirmButton = {
                Button(
                    onClick = { showSuccessDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = KarigorEmeraldDark)
                ) {
                    Text("ঠিক আছে")
                }
            },
            title = { Text("প্ল্যান আপডেট সম্পন্ন হয়েছে", fontWeight = FontWeight.Bold, color = KarigorEmeraldDark) },
            text = { Text("আপনার প্রতিষ্ঠানের সাবস্ক্রিপশন সফলভাবে আপডেট করা হয়েছে।") }
        )
    }
}
