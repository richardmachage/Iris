package com.forsythe.iris.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import android.widget.Toast
import com.forsythe.iris.models.TransactionType

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        //var messageBody : String? = null
        val myMessage = MyMessage()
        if (intent?.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            Log.d("receiver", "broadcast fired")
            val bundle = intent.extras
            bundle?.let {
                val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                val fullMessage = StringBuilder()

                for (sms in messages){
                    fullMessage.append(sms.messageBody)
                    myMessage.originatingAddress = sms.originatingAddress.toString()
                }
                myMessage.body = fullMessage.toString()
                Log.d("receiver", "SMS received from ${myMessage.originatingAddress}: ${myMessage.body}")
            }

            //TODO
            /*
            1.extract required info from the body
            -check the message type first, then apply appropriate regex


            2. save the info to local Room
           */
            var type : String?=null
            if (myMessage.originatingAddress == "MPESA") {
                when{
                    myMessage.body.contains("sent to") && myMessage.body.contains("for account") -> type = TransactionType.payBill
                    myMessage.body.contains("sent to", ignoreCase = true) -> type = TransactionType.send
                    myMessage.body.contains("received", ignoreCase = true) -> type = TransactionType.receive
                    myMessage.body.contains("paid to", ignoreCase = true) -> type = TransactionType.till
                    else -> type =  TransactionType.none
                }
            }
            type?.let {
                Log.d("Mpesa", "Transaction Type: $it")
            }?:{
                Log.d("Mpesa", "Transaction Type: null")
            }
            /*Log.d("mpesa details", "Amount Received ${mpesaDetails.person}")
            Log.d("mpesa details", "Balance ${mpesaDetails.balance}")
            Log.d("mpesa details", "Type of transaction ${mpesaDetails.messageType}")
            Log.d("mpesa details", "transaction code ${mpesaDetails.transactionCode}")
            Log.d("mpesa details", "transaction cost ${mpesaDetails.transactionCost}")*/

        }
    }
}


fun extractMpesaDetails(message: String): MpesaDetails? {
    Log.d("extract mpesa details", "function started")

    val sendRegex =
        """([A-Z0-9]+) Confirmed\. Ksh([\d,]+)\.00 sent to ([a-zA-Z\s]+) (\d{10}) on \d{1,2}/\d{1,2}/\d{2} at \d{1,2}:\d{2} (AM|PM)\. New M-PESA balance is Ksh([\d,]+)\.00\. Transaction cost, Ksh([\d,]+)\.00\.""".toRegex()
    val receiveRegex =
        """([A-Z0-9]+) Confirmed\.You have received Ksh([\d,]+)\.00 from ([a-zA-Z\s]+) (\d{10}) on \d{1,2}/\d{1,2}/\d{2} at \d{1,2}:\d{2} (AM|PM)  New M-PESA balance is Ksh([\d,]+)\.([0-9]{2})""".toRegex()

    return when {
        sendRegex.containsMatchIn(message) -> {
            Log.d("extract mpesa details", "money sent")

            val matchResult = sendRegex.find(message)
            matchResult?.let {
                val (transactionCode, amount, sentTo, sendToNumber, _, balance, transactionCost) = it.destructured

                MpesaDetails(
                    transactionCode = transactionCode,
                    amount = amount.replace(",", "").toDouble(),
                    person = sentTo.trim(),
                    personNumber = sendToNumber,
                    balance = balance.replace(",", "").toDouble(),
                    transactionCost = transactionCost.replace(",", "").toDouble(),
                    messageType = "send"
                )
            }
        }

        receiveRegex.containsMatchIn(message) -> {
            Log.d("extract mpesa details", "money received")
            val matchResult = receiveRegex.find(message)
            matchResult?.let {
                val (transactionCode, amount, receivedFrom, receivedFromNumber, _, balance, cents) = it.destructured

                MpesaDetails(
                    transactionCode = transactionCode,
                    amount = amount.replace(",", "").toDouble(),
                    person = receivedFrom.trim(),
                    personNumber = receivedFromNumber,
                    balance = balance.replace(",", "").toDouble() + "0.$cents".toDouble(),
                    messageType = "receive"
                )
            }
        }

        else -> {
            Log.d("extract mpesa details", "no regex")
            MpesaDetails(
                transactionCode = "none",
                amount = 0.0,
                person = "none",
                personNumber = "none",
                balance = 0.0,
                messageType = "none"
            )
        }
    }

}

data class MpesaDetails(
    val transactionCode: String,
    val amount: Double,
    val person: String,
    val personNumber: String,
    val balance: Double,
    var transactionCost: Double = 0.0,
    val messageType: String
)

data class MyMessage(
    var body: String = "",
    var originatingAddress: String = ""
)
