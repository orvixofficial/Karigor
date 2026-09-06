package com.example.karigor.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.karigor.ai.KarigorAiService
import com.example.karigor.data.local.*
import com.example.karigor.data.model.AppLanguage
import com.example.karigor.data.repository.KarigorRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class Screen {
    LANDING,
    DASHBOARD,
    PRODUCTS,
    INVENTORY,
    SALES,
    CUSTOMERS,
    EXPENSES,
    INVOICES,
    ONLINE_STORE,
    STOREFRONT_PREVIEW,
    AI_ASSISTANT,
    SUPPLIERS,
    MARKETPLACE,
    SUBSCRIPTION,
    SETTINGS
}

data class ChatMessage(
    val sender: String, // "user" or "karigor_ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class KarigorViewModel(
    application: Application,
    private val repository: KarigorRepository
) : AndroidViewModel(application) {

    private val aiService = KarigorAiService()

    // Navigation & App Settings
    private val _currentScreen = MutableStateFlow(Screen.DASHBOARD)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _language = MutableStateFlow(AppLanguage.BANGLA)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    // Business & Core Data
    val activeBusiness: StateFlow<BusinessEntity?> = repository.activeBusiness
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allBusinesses: StateFlow<List<BusinessEntity>> = repository.allBusinesses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val products: StateFlow<List<ProductEntity>> = repository.products
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customers: StateFlow<List<CustomerEntity>> = repository.customers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sales: StateFlow<List<SaleEntity>> = repository.sales
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<ExpenseEntity>> = repository.expenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inventoryLogs: StateFlow<List<InventoryLogEntity>> = repository.inventoryLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<OrderEntity>> = repository.orders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val suppliers: StateFlow<List<SupplierEntity>> = repository.suppliers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationEntity>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filter & Search states
    val productSearch = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("All")

    // POS Cart State
    val posCart = MutableStateFlow<List<KarigorRepository.CartItem>>(emptyList())
    val selectedPosCustomer = MutableStateFlow<CustomerEntity?>(null)
    val posDiscount = MutableStateFlow(0.0)
    val posPaymentMethod = MutableStateFlow("Cash")

    // AI Chat & Marketing State
    val chatMessages = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage("karigor_ai", "আসসালামু আলাইকুম! আমি কারিগর এআই বিজনেস সহকারী। আপনার বিক্রি, স্টক, খরচ বা ব্যবসার প্রবৃদ্ধি সংক্রান্ত যেকোনো প্রশ্ন করতে পারেন।")
    ))
    val isAiThinking = MutableStateFlow(false)
    val marketingResult = MutableStateFlow("")
    val isMarketingThinking = MutableStateFlow(false)

    // Selected Invoice for Modal Preview
    val selectedInvoice = MutableStateFlow<SaleEntity?>(null)

    // Modals visibility
    val showOnboardingDialog = MutableStateFlow(false)
    val showAddProductDialog = MutableStateFlow(false)
    val showNewSaleDialog = MutableStateFlow(false)
    val showAddExpenseDialog = MutableStateFlow(false)
    val showAddCustomerDialog = MutableStateFlow(false)
    val showAdjustStockDialog = MutableStateFlow<ProductEntity?>(null)
    val showAddSupplierDialog = MutableStateFlow(false)
    val showNotificationsSheet = MutableStateFlow(false)

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun toggleLanguage() {
        _language.value = if (_language.value == AppLanguage.BANGLA) AppLanguage.ENGLISH else AppLanguage.BANGLA
    }

    fun selectBusiness(businessId: String) {
        repository.setActiveBusiness(businessId)
    }

    fun createNewBusiness(
        name: String,
        category: String,
        ownerName: String,
        phone: String,
        email: String,
        address: String,
        description: String
    ) {
        viewModelScope.launch {
            repository.createBusiness(name, category, ownerName, phone, email, address, description)
            showOnboardingDialog.value = false
            _currentScreen.value = Screen.DASHBOARD
        }
    }

    fun addProduct(
        name: String,
        sku: String,
        category: String,
        sellingPrice: Double,
        costPrice: Double,
        stock: Int,
        lowStockThreshold: Int
    ) {
        val bizId = activeBusiness.value?.id ?: return
        viewModelScope.launch {
            val product = ProductEntity(
                businessId = bizId,
                name = name,
                sku = sku,
                category = category,
                sellingPrice = sellingPrice,
                costPrice = costPrice,
                stock = stock,
                lowStockThreshold = lowStockThreshold
            )
            repository.addProduct(product)
            showAddProductDialog.value = false
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    fun adjustStock(productId: String, changeAmount: Int, reason: String) {
        viewModelScope.launch {
            repository.adjustStock(productId, changeAmount, reason)
            showAdjustStockDialog.value = null
        }
    }

    fun addProductToPosCart(product: ProductEntity) {
        val current = posCart.value.toMutableList()
        val index = current.indexOfFirst { it.product.id == product.id }
        if (index >= 0) {
            val item = current[index]
            if (item.quantity < product.stock) {
                current[index] = item.copy(quantity = item.quantity + 1)
            }
        } else {
            if (product.stock > 0) {
                current.add(KarigorRepository.CartItem(product, 1))
            }
        }
        posCart.value = current
    }

    fun removePosCartItem(product: ProductEntity) {
        val current = posCart.value.toMutableList()
        val index = current.indexOfFirst { it.product.id == product.id }
        if (index >= 0) {
            val item = current[index]
            if (item.quantity > 1) {
                current[index] = item.copy(quantity = item.quantity - 1)
            } else {
                current.removeAt(index)
            }
        }
        posCart.value = current
    }

    fun clearPosCart() {
        posCart.value = emptyList()
        selectedPosCustomer.value = null
        posDiscount.value = 0.0
    }

    fun submitPosSale(customerName: String, notes: String, onSuccess: (String) -> Unit) {
        val bizId = activeBusiness.value?.id ?: return
        val items = posCart.value
        if (items.isEmpty()) return

        viewModelScope.launch {
            val invoiceNo = repository.completeSale(
                businessId = bizId,
                customer = selectedPosCustomer.value,
                customerName = customerName,
                cartItems = items,
                discount = posDiscount.value,
                paymentMethod = posPaymentMethod.value,
                notes = notes
            )
            clearPosCart()
            showNewSaleDialog.value = false
            onSuccess(invoiceNo)
        }
    }

    fun addCustomer(name: String, phone: String, email: String, address: String, notes: String) {
        val bizId = activeBusiness.value?.id ?: return
        viewModelScope.launch {
            repository.addCustomer(
                CustomerEntity(
                    businessId = bizId,
                    name = name,
                    phone = phone,
                    email = email,
                    address = address,
                    notes = notes
                )
            )
            showAddCustomerDialog.value = false
        }
    }

    fun deleteCustomer(customer: CustomerEntity) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
        }
    }

    fun addExpense(category: String, amount: Double, date: String, notes: String) {
        val bizId = activeBusiness.value?.id ?: return
        viewModelScope.launch {
            repository.addExpense(
                ExpenseEntity(
                    businessId = bizId,
                    category = category,
                    amount = amount,
                    date = date,
                    notes = notes
                )
            )
            showAddExpenseDialog.value = false
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    fun addSupplier(name: String, contactPerson: String, phone: String, category: String, address: String, suppliedProducts: String) {
        val bizId = activeBusiness.value?.id ?: return
        viewModelScope.launch {
            repository.addSupplier(
                SupplierEntity(
                    businessId = bizId,
                    name = name,
                    contactPerson = contactPerson,
                    phone = phone,
                    category = category,
                    address = address,
                    suppliedProducts = suppliedProducts
                )
            )
            showAddSupplierDialog.value = false
        }
    }

    fun deleteSupplier(supplier: SupplierEntity) {
        viewModelScope.launch {
            repository.deleteSupplier(supplier)
        }
    }

    fun placeStorefrontOrder(
        customerName: String,
        phone: String,
        address: String,
        itemsSummary: String,
        total: Double,
        notes: String,
        onSuccess: () -> Unit
    ) {
        val bizId = activeBusiness.value?.id ?: return
        viewModelScope.launch {
            repository.placeOnlineOrder(bizId, customerName, phone, address, itemsSummary, total, notes)
            onSuccess()
        }
    }

    fun updateOrderStatus(orderId: String, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
        }
    }

    fun markAllNotificationsRead() {
        val bizId = activeBusiness.value?.id ?: return
        viewModelScope.launch {
            repository.markAllNotificationsRead(bizId)
        }
    }

    fun askAiAssistant(question: String) {
        if (question.isBlank()) return
        val userMsg = ChatMessage("user", question)
        chatMessages.value = chatMessages.value + userMsg
        isAiThinking.value = true

        val biz = activeBusiness.value
        val prods = products.value
        val sls = sales.value
        val exps = expenses.value
        val isBn = language.value == AppLanguage.BANGLA

        viewModelScope.launch {
            val answer = aiService.askBusinessAdvisor(question, biz, prods, sls, exps, isBn)
            chatMessages.value = chatMessages.value + ChatMessage("karigor_ai", answer)
            isAiThinking.value = false
        }
    }

    fun generateMarketingContent(productName: String, category: String, price: Double, occasion: String) {
        isMarketingThinking.value = true
        val isBn = language.value == AppLanguage.BANGLA
        viewModelScope.launch {
            val result = aiService.generateMarketingCopy(productName, category, price, occasion, isBn)
            marketingResult.value = result
            isMarketingThinking.value = false
        }
    }
}
