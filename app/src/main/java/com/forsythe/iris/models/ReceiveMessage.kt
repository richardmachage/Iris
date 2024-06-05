package com.forsythe.iris.models

data class ReceiveMessage(
    val transactionId : String,
    val  amountReceived : Double,
    val senderName : String,
    val senderNumber : String,
    val date : String,
    val time : String,
    val newBalance : Double
)
