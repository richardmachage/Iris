package com.forsythe.iris.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import android.widget.Toast

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        //var messageBody : String? = null
        val myMessage = MyMessage()
        if (intent?.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            Log.d("receiver", "broadcast fired")
            val bundle = intent.extras
            bundle?.let {
                val pdus = bundle.get("pdus") as Array<*>

                for (pdu in pdus) {
                    val msg = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                    for (sms in msg) {
                        myMessage.body = sms.messageBody
                        myMessage.originatingAddress = sms.originatingAddress.toString()

                    }
                }
                Log.d("receiver", "SMS received from ${myMessage.originatingAddress}: ${myMessage.body}")

            }

            //TODO
            /*
            1.extract required info from the body
            2. save the info to local Room
           */
            if (myMessage.originatingAddress == "MPESA"){
                val mpesaDetails = extractMpesaDetails(myMessage.body)
                Log.d("receiver", "SMS received from ${myMessage.originatingAddress}: ${myMessage.body}")

            }
        }
    }
}

fun extractMpesaDetails(message: String): MpesaDetails? {
    val sendRegex =
        """([A-Z0-9]+) Confirmed\. Ksh([\d,]+)\.00 sent to ([a-zA-Z\s]+) (\d{10}) on \d{1,2}/\d{1,2}/\d{2} at \d{1,2}:\d{2} (AM|PM)\. New M-PESA balance is Ksh([\d,]+)\.00\. Transaction cost, Ksh([\d,]+)\.00\.""".toRegex()
    val receiveRegex =
        """([A-Z0-9]+) Confirmed\.You have received Ksh([\d,]+)\.00 from ([a-zA-Z\s]+) (\d{10}) on \d{1,2}/\d{1,2}/\d{2} at \d{1,2}:\d{2} (AM|PM)  New M-PESA balance is Ksh([\d,]+)\.([0-9]{2})""".toRegex()

    return when {
        sendRegex.containsMatchIn(message) -> {
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

        else -> null
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
