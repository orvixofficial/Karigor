package com.example.karigor.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        BusinessEntity::class,
        ProductEntity::class,
        CustomerEntity::class,
        SaleEntity::class,
        SaleItemEntity::class,
        ExpenseEntity::class,
        InventoryLogEntity::class,
        OrderEntity::class,
        SupplierEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class KarigorDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun customerDao(): CustomerDao
    abstract fun saleDao(): SaleDao
    abstract fun businessDao(): BusinessDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun inventoryLogDao(): InventoryLogDao
    abstract fun orderDao(): OrderDao
    abstract fun supplierDao(): SupplierDao
    abstract fun notificationDao(): NotificationDao
    abstract fun karigorDao(): KarigorDao

    companion object {
        @Volatile
        private var INSTANCE: KarigorDatabase? = null

        fun getDatabase(context: Context): KarigorDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KarigorDatabase::class.java,
                    "karigor_cloud_business.db"
                )
                .fallbackToDestructiveMigration(true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
