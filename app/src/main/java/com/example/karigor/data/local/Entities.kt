package com.example.karigor.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.UUID

@Entity(
    tableName = "businesses",
    indices = [Index(value = ["slug"], unique = true)]
)
data class BusinessEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val slug: String,
    val ownerName: String,
    val category: String,
    val phone: String,
    val email: String = "",
    val address: String,
    val description: String = "",
    val currency: String = "BDT (৳)",
    val logoUrl: String = "",
    val isStorePublished: Boolean = true,
    val subscriptionTier: String = "BUSINESS", // FREE, STARTER, BUSINESS, PRO
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "products",
    indices = [
        Index(value = ["businessId"]),
        Index(value = ["businessId", "sku"]),
        Index(value = ["businessId", "category"])
    ]
)
data class ProductEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val businessId: String,
    val name: String,
    val sku: String = "",
    val category: String = "General",
    val sellingPrice: Double,
    val costPrice: Double,
    val stock: Int,
    val lowStockThreshold: Int = 5,
    val status: String = "Active", // Active, Out of stock, Archived
    val imageUrl: String = "",
    val unit: String = "pcs",
    val barcode: String = "",
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "customers",
    indices = [
        Index(value = ["businessId"]),
        Index(value = ["businessId", "phone"])
    ]
)
data class CustomerEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val businessId: String,
    val name: String,
    val phone: String,
    val email: String = "",
    val address: String = "",
    val notes: String = "",
    val customerType: String = "Regular", // Regular, VIP, Wholesale
    val dueBalance: Double = 0.0,
    val totalPurchases: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "sales",
    indices = [
        Index(value = ["businessId"]),
        Index(value = ["businessId", "createdAt"]),
        Index(value = ["customerId"])
    ]
)
data class SaleEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val businessId: String,
    val invoiceNumber: String,
    val customerId: String? = null,
    val customerName: String,
    val customerPhone: String = "",
    val subtotal: Double,
    val discount: Double = 0.0,
    val vat: Double = 0.0,
    val deliveryCharge: Double = 0.0,
    val total: Double,
    val paidAmount: Double = 0.0,
    val dueAmount: Double = 0.0,
    val paymentMethod: String = "Cash", // Cash, bKash / Nagad, Bank Transfer, Other
    val paymentStatus: String = "Paid", // Paid, Due
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "sale_items",
    indices = [
        Index(value = ["saleId"]),
        Index(value = ["businessId"]),
        Index(value = ["productId"])
    ]
)
data class SaleItemEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val saleId: String,
    val businessId: String,
    val productId: String,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double,
    val costPrice: Double = 0.0,
    val totalPrice: Double
)

// One-to-Many Relationship: Sale with its itemized lines
data class SaleWithItems(
    @Embedded val sale: SaleEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "saleId"
    )
    val items: List<SaleItemEntity>
)

// One-to-Many Relationship: Customer with purchase history
data class CustomerWithSales(
    @Embedded val customer: CustomerEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "customerId"
    )
    val sales: List<SaleEntity>
)

@Entity(
    tableName = "expenses",
    indices = [Index(value = ["businessId"]), Index(value = ["businessId", "createdAt"])]
)
data class ExpenseEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val businessId: String,
    val category: String, // Rent, Salary, Electricity, Internet, Transport, Marketing, Inventory, Other
    val amount: Double,
    val date: String,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "inventory_logs",
    indices = [Index(value = ["businessId"]), Index(value = ["productId"])]
)
data class InventoryLogEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val businessId: String,
    val productId: String,
    val productName: String,
    val changeAmount: Int, // positive for addition, negative for deduction
    val newQuantity: Int,
    val reason: String, // Sale #123, Restock, Return, Damaged, Correction
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "orders",
    indices = [Index(value = ["businessId"]), Index(value = ["businessId", "status"])]
)
data class OrderEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val businessId: String,
    val orderNumber: String,
    val customerName: String,
    val customerPhone: String,
    val customerAddress: String,
    val itemsSummary: String,
    val totalAmount: Double,
    val status: String = "New", // New, Confirmed, Shipped, Completed, Cancelled
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "suppliers",
    indices = [Index(value = ["businessId"])]
)
data class SupplierEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val businessId: String,
    val name: String,
    val contactPerson: String,
    val phone: String,
    val email: String = "",
    val category: String = "Wholesale",
    val address: String = "",
    val suppliedProducts: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "notifications",
    indices = [Index(value = ["businessId"]), Index(value = ["businessId", "isRead"])]
)
data class NotificationEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val businessId: String,
    val title: String,
    val message: String,
    val type: String = "ALERT", // LOW_STOCK, NEW_ORDER, SALE, ALERT
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
