package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.karigor.data.local.KarigorDatabase
import com.example.karigor.data.repository.KarigorRepository
import com.example.karigor.ui.*
import com.example.karigor.ui.components.*
import com.example.karigor.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var repository: KarigorRepository
    private lateinit var viewModel: KarigorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = KarigorDatabase.getDatabase(applicationContext)
        repository = KarigorRepository(database, lifecycleScope)
        viewModel = KarigorViewModel(application, repository)

        setContent {
            MyApplicationTheme {
                KarigorMainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun KarigorMainApp(viewModel: KarigorViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()
    val activeBusiness by viewModel.activeBusiness.collectAsStateWithLifecycle()
    val allBusinesses by viewModel.allBusinesses.collectAsStateWithLifecycle()

    val products by viewModel.products.collectAsStateWithLifecycle()
    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val sales by viewModel.sales.collectAsStateWithLifecycle()
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    val inventoryLogs by viewModel.inventoryLogs.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val suppliers by viewModel.suppliers.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()

    // POS Cart & Checkout states
    val posCart by viewModel.posCart.collectAsStateWithLifecycle()
    val selectedPosCustomer by viewModel.selectedPosCustomer.collectAsStateWithLifecycle()
    val posDiscount by viewModel.posDiscount.collectAsStateWithLifecycle()
    val posPaymentMethod by viewModel.posPaymentMethod.collectAsStateWithLifecycle()

    // AI & Marketing states
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isAiThinking by viewModel.isAiThinking.collectAsStateWithLifecycle()
    val marketingResult by viewModel.marketingResult.collectAsStateWithLifecycle()
    val isMarketingThinking by viewModel.isMarketingThinking.collectAsStateWithLifecycle()

    // Dialogs & Modals
    val showOnboardingDialog by viewModel.showOnboardingDialog.collectAsStateWithLifecycle()
    val showAddProductDialog by viewModel.showAddProductDialog.collectAsStateWithLifecycle()
    val showNewSaleDialog by viewModel.showNewSaleDialog.collectAsStateWithLifecycle()
    val showAddExpenseDialog by viewModel.showAddExpenseDialog.collectAsStateWithLifecycle()
    val showAddCustomerDialog by viewModel.showAddCustomerDialog.collectAsStateWithLifecycle()
    val adjustStockProduct by viewModel.showAdjustStockDialog.collectAsStateWithLifecycle()
    val showAddSupplierDialog by viewModel.showAddSupplierDialog.collectAsStateWithLifecycle()
    val showNotificationsSheet by viewModel.showNotificationsSheet.collectAsStateWithLifecycle()
    val selectedInvoice by viewModel.selectedInvoice.collectAsStateWithLifecycle()

    val unreadNotifs = notifications.count { !it.isRead }

    // Back handling: If in storefront preview or other subscreen, return to dashboard
    BackHandler(enabled = currentScreen != Screen.DASHBOARD && currentScreen != Screen.LANDING) {
        viewModel.navigateTo(Screen.DASHBOARD)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = currentScreen != Screen.STOREFRONT_PREVIEW,
        drawerContent = {
            KarigorDrawerContent(
                currentScreen = currentScreen,
                activeBusiness = activeBusiness,
                language = language,
                onNavigate = { screen ->
                    viewModel.navigateTo(screen)
                },
                onCloseDrawer = {
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                if (currentScreen != Screen.STOREFRONT_PREVIEW) {
                    KarigorTopBar(
                        currentScreen = currentScreen,
                        language = language,
                        activeBusiness = activeBusiness,
                        allBusinesses = allBusinesses,
                        unreadNotificationCount = unreadNotifs,
                        onMenuClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        },
                        onLanguageToggle = { viewModel.toggleLanguage() },
                        onNotificationsClick = { viewModel.showNotificationsSheet.value = true },
                        onBusinessSelect = { bizId -> viewModel.selectBusiness(bizId) },
                        onNewBusinessClick = { viewModel.showOnboardingDialog.value = true },
                        onLandingClick = {
                            if (currentScreen == Screen.LANDING) {
                                viewModel.navigateTo(Screen.DASHBOARD)
                            } else {
                                viewModel.navigateTo(Screen.LANDING)
                            }
                        },
                        onStorefrontClick = { viewModel.navigateTo(Screen.STOREFRONT_PREVIEW) }
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentScreen) {
                    Screen.LANDING -> LandingScreen(
                        language = language,
                        onStartClick = { viewModel.navigateTo(Screen.DASHBOARD) },
                        onDemoClick = { viewModel.navigateTo(Screen.STOREFRONT_PREVIEW) }
                    )

                    Screen.DASHBOARD -> DashboardScreen(
                        language = language,
                        products = products,
                        sales = sales,
                        expensesTotal = expenses.sumOf { it.amount },
                        orderCount = orders.size,
                        customerCount = customers.size,
                        onNavigate = { viewModel.navigateTo(it) },
                        onNewSaleClick = { viewModel.showNewSaleDialog.value = true },
                        onAddProductClick = { viewModel.showAddProductDialog.value = true },
                        onRecordExpenseClick = { viewModel.showAddExpenseDialog.value = true },
                        onInvoiceClick = { sale -> viewModel.selectedInvoice.value = sale }
                    )

                    Screen.PRODUCTS -> ProductsScreen(
                        language = language,
                        products = products,
                        onAddProductClick = { viewModel.showAddProductDialog.value = true },
                        onAdjustStockClick = { prod -> viewModel.showAdjustStockDialog.value = prod },
                        onDeleteProduct = { prod -> viewModel.deleteProduct(prod) }
                    )

                    Screen.INVENTORY -> InventoryScreen(
                        language = language,
                        products = products,
                        inventoryLogs = inventoryLogs,
                        onAdjustStockClick = { prod -> viewModel.showAdjustStockDialog.value = prod }
                    )

                    Screen.SALES -> SalesScreen(
                        language = language,
                        sales = sales,
                        onNewSaleClick = { viewModel.showNewSaleDialog.value = true },
                        onInvoiceClick = { sale -> viewModel.selectedInvoice.value = sale }
                    )

                    Screen.CUSTOMERS -> CustomersScreen(
                        language = language,
                        customers = customers,
                        sales = sales,
                        onAddCustomerClick = { viewModel.showAddCustomerDialog.value = true },
                        onDeleteCustomer = { cust -> viewModel.deleteCustomer(cust) }
                    )

                    Screen.EXPENSES -> ExpensesScreen(
                        language = language,
                        expenses = expenses,
                        onAddExpenseClick = { viewModel.showAddExpenseDialog.value = true },
                        onDeleteExpense = { exp -> viewModel.deleteExpense(exp) }
                    )

                    Screen.INVOICES -> InvoicesScreen(
                        language = language,
                        sales = sales,
                        onInvoiceClick = { sale -> viewModel.selectedInvoice.value = sale }
                    )

                    Screen.ONLINE_STORE -> OnlineStoreScreen(
                        language = language,
                        business = activeBusiness,
                        orders = orders,
                        onPreviewStorefront = { viewModel.navigateTo(Screen.STOREFRONT_PREVIEW) },
                        onUpdateOrderStatus = { orderId, status -> viewModel.updateOrderStatus(orderId, status) }
                    )

                    Screen.STOREFRONT_PREVIEW -> StorefrontPreviewScreen(
                        language = language,
                        business = activeBusiness,
                        products = products,
                        onBack = { viewModel.navigateTo(Screen.ONLINE_STORE) },
                        onPlaceOrder = { name, phone, addr, summary, total, notes ->
                            viewModel.placeStorefrontOrder(name, phone, addr, summary, total, notes) {
                                // Handled in preview dialog
                            }
                        }
                    )

                    Screen.AI_ASSISTANT -> AiAssistantScreen(
                        language = language,
                        products = products,
                        chatMessages = chatMessages,
                        isAiThinking = isAiThinking,
                        marketingResult = marketingResult,
                        isMarketingThinking = isMarketingThinking,
                        onSendMessage = { query -> viewModel.askAiAssistant(query) },
                        onGenerateMarketing = { pName, cat, price, goal ->
                            viewModel.generateMarketingContent(pName, cat, price, goal)
                        }
                    )

                    Screen.SUPPLIERS -> SuppliersScreen(
                        language = language,
                        suppliers = suppliers,
                        onAddSupplierClick = { viewModel.showAddSupplierDialog.value = true },
                        onDeleteSupplier = { sup -> viewModel.deleteSupplier(sup) }
                    )

                    Screen.MARKETPLACE -> MarketplaceScreen(
                        language = language
                    )

                    Screen.SUBSCRIPTION -> SubscriptionScreen(
                        language = language,
                        business = activeBusiness
                    )

                    Screen.SETTINGS -> SettingsScreen(
                        language = language,
                        business = activeBusiness,
                        onToggleLanguage = { viewModel.toggleLanguage() },
                        onNewBusinessClick = { viewModel.showOnboardingDialog.value = true }
                    )
                }
            }
        }
    }

    // Modal Dialogs Handling
    if (showNewSaleDialog) {
        NewSaleDialog(
            language = language,
            products = products,
            customers = customers,
            cartItems = posCart,
            selectedCustomer = selectedPosCustomer,
            discount = posDiscount,
            paymentMethod = posPaymentMethod,
            onAddToCart = { prod -> viewModel.addProductToPosCart(prod) },
            onRemoveFromCart = { prod -> viewModel.removePosCartItem(prod) },
            onCustomerSelect = { cust -> viewModel.selectedPosCustomer.value = cust },
            onDiscountChange = { disc -> viewModel.posDiscount.value = disc },
            onPaymentMethodChange = { method -> viewModel.posPaymentMethod.value = method },
            onDismiss = {
                viewModel.clearPosCart()
                viewModel.showNewSaleDialog.value = false
            },
            onSubmitSale = { customerName, notes ->
                viewModel.submitPosSale(customerName, notes) { invoiceNo ->
                    val createdSale = sales.find { it.invoiceNumber == invoiceNo }
                    if (createdSale != null) {
                        viewModel.selectedInvoice.value = createdSale
                    }
                }
            }
        )
    }

    if (showAddProductDialog) {
        AddProductDialog(
            language = language,
            onDismiss = { viewModel.showAddProductDialog.value = false },
            onConfirm = { name, sku, category, sell, cost, stock, threshold ->
                viewModel.addProduct(name, sku, category, sell, cost, stock, threshold)
            }
        )
    }

    adjustStockProduct?.let { prod ->
        AdjustStockDialog(
            language = language,
            product = prod,
            onDismiss = { viewModel.showAdjustStockDialog.value = null },
            onConfirm = { changeAmt, reason ->
                viewModel.adjustStock(prod.id, changeAmt, reason)
            }
        )
    }

    if (showAddExpenseDialog) {
        AddExpenseDialog(
            language = language,
            onDismiss = { viewModel.showAddExpenseDialog.value = false },
            onConfirm = { category, amount, date, notes ->
                viewModel.addExpense(category, amount, date, notes)
            }
        )
    }

    if (showAddCustomerDialog) {
        AddCustomerDialog(
            language = language,
            onDismiss = { viewModel.showAddCustomerDialog.value = false },
            onConfirm = { name, phone, email, addr, notes ->
                viewModel.addCustomer(name, phone, email, addr, notes)
            }
        )
    }

    if (showAddSupplierDialog) {
        AddSupplierDialog(
            language = language,
            onDismiss = { viewModel.showAddSupplierDialog.value = false },
            onConfirm = { name, contact, phone, cat, addr, supplied ->
                viewModel.addSupplier(name, contact, phone, cat, addr, supplied)
            }
        )
    }

    if (showOnboardingDialog) {
        OnboardingBusinessDialog(
            language = language,
            onDismiss = { viewModel.showOnboardingDialog.value = false },
            onConfirm = { name, cat, owner, phone, email, addr, desc ->
                viewModel.createNewBusiness(name, cat, owner, phone, email, addr, desc)
            }
        )
    }

    if (showNotificationsSheet) {
        NotificationsDialog(
            language = language,
            notifications = notifications,
            onDismiss = { viewModel.showNotificationsSheet.value = false },
            onMarkAllRead = { viewModel.markAllNotificationsRead() }
        )
    }

    selectedInvoice?.let { sale ->
        InvoiceDialog(
            language = language,
            sale = sale,
            business = activeBusiness,
            onDismiss = { viewModel.selectedInvoice.value = null }
        )
    }
}
