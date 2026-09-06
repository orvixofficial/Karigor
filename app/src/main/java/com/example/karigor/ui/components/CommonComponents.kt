package com.example.karigor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.karigor.data.local.BusinessEntity
import com.example.karigor.data.model.AppLanguage
import com.example.karigor.data.model.AppStrings
import com.example.karigor.ui.Screen
import com.example.karigor.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KarigorTopBar(
    currentScreen: Screen,
    language: AppLanguage,
    activeBusiness: BusinessEntity?,
    allBusinesses: List<BusinessEntity>,
    unreadNotificationCount: Int,
    onMenuClick: () -> Unit,
    onLanguageToggle: () -> Unit,
    onNotificationsClick: () -> Unit,
    onBusinessSelect: (String) -> Unit,
    onNewBusinessClick: () -> Unit,
    onLandingClick: () -> Unit,
    onStorefrontClick: () -> Unit
) {
    var showBusinessMenu by remember { mutableStateOf(false) }

    Surface(
        color = KarigorEmeraldDark,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = onMenuClick,
                        modifier = Modifier.testTag("menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color.White
                        )
                    }

                    // Brand & Business Name
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showBusinessMenu = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "কারিগর",
                                color = KarigorGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                            Text(
                                text = " • Karigor",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = activeBusiness?.name ?: "লোড হচ্ছে...",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Switch business",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showBusinessMenu,
                            onDismissRequest = { showBusinessMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = if (language == AppLanguage.BANGLA) "প্রতিষ্ঠান নির্বাচন করুন" else "Select Business",
                                        fontWeight = FontWeight.Bold,
                                        color = KarigorEmeraldDark
                                    )
                                },
                                onClick = {},
                                enabled = false
                            )
                            allBusinesses.forEach { biz ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(biz.name, fontWeight = FontWeight.SemiBold)
                                            Text(biz.category, fontSize = 11.sp, color = Color.Gray)
                                        }
                                    },
                                    onClick = {
                                        onBusinessSelect(biz.id)
                                        showBusinessMenu = false
                                    },
                                    leadingIcon = {
                                        if (biz.id == activeBusiness?.id) {
                                            Icon(Icons.Default.Check, contentDescription = "Active", tint = KarigorEmeraldPrimary)
                                        }
                                    }
                                )
                            }
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "+ ${AppStrings.get("new_business", language)}",
                                        color = KarigorEmeraldPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                onClick = {
                                    showBusinessMenu = false
                                    onNewBusinessClick()
                                }
                            )
                        }
                    }
                }

                // Action icons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Online Store Shortcut
                    IconButton(
                        onClick = onStorefrontClick,
                        modifier = Modifier.testTag("storefront_top_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = "Storefront",
                            tint = if (currentScreen == Screen.STOREFRONT_PREVIEW) KarigorGold else Color.White
                        )
                    }

                    // Notifications with badge
                    IconButton(
                        onClick = onNotificationsClick,
                        modifier = Modifier.testTag("notifications_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotificationCount > 0) {
                                    Badge(
                                        containerColor = KarigorError,
                                        contentColor = Color.White
                                    ) {
                                        Text(text = "$unreadNotificationCount")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White
                            )
                        }
                    }

                    // Language toggle pill
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onLanguageToggle() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("lang_toggle_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Language",
                                tint = KarigorGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (language == AppLanguage.BANGLA) "বাংলা" else "EN",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Landing page toggle
                    IconButton(
                        onClick = onLandingClick,
                        modifier = Modifier.testTag("landing_toggle_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Landing",
                            tint = if (currentScreen == Screen.LANDING) KarigorGold else Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    color = KarigorSlateLight,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = KarigorNavy
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = KarigorSlateLight
            )
        }
    }
}
