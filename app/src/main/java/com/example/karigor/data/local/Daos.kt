package com.example.karigor.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Product Data Access Object for local offline inventory management.
 */
@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE businessId = :businessId ORDER BY name ASC")
    fun getProductsByBusiness(businessId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE businessId = :businessId AND stock <= lowStockThreshold ORDER BY stock ASC")
    fun getLowStockProducts(businessId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE businessId = :businessId AND (name LIKE '%' || :query || '%' OR sku LIKE '%' || :query || '%') ORDER BY name ASC")
    fun searchProducts(businessId: String, query: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE businessId = :businessId AND category = :category ORDER BY name ASC")
    fun getProductsByCategory(businessId: String, category: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :productId LIMIT 1")
    suspend fun getProductById(productId: String): ProductEntity?

    @Query("SELECT * FROM products WHERE businessId = :businessId AND sku = :sku LIMIT 1")
    suspend fun getProductBySku(businessId: String, sku: String): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("UPDATE products SET stock = :newStock, status = CASE WHEN :newStock <= 0 THEN 'Out of stock' ELSE 'Active' END, updatedAt = :updatedAt WHERE id = :productId")
    suspend fun updateProductStock(productId: String, newStock: Int, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM products WHERE businessId = :businessId")
    suspend fun countProducts(businessId: String): Int

    @Query("SELECT COUNT(*) FROM products WHERE businessId = :businessId AND stock <= lowStockThreshold")
    suspend fun countLowStockProducts(businessId: String): Int
}

/**
 * Customer Data Access Object for local customer registry and balance tracking.
 */
@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers WHERE businessId = :businessId ORDER BY name ASC")
    fun getCustomersByBusiness(businessId: String): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers WHERE businessId = :businessId AND (name LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%') ORDER BY name ASC")
    fun searchCustomers(businessId: String, query: String): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers WHERE id = :customerId LIMIT 1")
    suspend fun getCustomerById(customerId: String): CustomerEntity?

    @Query("SELECT * FROM customers WHERE businessId = :businessId AND phone = :phone LIMIT 1")
    suspend fun getCustomerByPhone(businessId: String, phone: String): CustomerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: CustomerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomers(customers: List<CustomerEntity>)

    @Update
    suspend fun updateCustomer(customer: CustomerEntity)

    @Delete
    suspend fun deleteCustomer(customer: CustomerEntity)

    @Query("SELECT COUNT(*) FROM customers WHERE businessId = :businessId")
    suspend fun countCustomers(businessId: String): Int

    @Transaction
    @Query("SELECT * FROM customers WHERE id = :customerId LIMIT 1")
    fun getCustomerWithSales(customerId: String): Flow<CustomerWithSales?>
}

/**
 * Sale & Sale Item Data Access Object for POS checkout and invoice generation.
 */
@Dao
interface SaleDao {
    @Query("SELECT * FROM sales WHERE businessId = :businessId ORDER BY createdAt DESC")
    fun getSalesByBusiness(businessId: String): Flow<List<SaleEntity>>

    @Query("SELECT * FROM sales WHERE id = :saleId LIMIT 1")
    suspend fun getSaleById(saleId: String): SaleEntity?

    @Query("SELECT * FROM sales WHERE businessId = :businessId AND invoiceNumber = :invoiceNumber LIMIT 1")
    suspend fun getSaleByInvoiceNumber(businessId: String, invoiceNumber: String): SaleEntity?

    @Query("SELECT * FROM sales WHERE businessId = :businessId AND customerId = :customerId ORDER BY createdAt DESC")
    fun getSalesByCustomer(businessId: String, customerId: String): Flow<List<SaleEntity>>

    @Query("SELECT * FROM sales WHERE businessId = :businessId AND createdAt >= :startTime AND createdAt <= :endTime ORDER BY createdAt DESC")
    fun getSalesInRange(businessId: String, startTime: Long, endTime: Long): Flow<List<SaleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: SaleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSales(sales: List<SaleEntity>)

    @Update
    suspend fun updateSale(sale: SaleEntity)

    @Delete
    suspend fun deleteSale(sale: SaleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaleItems(items: List<SaleItemEntity>)

    @Query("SELECT * FROM sale_items WHERE saleId = :saleId")
    fun getSaleItems(saleId: String): Flow<List<SaleItemEntity>>

    @Query("SELECT * FROM sale_items WHERE businessId = :businessId")
    fun getAllSaleItemsByBusiness(businessId: String): Flow<List<SaleItemEntity>>

    @Transaction
    @Query("SELECT * FROM sales WHERE id = :saleId LIMIT 1")
    suspend fun getSaleWithItems(saleId: String): SaleWithItems?

    @Transaction
    @Query("SELECT * FROM sales WHERE businessId = :businessId ORDER BY createdAt DESC")
    fun getSalesWithItemsByBusiness(businessId: String): Flow<List<SaleWithItems>>

    @Query("SELECT SUM(total) FROM sales WHERE businessId = :businessId")
    suspend fun getTotalRevenue(businessId: String): Double?

    @Query("SELECT COUNT(*) FROM sales WHERE businessId = :businessId")
    suspend fun countSales(businessId: String): Int
}

/**
 * Business Multi-tenant Data Access Object.
 */
@Dao
interface BusinessDao {
    @Query("SELECT * FROM businesses ORDER BY createdAt DESC")
    fun getAllBusinesses(): Flow<List<BusinessEntity>>

    @Query("SELECT * FROM businesses WHERE id = :businessId LIMIT 1")
    suspend fun getBusinessById(businessId: String): BusinessEntity?

    @Query("SELECT * FROM businesses WHERE slug = :slug LIMIT 1")
    suspend fun getBusinessBySlug(slug: String): BusinessEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusiness(business: BusinessEntity): Long

    @Update
    suspend fun updateBusiness(business: BusinessEntity)

    @Delete
    suspend fun deleteBusiness(business: BusinessEntity)
}

/**
 * Expense Data Access Object.
 */
@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses WHERE businessId = :businessId ORDER BY createdAt DESC")
    fun getExpensesByBusiness(businessId: String): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("SELECT SUM(amount) FROM expenses WHERE businessId = :businessId")
    suspend fun getTotalExpenses(businessId: String): Double?
}

/**
 * Inventory Log Data Access Object for audit trails.
 */
@Dao
interface InventoryLogDao {
    @Query("SELECT * FROM inventory_logs WHERE businessId = :businessId ORDER BY createdAt DESC")
    fun getInventoryLogsByBusiness(businessId: String): Flow<List<InventoryLogEntity>>

    @Query("SELECT * FROM inventory_logs WHERE productId = :productId ORDER BY createdAt DESC")
    fun getInventoryLogsByProduct(productId: String): Flow<List<InventoryLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryLog(log: InventoryLogEntity): Long
}

/**
 * Storefront Order Data Access Object.
 */
@Dao
interface OrderDao {
    @Query("SELECT * FROM orders WHERE businessId = :businessId ORDER BY createdAt DESC")
    fun getOrdersByBusiness(businessId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE businessId = :businessId AND status = :status ORDER BY createdAt DESC")
    fun getOrdersByStatus(businessId: String, status: String): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String)
}

/**
 * Supplier Data Access Object.
 */
@Dao
interface SupplierDao {
    @Query("SELECT * FROM suppliers WHERE businessId = :businessId ORDER BY name ASC")
    fun getSuppliersByBusiness(businessId: String): Flow<List<SupplierEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplier(supplier: SupplierEntity): Long

    @Delete
    suspend fun deleteSupplier(supplier: SupplierEntity)
}

/**
 * Notification Data Access Object.
 */
@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE businessId = :businessId ORDER BY createdAt DESC")
    fun getNotificationsByBusiness(businessId: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE businessId = :businessId")
    suspend fun markAllNotificationsRead(businessId: String)

    @Query("SELECT COUNT(*) FROM notifications WHERE businessId = :businessId AND isRead = 0")
    fun getUnreadNotificationCount(businessId: String): Flow<Int>
}

/**
 * Unified Karigor Data Access Object for complete offline persistence across all modules.
 */
@Dao
interface KarigorDao {

    // --- Businesses ---
    @Query("SELECT * FROM businesses ORDER BY createdAt DESC")
    fun getAllBusinesses(): Flow<List<BusinessEntity>>

    @Query("SELECT * FROM businesses WHERE id = :businessId LIMIT 1")
    suspend fun getBusinessById(businessId: String): BusinessEntity?

    @Query("SELECT * FROM businesses WHERE slug = :slug LIMIT 1")
    suspend fun getBusinessBySlug(slug: String): BusinessEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusiness(business: BusinessEntity): Long

    @Update
    suspend fun updateBusiness(business: BusinessEntity)

    // --- Products ---
    @Query("SELECT * FROM products WHERE businessId = :businessId ORDER BY name ASC")
    fun getProductsByBusiness(businessId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE businessId = :businessId AND stock <= lowStockThreshold ORDER BY stock ASC")
    fun getLowStockProducts(businessId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE businessId = :businessId AND (name LIKE '%' || :query || '%' OR sku LIKE '%' || :query || '%') ORDER BY name ASC")
    fun searchProducts(businessId: String, query: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :productId LIMIT 1")
    suspend fun getProductById(productId: String): ProductEntity?

    @Query("SELECT * FROM products WHERE businessId = :businessId AND sku = :sku LIMIT 1")
    suspend fun getProductBySku(businessId: String, sku: String): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("UPDATE products SET stock = :newStock, status = CASE WHEN :newStock <= 0 THEN 'Out of stock' ELSE 'Active' END, updatedAt = :updatedAt WHERE id = :productId")
    suspend fun updateProductStock(productId: String, newStock: Int, updatedAt: Long = System.currentTimeMillis())

    // --- Customers ---
    @Query("SELECT * FROM customers WHERE businessId = :businessId ORDER BY name ASC")
    fun getCustomersByBusiness(businessId: String): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers WHERE businessId = :businessId AND (name LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%') ORDER BY name ASC")
    fun searchCustomers(businessId: String, query: String): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers WHERE id = :customerId LIMIT 1")
    suspend fun getCustomerById(customerId: String): CustomerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: CustomerEntity): Long

    @Update
    suspend fun updateCustomer(customer: CustomerEntity)

    @Delete
    suspend fun deleteCustomer(customer: CustomerEntity)

    @Transaction
    @Query("SELECT * FROM customers WHERE id = :customerId LIMIT 1")
    fun getCustomerWithSales(customerId: String): Flow<CustomerWithSales?>

    // --- Sales & Sale Items ---
    @Query("SELECT * FROM sales WHERE businessId = :businessId ORDER BY createdAt DESC")
    fun getSalesByBusiness(businessId: String): Flow<List<SaleEntity>>

    @Query("SELECT * FROM sales WHERE id = :saleId LIMIT 1")
    suspend fun getSaleById(saleId: String): SaleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: SaleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaleItems(items: List<SaleItemEntity>)

    @Query("SELECT * FROM sale_items WHERE saleId = :saleId")
    fun getSaleItems(saleId: String): Flow<List<SaleItemEntity>>

    @Query("SELECT * FROM sale_items WHERE businessId = :businessId")
    fun getAllSaleItemsByBusiness(businessId: String): Flow<List<SaleItemEntity>>

    @Transaction
    @Query("SELECT * FROM sales WHERE id = :saleId LIMIT 1")
    suspend fun getSaleWithItems(saleId: String): SaleWithItems?

    @Transaction
    @Query("SELECT * FROM sales WHERE businessId = :businessId ORDER BY createdAt DESC")
    fun getSalesWithItemsByBusiness(businessId: String): Flow<List<SaleWithItems>>

    // --- Expenses ---
    @Query("SELECT * FROM expenses WHERE businessId = :businessId ORDER BY createdAt DESC")
    fun getExpensesByBusiness(businessId: String): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    // --- Inventory Logs ---
    @Query("SELECT * FROM inventory_logs WHERE businessId = :businessId ORDER BY createdAt DESC")
    fun getInventoryLogsByBusiness(businessId: String): Flow<List<InventoryLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryLog(log: InventoryLogEntity): Long

    // --- Orders ---
    @Query("SELECT * FROM orders WHERE businessId = :businessId ORDER BY createdAt DESC")
    fun getOrdersByBusiness(businessId: String): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String)

    // --- Suppliers ---
    @Query("SELECT * FROM suppliers WHERE businessId = :businessId ORDER BY name ASC")
    fun getSuppliersByBusiness(businessId: String): Flow<List<SupplierEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplier(supplier: SupplierEntity): Long

    @Delete
    suspend fun deleteSupplier(supplier: SupplierEntity)

    // --- Notifications ---
    @Query("SELECT * FROM notifications WHERE businessId = :businessId ORDER BY createdAt DESC")
    fun getNotificationsByBusiness(businessId: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE businessId = :businessId")
    suspend fun markAllNotificationsRead(businessId: String)
}
