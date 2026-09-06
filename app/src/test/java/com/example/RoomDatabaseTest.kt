package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.karigor.data.local.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RoomDatabaseTest {

    private lateinit var db: KarigorDatabase
    private lateinit var productDao: ProductDao
    private lateinit var customerDao: CustomerDao
    private lateinit var saleDao: SaleDao
    private lateinit var businessDao: BusinessDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, KarigorDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        productDao = db.productDao()
        customerDao = db.customerDao()
        saleDao = db.saleDao()
        businessDao = db.businessDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testProductInsertAndStockUpdate() = runBlocking {
        val bizId = "biz-test-01"
        val product = ProductEntity(
            businessId = bizId,
            name = "Panjabi Premium",
            sku = "PANJ-01",
            category = "Clothing",
            sellingPrice = 2500.0,
            costPrice = 1600.0,
            stock = 10,
            lowStockThreshold = 3
        )
        productDao.insertProduct(product)

        val products = productDao.getProductsByBusiness(bizId).first()
        assertEquals(1, products.size)
        assertEquals("Panjabi Premium", products[0].name)
        assertEquals(10, products[0].stock)

        // Update stock
        productDao.updateProductStock(product.id, 2)
        val updated = productDao.getProductById(product.id)
        assertNotNull(updated)
        assertEquals(2, updated?.stock)

        // Verify low stock query
        val lowStock = productDao.getLowStockProducts(bizId).first()
        assertEquals(1, lowStock.size)
        assertEquals("Panjabi Premium", lowStock[0].name)
    }

    @Test
    fun testCustomerInsertAndSearch() = runBlocking {
        val bizId = "biz-test-02"
        val customer1 = CustomerEntity(
            businessId = bizId,
            name = "Shafiqul Islam",
            phone = "01711998877",
            customerType = "VIP"
        )
        val customer2 = CustomerEntity(
            businessId = bizId,
            name = "Sadia Akter",
            phone = "01822334455",
            customerType = "Regular"
        )
        customerDao.insertCustomer(customer1)
        customerDao.insertCustomer(customer2)

        val allCustomers = customerDao.getCustomersByBusiness(bizId).first()
        assertEquals(2, allCustomers.size)

        val searchResult = customerDao.searchCustomers(bizId, "Shafiq").first()
        assertEquals(1, searchResult.size)
        assertEquals("Shafiqul Islam", searchResult[0].name)
    }

    @Test
    fun testSaleAndSaleWithItemsRelation() = runBlocking {
        val bizId = "biz-test-03"
        val sale = SaleEntity(
            businessId = bizId,
            invoiceNumber = "KG-INV-999",
            customerName = "Walk-in Customer",
            subtotal = 3000.0,
            discount = 100.0,
            total = 2900.0,
            paymentMethod = "Cash",
            paymentStatus = "Paid"
        )
        saleDao.insertSale(sale)

        val item1 = SaleItemEntity(
            saleId = sale.id,
            businessId = bizId,
            productId = "prod-1",
            productName = "Cotton Shirt",
            quantity = 2,
            unitPrice = 1500.0,
            totalPrice = 3000.0
        )
        saleDao.insertSaleItems(listOf(item1))

        val retrievedSale = saleDao.getSaleWithItems(sale.id)
        assertNotNull(retrievedSale)
        assertEquals("KG-INV-999", retrievedSale?.sale?.invoiceNumber)
        assertEquals(1, retrievedSale?.items?.size)
        assertEquals("Cotton Shirt", retrievedSale?.items?.get(0)?.productName)
        assertEquals(2, retrievedSale?.items?.get(0)?.quantity)
    }
}
