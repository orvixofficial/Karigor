package com.example.karigor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.karigor.data.local.ExpenseEntity
import com.example.karigor.data.model.AppLanguage
import com.example.karigor.ui.theme.*

@Composable
fun ExpensesScreen(
    language: AppLanguage,
    expenses: List<ExpenseEntity>,
    onAddExpenseClick: () -> Unit,
    onDeleteExpense: (ExpenseEntity) -> Unit
) {
    val isBn = language == AppLanguage.BANGLA
    val currency = "৳"

    val totalExpense = expenses.sumOf { it.amount }
    val categoryTotals = expenses.groupBy { it.category }
        .mapValues { (_, list) -> list.sumOf { it.amount } }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddExpenseClick,
                containerColor = KarigorError,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("add_expense_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(KarigorBackgroundLight)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header stats
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isBn) "মোট পরিচালন খরচ (Total Expenses)" else "Total Operational Expenses",
                                fontSize = 13.sp,
                                color = KarigorSlateLight
                            )
                            Text(
                                text = "$currency${String.format("%,.0f", totalExpense)}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = KarigorError
                            )
                        }

                        FilledTonalButton(
                            onClick = onAddExpenseClick,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = KarigorError.copy(alpha = 0.12f),
                                contentColor = KarigorError
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = if (isBn) "খরচ লিখুন" else "Add Expense", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = KarigorBorder)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isBn) "খাতভিত্তিক ব্যয় বণ্টন:" else "Category Breakdown:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = KarigorSlateLight
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    categoryTotals.entries.take(3).forEach { (cat, amount) ->
                        val fraction = if (totalExpense > 0) (amount / totalExpense).toFloat() else 0f
                        Column(modifier = Modifier.padding(vertical = 3.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(cat, fontSize = 12.sp, color = KarigorNavy)
                                Text("$currency${String.format("%,.0f", amount)} (${(fraction * 100).toInt()}%)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            LinearProgressIndicator(
                                progress = { fraction },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = KarigorError,
                                trackColor = KarigorBorder
                            )
                        }
                    }
                }
            }

            // List Header
            Text(
                text = if (isBn) "খরচের ভাউচার তালিকা" else "Expense Vouchers & Logs",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = KarigorNavy
            )

            if (expenses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isBn) "এখনও কোনো খরচের রেকর্ড যুক্ত করা হয়নি।" else "No expenses recorded yet.",
                        color = KarigorSlateLight
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(expenses) { expense ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(KarigorError.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Receipt,
                                            contentDescription = null,
                                            tint = KarigorError,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Text(
                                            text = expense.category,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = KarigorNavy
                                        )
                                        Text(
                                            text = "${expense.date} ${if (expense.notes.isNotBlank()) "• ${expense.notes}" else ""}",
                                            fontSize = 11.sp,
                                            color = KarigorSlateLight
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "$currency${String.format("%,.0f", expense.amount)}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = KarigorError
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(
                                        onClick = { onDeleteExpense(expense) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = KarigorSlateLight)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
