package com.example.karigor.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.karigor.data.local.ProductEntity
import com.example.karigor.data.model.AppLanguage
import com.example.karigor.ui.ChatMessage
import com.example.karigor.ui.theme.*

@Composable
fun AiAssistantScreen(
    language: AppLanguage,
    products: List<ProductEntity>,
    chatMessages: List<ChatMessage>,
    isAiThinking: Boolean,
    marketingResult: String,
    isMarketingThinking: Boolean,
    onSendMessage: (String) -> Unit,
    onGenerateMarketing: (String, String, Double, String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Business Advisor, 1: Marketing Copy
    var chatInput by remember { mutableStateOf("") }
    val chatListState = rememberLazyListState()
    val context = LocalContext.current

    val isBn = language == AppLanguage.BANGLA

    // Marketing Form State
    var selectedProduct by remember { mutableStateOf<ProductEntity?>(products.firstOrNull()) }
    var customProductName by remember { mutableStateOf(products.firstOrNull()?.name ?: "পাঞ্জাবি") }
    var customCategory by remember { mutableStateOf(products.firstOrNull()?.category ?: "পোশাক") }
    var customPrice by remember { mutableStateOf(products.firstOrNull()?.sellingPrice?.toString() ?: "1500") }
    var campaignGoal by remember { mutableStateOf("ঈদ স্পেশাল অফার ও ক্যাশ অন ডেলিভারি") }

    val quickQuestions = if (isBn) listOf(
        "আমার এই মাসে sales কেমন?",
        "কোন product বেশি বিক্রি হচ্ছে?",
        "কোন product-এর stock কম?",
        "আমার expense কোথায় বেশি?",
        "কীভাবে আমার sales বাড়াতে পারি?"
    ) else listOf(
        "How are my sales this month?",
        "Which product is selling best?",
        "Which product has low stock?",
        "Where is my highest expense?",
        "How can I increase my sales?"
    )

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            chatListState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KarigorBackgroundLight)
    ) {
        // AI Header
        Surface(
            color = KarigorEmeraldDark,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(KarigorGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = KarigorNavy)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isBn) "কারিগর এআই বিজনেস কো-পাইলট" else "Karigor AI Business Co-Pilot",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isBn) "আপনার ব্যবসার ব্যক্তিগত ডেটা-চালিত এআই সহকারী" else "Private AI powered by your verified store analytics",
                            color = KarigorEmeraldLight,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White.copy(alpha = 0.12f),
                    contentColor = KarigorGold,
                    indicator = {},
                    divider = {},
                    modifier = Modifier.clip(RoundedCornerShape(10.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = if (isBn) "বিজনেস উপদেষ্টা" else "Business Advisor",
                                color = if (selectedTab == 0) KarigorGold else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = if (isBn) "মার্কেটিং কন্টেন্ট জেনারেটর" else "Marketing Copy AI",
                                color = if (selectedTab == 1) KarigorGold else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }
        }

        if (selectedTab == 0) {
            // Business Advisor Chat
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                // Quick prompt suggestions
                Text(
                    text = if (isBn) "দ্রুত জিজ্ঞাস্য প্রশ্নসমূহ:" else "Quick Questions:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = KarigorSlateLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(quickQuestions) { q ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, KarigorBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onSendMessage(q) }
                        ) {
                            Text(
                                text = q,
                                fontSize = 11.sp,
                                color = KarigorEmeraldDark,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Chat Messages List
                LazyColumn(
                    state = chatListState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(chatMessages) { msg ->
                        val isUser = msg.sender == "user"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                        ) {
                            if (!isUser) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(KarigorEmeraldDark),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Psychology, contentDescription = null, tint = KarigorGold, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                            }

                            Surface(
                                shape = RoundedCornerShape(
                                    topStart = 14.dp,
                                    topEnd = 14.dp,
                                    bottomStart = if (isUser) 14.dp else 2.dp,
                                    bottomEnd = if (isUser) 2.dp else 14.dp
                                ),
                                color = if (isUser) KarigorEmeraldPrimary else Color.White,
                                shadowElevation = 1.dp,
                                modifier = Modifier.widthIn(max = 280.dp)
                            ) {
                                Text(
                                    text = msg.text,
                                    color = if (isUser) Color.White else KarigorNavy,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(12.dp),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    if (isAiThinking) {
                        item {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = KarigorEmeraldPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isBn) "কারিগর এআই আপনার ব্যবসার তথ্য বিশ্লেষণ করছে..." else "Analyzing authorized business metrics...",
                                    fontSize = 11.sp,
                                    color = KarigorSlateLight,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Chat Input Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = chatInput,
                        onValueChange = { chatInput = it },
                        placeholder = { Text(if (isBn) "আপনার প্রশ্ন লিখুন..." else "Ask your business question...", fontSize = 13.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_chat_input"),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = KarigorEmeraldPrimary,
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (chatInput.isNotBlank()) {
                                onSendMessage(chatInput)
                                chatInput = ""
                            }
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(KarigorEmeraldDark)
                            .testTag("ai_send_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        } else {
            // AI Marketing Copy Tab
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = if (isBn) "সোশ্যাল মিডিয়া ও বিজ্ঞাপন কন্টেন্ট জেনারেটর" else "Social Media & Ad Copy Generator",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = KarigorNavy
                    )
                    Text(
                        text = if (isBn) "ফেসবুক, ইনস্টাগ্রাম ও টিকটকে বিক্রয় বাড়াতে আকর্ষণীয় পোস্ট তৈরি করুন।" else "Generate high converting post captions with hashtags & offer copy.",
                        fontSize = 12.sp,
                        color = KarigorSlateLight
                    )
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Fast Product Selector
                            if (products.isNotEmpty()) {
                                Text(text = if (isBn) "আপনার পণ্য নির্বাচন করুন:" else "Select from your products:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(products) { prod ->
                                        val isSel = selectedProduct?.id == prod.id
                                        FilterChip(
                                            selected = isSel,
                                            onClick = {
                                                selectedProduct = prod
                                                customProductName = prod.name
                                                customCategory = prod.category
                                                customPrice = prod.sellingPrice.toString()
                                            },
                                            label = { Text(prod.name, fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = KarigorEmeraldPrimary,
                                                selectedLabelColor = Color.White
                                            )
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = customProductName,
                                onValueChange = { customProductName = it },
                                label = { Text(if (isBn) "পণ্যের নাম" else "Product Name") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = customCategory,
                                    onValueChange = { customCategory = it },
                                    label = { Text(if (isBn) "ক্যাটাগরি" else "Category") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = customPrice,
                                    onValueChange = { customPrice = it },
                                    label = { Text(if (isBn) "মূল্য (৳)" else "Price (৳)") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }

                            OutlinedTextField(
                                value = campaignGoal,
                                onValueChange = { campaignGoal = it },
                                label = { Text(if (isBn) "উপলক্ষ / অফার লক্ষ্য" else "Campaign Occasion / Goal") },
                                placeholder = { Text("ঈদ ধামাকা অফার, বৈশাখী মেলা, ইত্যাদি") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Button(
                                onClick = {
                                    val priceVal = customPrice.toDoubleOrNull() ?: 1000.0
                                    onGenerateMarketing(customProductName, customCategory, priceVal, campaignGoal)
                                },
                                enabled = !isMarketingThinking && customProductName.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = KarigorEmeraldDark),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("generate_marketing_btn")
                            ) {
                                if (isMarketingThinking) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "তৈরি হচ্ছে...")
                                } else {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = KarigorGold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = if (isBn) "এআই বিজ্ঞাপন কন্টেন্ট তৈরি করুন" else "Generate Ad Copy", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Generated Output
                if (marketingResult.isNotBlank()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isBn) "তৈরিকৃত সোশ্যাল মিডিয়া কন্টেন্ট" else "Generated Social Media Content",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = KarigorEmeraldDark
                                    )
                                    IconButton(
                                        onClick = {
                                            val sendIntent: Intent = Intent().apply {
                                                action = Intent.ACTION_SEND
                                                putExtra(Intent.EXTRA_TEXT, marketingResult)
                                                type = "text/plain"
                                            }
                                            context.startActivity(Intent.createChooser(sendIntent, "কপি বা শেয়ার করুন"))
                                        }
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = "Share", tint = KarigorEmeraldPrimary)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = KarigorBorder)
                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = marketingResult,
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp,
                                    color = KarigorNavy
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
