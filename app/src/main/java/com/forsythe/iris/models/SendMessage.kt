package com.forsythe.iris.models

data class SendMessage(
    val transactionId: String,
    val amountSent: Double,
    val recipientName: String,
    val recipientPhoneNumber: String,
    val dateTime: String,
    val newMpesaBalance: Double,
    val transactionCost: Double,
    val dailyTransactionLimit: Double
)
