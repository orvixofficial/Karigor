package com.example.karigor.data.repository

import com.example.karigor.data.local.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

/**
 * Repository providing offline-first local data management for the Karigor business platform.
 * Backed by Room DAOs with reactive Flow queries and thread-safe coroutine operations.
 */
class KarigorRepository(
    val productDao: ProductDao,
    val customerDao: CustomerDao,
    val saleDao: SaleDao,
    val businessDao: BusinessDao,
    val expenseDao: ExpenseDao,
    val inventoryLogDao: InventoryLogDao,
    val orderDao: OrderDao,
    val supplierDao: SupplierDao,
    val notificationDao: NotificationDao,
    private val appScope: CoroutineScope
) {
    /**
     * Primary convenience constructor utilizing the Room KarigorDatabase instance.
     */
    constructor(database: KarigorDatabase, appScope: CoroutineScope) : this(
        productDao = database.productDao(),
        customerDao = database.customerDao(),
        saleDao = database.saleDao(),
        businessDao = database.businessDao(),
        expenseDao = database.expenseDao(),
        inventoryLogDao = database.inventoryLogDao(),
        orderDao = database.orderDao(),
        supplierDao = database.supplierDao(),
        notificationDao = database.notificationDao(),
        appScope = appScope
    )

    private val _activeBusinessId = MutableStateFlow<String>("")
    val activeBusinessId: StateFlow<String> = _activeBusinessId.asStateFlow()

    val allBusinesses: Flow<List<BusinessEntity>> = businessDao.getAllBusinesses()

    val activeBusiness: Flow<BusinessEntity?> = combine(allBusinesses, _activeBusinessId) { businesses, activeId ->
        businesses.find { it.id == activeId } ?: businesses.firstOrNull()
    }

    // --- Core Model Reactive Streams (Offline-First) ---
    val products: Flow<List<ProductEntity>> = _activeBusinessId.flatMapLatest { id ->
        if (id.isEmpty()) flowOf(emptyList()) else productDao.getProductsByBusiness(id)
    }

    val customers: Flow<List<CustomerEntity>> = _activeBusinessId.flatMapLatest { id ->
        if (id.isEmpty()) flowOf(emptyList()) else customerDao.getCustomersByBusiness(id)
    }

    val sales: Flow<List<SaleEntity>> = _activeBusinessId.flatMapLatest { id ->
        if (id.isEmpty()) flowOf(emptyList()) else saleDao.getSalesByBusiness(id)
    }

    val lowStockProducts: Flow<List<ProductEntity>> = _activeBusinessId.flatMapLatest { id ->
        if (id.isEmpty()) flowOf(emptyList()) else productDao.getLowStockProducts(id)
    }

    val expenses: Flow<List<ExpenseEntity>> = _activeBusinessId.flatMapLatest { id ->
        if (id.isEmpty()) flowOf(emptyList()) else expenseDao.getExpensesByBusiness(id)
    }

    val inventoryLogs: Flow<List<InventoryLogEntity>> = _activeBusinessId.flatMapLatest { id ->
        if (id.isEmpty()) flowOf(emptyList()) else inventoryLogDao.getInventoryLogsByBusiness(id)
    }

    val orders: Flow<List<OrderEntity>> = _activeBusinessId.flatMapLatest { id ->
        if (id.isEmpty()) flowOf(emptyList()) else orderDao.getOrdersByBusiness(id)
    }

    val suppliers: Flow<List<SupplierEntity>> = _activeBusinessId.flatMapLatest { id ->
        if (id.isEmpty()) flowOf(emptyList()) else supplierDao.getSuppliersByBusiness(id)
    }

    val notifications: Flow<List<NotificationEntity>> = _activeBusinessId.flatMapLatest { id ->
        if (id.isEmpty()) flowOf(emptyList()) else notificationDao.getNotificationsByBusiness(id)
    }

    init {
        appScope.launch(Dispatchers.IO) {
            seedInitialDataIfNeeded()
        }
    }

    fun setActiveBusiness(businessId: String) {
        _activeBusinessId.value = businessId
    }

    // --- Business Management ---
    suspend fun createBusiness(
        name: String,
        category: String,
        ownerName: String,
        phone: String,
        email: String,
        address: String,
        description: String
    ): BusinessEntity = withContext(Dispatchers.IO) {
        val slug = name.lowercase().replace("\\s+".toRegex(), "-").filter { it.isLetterOrDigit() || it == '-' }
            .ifEmpty { "biz-${System.currentTimeMillis() % 10000}" }
        val business = BusinessEntity(
            name = name,
            slug = slug,
            ownerName = ownerName,
            category = category,
            phone = phone,
            email = email,
            address = address,
            description = description,
            currency = "BDT (৳)",
            subscriptionTier = "BUSINESS"
        )
        businessDao.insertBusiness(business)
        _activeBusinessId.value = business.id

        // Seed a sample welcome product & notification
        val welcomeProduct = ProductEntity(
            businessId = business.id,
            name = "নমুনা পণ্য (Sample Product)",
            sku = "SKU-001",
            category = category,
            sellingPrice = 1200.0,
            costPrice = 850.0,
            stock = 25,
            lowStockThreshold = 5
        )
        productDao.insertProduct(welcomeProduct)

        notificationDao.insertNotification(
            NotificationEntity(
                businessId = business.id,
                title = "কারিগর-এ স্বাগতম!",
                message = "${business.name} সফলভাবে তৈরি হয়েছে। আপনার ডিজিটাল ব্যবসা শুরু করুন।",
                type = "ALERT"
            )
        )
        business
    }

    // --- Product Management ---
    suspend fun addProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        productDao.insertProduct(product)
        inventoryLogDao.insertInventoryLog(
            InventoryLogEntity(
                businessId = product.businessId,
                productId = product.id,
                productName = product.name,
                changeAmount = product.stock,
                newQuantity = product.stock,
                reason = "Initial Stock Add"
            )
        )
    }

    suspend fun updateProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        productDao.updateProduct(product)
    }

    suspend fun deleteProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        productDao.deleteProduct(product)
    }

    suspend fun adjustStock(productId: String, changeAmount: Int, reason: String) = withContext(Dispatchers.IO) {
        val product = productDao.getProductById(productId) ?: return@withContext
        val newStock = (product.stock + changeAmount).coerceAtLeast(0)
        productDao.updateProductStock(productId, newStock)
        inventoryLogDao.insertInventoryLog(
            InventoryLogEntity(
                businessId = product.businessId,
                productId = productId,
                productName = product.name,
                changeAmount = changeAmount,
                newQuantity = newStock,
                reason = reason
            )
        )
        if (newStock <= product.lowStockThreshold) {
            notificationDao.insertNotification(
                NotificationEntity(
                    businessId = product.businessId,
                    title = "স্টক সতর্কবার্তা!",
                    message = "${product.name}-এর স্টক কমে ${newStock} টি তে দাঁড়িয়েছে। অবিলম্বে রিস্টক করুন।",
                    type = "LOW_STOCK"
                )
            )
        }
    }

    fun searchProducts(query: String): Flow<List<ProductEntity>> {
        val bizId = _activeBusinessId.value
        return if (bizId.isEmpty() || query.isBlank()) {
            products
        } else {
            productDao.searchProducts(bizId, query)
        }
    }

    // --- POS & Sale Management ---
    data class CartItem(
        val product: ProductEntity,
        val quantity: Int
    )

    suspend fun completeSale(
        businessId: String,
        customer: CustomerEntity?,
        customerName: String,
        cartItems: List<CartItem>,
        discount: Double,
        paymentMethod: String,
        notes: String
    ): String = withContext(Dispatchers.IO) {
        val subtotal = cartItems.sumOf { it.product.sellingPrice * it.quantity }
        val grandTotal = (subtotal - discount).coerceAtLeast(0.0)
        val invoiceNo = "KG-${SimpleDateFormat("yyMMdd", Locale.US).format(Date())}-${(1000..9999).random()}"

        val sale = SaleEntity(
            businessId = businessId,
            invoiceNumber = invoiceNo,
            customerId = customer?.id,
            customerName = if (customerName.isNotBlank()) customerName else "নগদ ক্রেতা (Walk-in)",
            customerPhone = customer?.phone ?: "",
            subtotal = subtotal,
            discount = discount,
            total = grandTotal,
            paymentMethod = paymentMethod,
            paymentStatus = "Paid",
            notes = notes
        )
        saleDao.insertSale(sale)

        val saleItems = cartItems.map { item ->
            SaleItemEntity(
                saleId = sale.id,
                businessId = businessId,
                productId = item.product.id,
                productName = item.product.name,
                quantity = item.quantity,
                unitPrice = item.product.sellingPrice,
                costPrice = item.product.costPrice,
                totalPrice = item.product.sellingPrice * item.quantity
            )
        }
        saleDao.insertSaleItems(saleItems)

        // Decrement inventory safely & record log
        for (item in cartItems) {
            val newStock = (item.product.stock - item.quantity).coerceAtLeast(0)
            productDao.updateProductStock(item.product.id, newStock)
            inventoryLogDao.insertInventoryLog(
                InventoryLogEntity(
                    businessId = businessId,
                    productId = item.product.id,
                    productName = item.product.name,
                    changeAmount = -item.quantity,
                    newQuantity = newStock,
                    reason = "Sale $invoiceNo"
                )
            )
            if (newStock <= item.product.lowStockThreshold) {
                notificationDao.insertNotification(
                    NotificationEntity(
                        businessId = businessId,
                        title = "স্টক সতর্কবার্তা!",
                        message = "${item.product.name}-এর স্টক কমে ${newStock} টি তে দাঁড়িয়েছে।",
                        type = "LOW_STOCK"
                    )
                )
            }
        }

        notificationDao.insertNotification(
            NotificationEntity(
                businessId = businessId,
                title = "নতুন বিক্রি সম্পন্ন!",
                message = "চালান $invoiceNo - ৳$grandTotal (${sale.customerName})",
                type = "SALE"
            )
        )

        invoiceNo
    }

    suspend fun getSaleWithItems(saleId: String): SaleWithItems? = withContext(Dispatchers.IO) {
        saleDao.getSaleWithItems(saleId)
    }

    // --- Customer Management ---
    suspend fun addCustomer(customer: CustomerEntity) = withContext(Dispatchers.IO) {
        customerDao.insertCustomer(customer)
    }

    suspend fun deleteCustomer(customer: CustomerEntity) = withContext(Dispatchers.IO) {
        customerDao.deleteCustomer(customer)
    }

    fun searchCustomers(query: String): Flow<List<CustomerEntity>> {
        val bizId = _activeBusinessId.value
        return if (bizId.isEmpty() || query.isBlank()) {
            customers
        } else {
            customerDao.searchCustomers(bizId, query)
        }
    }

    // --- Expense Management ---
    suspend fun addExpense(expense: ExpenseEntity) = withContext(Dispatchers.IO) {
        expenseDao.insertExpense(expense)
    }

    suspend fun deleteExpense(expense: ExpenseEntity) = withContext(Dispatchers.IO) {
        expenseDao.deleteExpense(expense)
    }

    // --- Storefront Orders ---
    suspend fun placeOnlineOrder(
        businessId: String,
        customerName: String,
        phone: String,
        address: String,
        itemsSummary: String,
        total: Double,
        notes: String
    ) = withContext(Dispatchers.IO) {
        val orderNo = "ORD-${SimpleDateFormat("ddHHmm", Locale.US).format(Date())}"
        val order = OrderEntity(
            businessId = businessId,
            orderNumber = orderNo,
            customerName = customerName,
            customerPhone = phone,
            customerAddress = address,
            itemsSummary = itemsSummary,
            totalAmount = total,
            status = "New",
            notes = notes
        )
        orderDao.insertOrder(order)
        notificationDao.insertNotification(
            NotificationEntity(
                businessId = businessId,
                title = "নতুন অনলাইন অর্ডার এসেছে!",
                message = "$orderNo: ৳$total ($customerName, $phone)",
                type = "NEW_ORDER"
            )
        )
    }

    suspend fun updateOrderStatus(orderId: String, status: String) = withContext(Dispatchers.IO) {
        orderDao.updateOrderStatus(orderId, status)
    }

    // --- Suppliers ---
    suspend fun addSupplier(supplier: SupplierEntity) = withContext(Dispatchers.IO) {
        supplierDao.insertSupplier(supplier)
    }

    suspend fun deleteSupplier(supplier: SupplierEntity) = withContext(Dispatchers.IO) {
        supplierDao.deleteSupplier(supplier)
    }

    // --- Notifications ---
    suspend fun markAllNotificationsRead(businessId: String) = withContext(Dispatchers.IO) {
        notificationDao.markAllNotificationsRead(businessId)
    }

    // --- Database Seeding ---
    private suspend fun seedInitialDataIfNeeded() {
        val existing = businessDao.getBusinessBySlug("dhaka-fashion-house")
        if (existing != null) {
            _activeBusinessId.value = existing.id
            return
        }

        // Create Dhaka Fashion House
        val b1 = BusinessEntity(
            name = "ঢাকা ফ্যাশন হাউস",
            slug = "dhaka-fashion-house",
            ownerName = "মোঃ রফিকুল ইসলাম",
            category = "Fashion & Lifestyle",
            phone = "+8801711223344",
            email = "info@dhakafashion.bd",
            address = "রোড #২৭, ধানমন্ডি, ঢাকা",
            description = "প্রিমিয়াম পাঞ্জাবি, শাড়ি এবং ঐতিহ্যবাহী পোশাকের সেরা প্রতিষ্ঠান।",
            currency = "BDT (৳)",
            subscriptionTier = "BUSINESS"
        )
        businessDao.insertBusiness(b1)
        _activeBusinessId.value = b1.id

        // Products for Fashion House
        val p1 = ProductEntity(businessId = b1.id, name = "প্রিমিয়াম কটন পাঞ্জাবি", sku = "DFH-P01", category = "পাঞ্জাবি", sellingPrice = 2200.0, costPrice = 1450.0, stock = 18, lowStockThreshold = 5, unit = "pcs")
        val p2 = ProductEntity(businessId = b1.id, name = "জামদানি সুতি শাড়ি", sku = "DFH-S02", category = "শাড়ি", sellingPrice = 4500.0, costPrice = 3100.0, stock = 8, lowStockThreshold = 3, unit = "pcs")
        val p3 = ProductEntity(businessId = b1.id, name = "সেমি-লং কাবলি সেট", sku = "DFH-K03", category = "পাঞ্জাবি", sellingPrice = 2800.0, costPrice = 1900.0, stock = 4, lowStockThreshold = 5, unit = "set")
        val p4 = ProductEntity(businessId = b1.id, name = "এক্সিকিউটিভ ফর্মাল শার্ট", sku = "DFH-SH04", category = "শার্ট", sellingPrice = 1650.0, costPrice = 1050.0, stock = 22, lowStockThreshold = 6, unit = "pcs")
        val p5 = ProductEntity(businessId = b1.id, name = "সিল্ক ওড়না ও শাল", sku = "DFH-SL05", category = "শীতকালীন", sellingPrice = 1250.0, costPrice = 750.0, stock = 2, lowStockThreshold = 4, unit = "pcs")

        listOf(p1, p2, p3, p4, p5).forEach { productDao.insertProduct(it) }

        // Customers
        val c1 = CustomerEntity(businessId = b1.id, name = "তানভীর আহমেদ", phone = "01819876543", address = "গুলশান ২, ঢাকা", notes = "VIP নিয়মিত ক্রেতা", customerType = "VIP")
        val c2 = CustomerEntity(businessId = b1.id, name = "নুসরাত জাহান", phone = "01912345678", address = "উত্তরা সেক্টর ৭, ঢাকা", notes = "অনলাইন কাস্টমার", customerType = "Regular")
        val c3 = CustomerEntity(businessId = b1.id, name = "মেহরাব হোসেন", phone = "01723456789", address = "বনানী, ঢাকা", notes = "ক্যাশ পেমেন্ট", customerType = "Regular")
        listOf(c1, c2, c3).forEach { customerDao.insertCustomer(it) }

        // Initial Sales
        val s1 = SaleEntity(
            businessId = b1.id,
            invoiceNumber = "KG-260901-1021",
            customerId = c1.id,
            customerName = c1.name,
            customerPhone = c1.phone,
            subtotal = 6700.0,
            discount = 200.0,
            total = 6500.0,
            paymentMethod = "bKash / Nagad",
            paymentStatus = "Paid",
            notes = "ঈদ কালেকশন প্রি-বুকিং",
            createdAt = System.currentTimeMillis() - 86400000L * 2
        )
        val s2 = SaleEntity(
            businessId = b1.id,
            invoiceNumber = "KG-260902-1088",
            customerId = c2.id,
            customerName = c2.name,
            customerPhone = c2.phone,
            subtotal = 4500.0,
            discount = 0.0,
            total = 4500.0,
            paymentMethod = "Cash",
            paymentStatus = "Paid",
            notes = "দোকানে এসে ক্রয়",
            createdAt = System.currentTimeMillis() - 86400000L
        )
        saleDao.insertSale(s1)
        saleDao.insertSale(s2)

        // Expenses
        val e1 = ExpenseEntity(businessId = b1.id, category = "দোকান ভাড়া (Rent)", amount = 25000.0, date = "২০২৬-০৯-০১", notes = "সেপ্টেম্বর মাসের শোরুম ভাড়া")
        val e2 = ExpenseEntity(businessId = b1.id, category = "বিদ্যুৎ বিল (Electricity)", amount = 3200.0, date = "২০২৬-০৯-০৩", notes = "আগস্ট মাসের বিদ্যুৎ বিল")
        val e3 = ExpenseEntity(businessId = b1.id, category = "বিজ্ঞাপন (Marketing)", amount = 5000.0, date = "২০২৬-০৯-০৪", notes = "ফেসবুক বুস্টিং ক্যাম্পেইন")
        listOf(e1, e2, e3).forEach { expenseDao.insertExpense(it) }

        // Suppliers
        val sup1 = SupplierEntity(businessId = b1.id, name = "নরসিংদী উইভিং টেক্সটাইল", contactPerson = "আব্দুল কাদের", phone = "01712009988", category = "ফ্যাব্রিক সাপ্লায়ার", address = "নরসিংদী সদর", suppliedProducts = "উন্নত সুতি ও খাদি কাপড়")
        supplierDao.insertSupplier(sup1)

        // Notification
        notificationDao.insertNotification(
            NotificationEntity(
                businessId = b1.id,
                title = "সিল্ক ওড়না ও শাল স্টক কম!",
                message = "মাত্র ২ টি অবশিষ্ট আছে। নতুন অর্ডার করার পরামর্শ দেওয়া হচ্ছে।",
                type = "LOW_STOCK"
            )
        )
    }
}
