package com.example.karigor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.karigor.data.local.BusinessEntity
import com.example.karigor.data.model.AppLanguage
import com.example.karigor.data.model.AppStrings
import com.example.karigor.ui.Screen
import com.example.karigor.ui.theme.*

data class NavItem(
    val screen: Screen,
    val titleKey: String,
    val icon: ImageVector
)

@Composable
fun KarigorDrawerContent(
    currentScreen: Screen,
    activeBusiness: BusinessEntity?,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit,
    onCloseDrawer: () -> Unit
) {
    val navItems = listOf(
        NavItem(Screen.DASHBOARD, "dashboard", Icons.Default.Dashboard),
        NavItem(Screen.PRODUCTS, "products", Icons.Default.Inventory2),
        NavItem(Screen.INVENTORY, "inventory", Icons.Default.Warehouse),
        NavItem(Screen.SALES, "sales", Icons.Default.PointOfSale),
        NavItem(Screen.CUSTOMERS, "customers", Icons.Default.People),
        NavItem(Screen.EXPENSES, "expenses", Icons.Default.ReceiptLong),
        NavItem(Screen.INVOICES, "invoices", Icons.Default.Description),
        NavItem(Screen.ONLINE_STORE, "online_store", Icons.Default.Storefront),
        NavItem(Screen.AI_ASSISTANT, "ai_tools", Icons.Default.Psychology),
        NavItem(Screen.SUPPLIERS, "suppliers", Icons.Default.LocalShipping),
        NavItem(Screen.MARKETPLACE, "marketplace", Icons.Default.ShoppingBag),
        NavItem(Screen.SUBSCRIPTION, "subscription", Icons.Default.CardMembership),
        NavItem(Screen.SETTINGS, "settings", Icons.Default.Settings)
    )

    ModalDrawerSheet(
        drawerContainerColor = KarigorSurfaceLight,
        modifier = Modifier.width(300.dp)
    ) {
        // Drawer Header with Bengali motif
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(KarigorEmeraldDark)
                .padding(top = 40.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(KarigorGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "কা",
                            color = KarigorNavy,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "কারিগর",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Cloud Business Platform",
                            color = KarigorEmeraldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    color = Color.White.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = activeBusiness?.name ?: "Business Tenant",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "${activeBusiness?.category ?: "Retail"} • BDT (৳)",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                        Text(
                            text = "Slug: /store/${activeBusiness?.slug ?: "shop"}",
                            color = KarigorGold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Navigation Items
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 12.dp)
        ) {
            items(navItems.size) { index ->
                val item = navItems[index]
                val isSelected = currentScreen == item.screen
                val title = AppStrings.get(item.titleKey, language)

                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = title,
                            tint = if (isSelected) KarigorEmeraldDark else KarigorSlateLight
                        )
                    },
                    label = {
                        Text(
                            text = title,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp,
                            color = if (isSelected) KarigorEmeraldDark else KarigorNavy
                        )
                    },
                    selected = isSelected,
                    onClick = {
                        onNavigate(item.screen)
                        onCloseDrawer()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = KarigorEmeraldContainer,
                        unselectedContainerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .padding(vertical = 2.dp)
                        .testTag("nav_item_${item.screen.name.lowercase()}")
                )
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Landing",
                            tint = if (currentScreen == Screen.LANDING) KarigorEmeraldDark else KarigorSlateLight
                        )
                    },
                    label = {
                        Text(
                            text = if (language == AppLanguage.BANGLA) "কারিগর ওয়েবসাইট / ল্যান্ডিং" else "Karigor Landing Page",
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = KarigorNavy
                        )
                    },
                    selected = currentScreen == Screen.LANDING,
                    onClick = {
                        onNavigate(Screen.LANDING)
                        onCloseDrawer()
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("nav_landing_item")
                )
            }
        }
    }
}
