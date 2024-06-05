package com.forsythe.iris.models

data class PayBillMessage(
    val transactionId: String,
    val amountSent: Double,
    val paidTo: String,
    val accountName: String,
    val date: String,
    val time: String,
    val newBalance: Double,
    val transactionCost: Double,
    val dailyLimit: Double
)
