package com.example.karigor.ai

import com.example.BuildConfig
import com.example.karigor.data.local.BusinessEntity
import com.example.karigor.data.local.ExpenseEntity
import com.example.karigor.data.local.ProductEntity
import com.example.karigor.data.local.SaleEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class KarigorAiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun askBusinessAdvisor(
        question: String,
        business: BusinessEntity?,
        products: List<ProductEntity>,
        sales: List<SaleEntity>,
        expenses: List<ExpenseEntity>,
        isBangla: Boolean
    ): String = withContext(Dispatchers.IO) {
        val totalRevenue = sales.sumOf { it.total }
        val totalExpense = expenses.sumOf { it.amount }
        val netProfit = totalRevenue - totalExpense
        val lowStockItems = products.filter { it.stock <= it.lowStockThreshold }
        val topProducts = products.sortedByDescending { it.sellingPrice }.take(3).map { "${it.name} (৳${it.sellingPrice})" }
        val highestExpense = expenses.maxByOrNull { it.amount }

        val contextInfo = """
            Business: ${business?.name ?: "Karigor Merchant"} (${business?.category ?: "Retail"})
            Currency: BDT (৳)
            Total Sales: ৳$totalRevenue across ${sales.size} sales.
            Total Expenses: ৳$totalExpense across ${expenses.size} records.
            Estimated Net Profit: ৳$netProfit
            Total Products: ${products.size}
            Low Stock Alerts: ${lowStockItems.size} items (${lowStockItems.joinToString { "${it.name} (${it.stock} pcs left)" }})
            Top priced items: ${topProducts.joinToString()}
            Largest expense: ${highestExpense?.category ?: "None"} (৳${highestExpense?.amount ?: 0.0})
        """.trimIndent()

        val prompt = if (isBangla) {
            """
            তুমি কারিগর (Karigor) ক্লাউড বিজনেস প্ল্যাটফর্মের একজন অভিজ্ঞ বাংলাদেশি বিজনেস উপদেষ্টা এআই।
            ব্যবসার বাস্তব তথ্য নিচে দেওয়া হলো:
            $contextInfo

            মালিকের প্রশ্ন: "$question"

            নির্দেশনা:
            ১. উপরের বাস্তব বিজনেস ডেটা ব্যবহার করে স্পষ্টভাবে বাংলায় উত্তর দিন।
            ২. টাকার হিসাব সবসময় '৳' (BDT) দিয়ে লিখবেন।
            ৩. কার্যকরী ও ইতিবাচক পরামর্শ দিন যা বাংলাদেশের ক্ষুদ্র ও মাঝারি ব্যবসার জন্য বাস্তবসম্মত।
            """.trimIndent()
        } else {
            """
            You are Karigor's AI Business Advisor for Bangladesh SMEs.
            Here is the authorized business data:
            $contextInfo

            Owner question: "$question"

            Instructions:
            1. Provide a sharp, data-driven answer in clear English.
            2. Reference figures in BDT (৳).
            3. Give actionable practical advice for Bangladesh retail/commerce.
            """.trimIndent()
        }

        callGeminiOrFallback(prompt) {
            // Intelligent Rule-Based Contextual Fallback
            generateSmartFallbackAnswer(question, isBangla, totalRevenue, totalExpense, netProfit, lowStockItems, highestExpense, products)
        }
    }

    suspend fun generateMarketingCopy(
        productName: String,
        category: String,
        price: Double,
        occasionOrGoal: String,
        isBangla: Boolean
    ): String = withContext(Dispatchers.IO) {
        val prompt = if (isBangla) {
            """
            তুমি কারিগর (Karigor) এর দক্ষ বাংলাদেশি সোশ্যাল মিডিয়া ও ডিজিটাল মার্কেটিং এক্সপার্ট।
            পণ্যের নাম: $productName
            ক্যাটাগরি: $category
            মূল্য: ৳$price
            উদ্দেশ্য বা উৎসব: $occasionOrGoal

            অনুগ্রহ করে আকর্ষণীয় ও বিক্রয় উপযোগী কন্টেন্ট তৈরি করুন:
            ১. ফেসবুক/ইনস্টাগ্রাম পোস্টের আকর্ষণীয় ক্যাপশন (ইমোজি সহ)
            ২. বিশেষ অফার/ডিসকাউন্ট স্লোগান
            ৩. পণ্যের মূল আকর্ষণ (Bullet points)
            ৪. কল টু অ্যাকশন (অর্ডার করার নিয়ম ও ইনবক্স করার আহ্বান)
            ৫. প্রাসঙ্গিক হ্যাশট্যাগ (#Karigor #MadeInBangladesh ইত্যাদি)
            """.trimIndent()
        } else {
            """
            You are Karigor's Marketing AI Generator for Bangladeshi entrepreneurs.
            Product: $productName
            Category: $category
            Price: ৳$price
            Campaign Goal: $occasionOrGoal

            Generate high-converting marketing materials:
            1. Engaging Facebook/Instagram caption with emojis
            2. Catchy Promo Headline
            3. Key Product Highlights
            4. Strong Call-to-Action (bKash/COD order process)
            5. Relevant Hashtags
            """.trimIndent()
        }

        callGeminiOrFallback(prompt) {
            if (isBangla) {
                """
                ✨ আপনার পছন্দের $productName এখন কারিগরে! ✨
                
                👗 ক্যাটাগরি: $category
                💰 বিশেষ মূল্য: মাত্র ৳$price!
                🎯 উপলক্ষ: $occasionOrGoal
                
                কেন নিবেন আমাদের এই পণ্যটি?
                ✔️ ১০০% প্রিমিয়াম কোয়ালিটি ও টেকসই ফিনিশিং
                ✔️ ট্রেন্ডি ও আকর্ষণীয় ডিজাইন
                ✔️ সারা বাংলাদেশে ক্যাশ অন ডেলিভারি (COD) সুবিধা
                
                📦 অর্ডার করতে এখনই আমাদের মেসেজ করুন অথবা কল করুন।
                ⚡ সীমিত স্টক! স্টক শেষ হওয়ার আগেই সংগ্রহ করুন।
                
                #Karigor #BangladeshBusiness #${category.replace(" ", "")} #ShopLocal #FashionBD
                """.trimIndent()
            } else {
                """
                ✨ Elevate your lifestyle with $productName! ✨
                
                🏷️ Category: $category
                💵 Special Price: Only ৳$price
                🎯 Campaign: $occasionOrGoal
                
                Why choose this?
                ✔️ Premium craftsmanship & authentic quality
                ✔️ Designed for style and comfort
                ✔️ Cash on Delivery available all over Bangladesh!
                
                🛒 Order now via our online store or send us a direct message!
                
                #Karigor #BDCommerce #${category.replace(" ", "")} #SMEBangladesh
                """.trimIndent()
            }
        }
    }

    private fun callGeminiOrFallback(prompt: String, fallback: () -> String): String {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (_: Exception) { "" }
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return fallback()
        }

        return try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val requestJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(requestJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val bodyString = response.body?.string() ?: ""
                    val root = JSONObject(bodyString)
                    val candidates = root.optJSONArray("candidates")
                    val firstCandidate = candidates?.optJSONObject(0)
                    val content = firstCandidate?.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    val text = parts?.optJSONObject(0)?.optString("text")
                    if (!text.isNullOrBlank()) {
                        text
                    } else {
                        fallback()
                    }
                } else {
                    fallback()
                }
            }
        } catch (_: Exception) {
            fallback()
        }
    }

    private fun generateSmartFallbackAnswer(
        question: String,
        isBangla: Boolean,
        revenue: Double,
        expense: Double,
        profit: Double,
        lowStock: List<ProductEntity>,
        highestExpense: ExpenseEntity?,
        products: List<ProductEntity>
    ): String {
        val q = question.lowercase()

        return if (isBangla) {
            when {
                q.contains("sales") || q.contains("বিক্রি") -> {
                    "📊 আপনার প্রতিষ্ঠানে মোট বিক্রি হয়েছে ৳${String.format("%,.2f", revenue)}। মোট খরচ বাদ দিয়ে আনুমানিক নিট মুনাফা দাঁড়িয়েছে ৳${String.format("%,.2f", profit)}। বিক্রি আরও বাড়াতে নিয়মিত গ্রাহকদের বিশেষ অফার পাঠাতে পারেন।"
                }
                q.contains("stock") || q.contains("স্টক") -> {
                    if (lowStock.isNotEmpty()) {
                        "⚠️ বর্তমানে ${lowStock.size}টি পণ্যের স্টক নির্ধারিত সীমার নিচে রয়েছে: " +
                                lowStock.joinToString { "${it.name} (বাকি ${it.stock}টি)" } +
                                "। গ্রাহক সন্তুষ্টি বজায় রাখতে দ্রুত রিস্টক করুন।"
                    } else {
                        "✅ আলহামদুলিল্লাহ! আপনার সব পণ্যের পর্যাপ্ত স্টক রয়েছে। কোনো পণ্যের স্টক কম নেই।"
                    }
                }
                q.contains("expense") || q.contains("খরচ") -> {
                    if (highestExpense != null) {
                        "💰 আপনার সবচেয়ে বড় খরচের খাত হলো '${highestExpense.category}' (৳${String.format("%,.2f", highestExpense.amount)})। আপনার মোট খরচ ৳${String.format("%,.2f", expense)}। খরচ কমাতে অপ্রয়োজনীয় বিলগুলো নিয়মিত পর্যবেক্ষণ করুন।"
                    } else {
                        "💰 আপনার মোট রেকর্ডকৃত খরচ ৳${String.format("%,.2f", expense)}।"
                    }
                }
                q.contains("বেশি বিক্রি") || q.contains("জনপ্রিয়") || q.contains("product") -> {
                    val top = products.maxByOrNull { it.sellingPrice }
                    "⭐ আপনার প্রিমিয়াম ও আকর্ষণীয় পণ্যের মধ্যে অন্যতম হলো '${top?.name ?: "পাঞ্জাবি"}' (মূল্য ৳${top?.sellingPrice ?: 0.0})। এই পণ্যের প্রমোশন সোশ্যাল মিডিয়ায় জোরদার করুন।"
                }
                else -> {
                    "📈 কারিগর বিশ্লেষণ অনুযায়ী: আপনার বর্তমান মোট বিক্রি ৳${String.format("%,.2f", revenue)}, খরচ ৳${String.format("%,.2f", expense)}, এবং সম্ভাব্য লাভ ৳${String.format("%,.2f", profit)}। আরও প্রবৃদ্ধি অর্জনে কারিগর অনলাইন স্টোরে নিয়মিত নতুন আকর্ষণীয় পণ্য যুক্ত করুন এবং গ্রাহকদের সাথে সম্পর্ক বজায় রাখুন।"
                }
            }
        } else {
            when {
                q.contains("sales") || q.contains("revenue") -> {
                    "📊 Your total recorded sales are ৳${String.format("%,.2f", revenue)}, with an estimated net profit of ৳${String.format("%,.2f", profit)}. Customer demand is strong!"
                }
                q.contains("stock") || q.contains("inventory") -> {
                    if (lowStock.isNotEmpty()) {
                        "⚠️ ${lowStock.size} products are running low: " +
                                lowStock.joinToString { "${it.name} (${it.stock} remaining)" } +
                                ". Consider placing a restock order soon."
                    } else {
                        "✅ All product stocks are in healthy standing above safety thresholds."
                    }
                }
                q.contains("expense") -> {
                    "💰 Total recorded expense is ৳${String.format("%,.2f", expense)}. The largest category is '${highestExpense?.category ?: "Operational"}' at ৳${highestExpense?.amount ?: 0.0}."
                }
                else -> {
                    "📈 Business Snapshot: Revenue ৳${String.format("%,.2f", revenue)} | Expenses ৳${String.format("%,.2f", expense)} | Est. Profit ৳${String.format("%,.2f", profit)}. Focus on marketing top-margin items and keeping inventory refreshed on your Karigor online storefront."
                }
            }
        }
    }
}
