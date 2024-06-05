package com.forsythe.iris.models

data class TillMessage(
    val transactionId : String,
    val amountPaid : Double,
    val paidTo : String,
    val date : String,
    val time : String,
    val newBalance : Double,
    val transactionCost : Double,
    val dailyLimit : Double,
)
