package com.forsythe.iris.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.provider.Telephony.Sms
import android.util.Log
import android.widget.Toast
import com.forsythe.iris.constants.payBillRegexPattern
import com.forsythe.iris.constants.receiveRgexPattern
import com.forsythe.iris.constants.sendRegexPattern
import com.forsythe.iris.constants.tillRegexPattern
import com.forsythe.iris.data.room.IrisDao
import com.forsythe.iris.data.room.IrisDatabase
import com.forsythe.iris.data.room.MessageRecord
import com.forsythe.iris.models.MyMessage
import com.forsythe.iris.models.PayBillMessage
import com.forsythe.iris.models.ReceiveMessage
import com.forsythe.iris.models.SendMessage
import com.forsythe.iris.models.TillMessage
import com.forsythe.iris.models.TransactionType
import com.forsythe.iris.utils.getReceivingAddress
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.file.Files.find
import javax.inject.Inject

@AndroidEntryPoint
class SmsReceiver (): BroadcastReceiver() {
    @Inject lateinit var irisDao: IrisDao
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("receiver", "broadcast fired")

        var subId : Int? = null
        Sms.Intents.getMessagesFromIntent(intent)?.let {
             subId = intent?.extras?.getInt("subscription",-1)?:-1
            Log.d("receiver", "subscription Id = $subId")
        }
        //var messageBody : String? = null
        val myMessage = MyMessage(receivingAddress = getReceivingAddress(context = context!!, subscriptionId = subId?:-1))
        if (intent?.action == Sms.Intents.SMS_RECEIVED_ACTION) {
            val bundle = intent.extras
            bundle?.let {
                val messages = Sms.Intents.getMessagesFromIntent(intent)
                val fullMessage = StringBuilder()

                for (sms in messages){
                    fullMessage.append(sms.messageBody)
                    myMessage.originatingAddress = sms.originatingAddress.toString()
                   // subscriptionId = sms.serviceCenterAddress
                }
                myMessage.body = fullMessage.toString()

                Log.d("receiver", "SMS received from ${myMessage.originatingAddress}: ${myMessage.body} subId = $subId")
            }
            //var type : String?=null
            var messageRecord : MessageRecord? = null
            if (myMessage.originatingAddress == "MPESA") {
                when{
                    (myMessage.body.contains("sent to", ignoreCase = true) && myMessage.body.contains("for account", ignoreCase = true)) ->{
                        Log.d("Mpesa", "message contains both \"sent to\"  and \"for account\" ")
                        //// type = TransactionType.payBill
                        val matcher = payBillRegexPattern.matcher(myMessage.body)
                        if (matcher.find()){
                            Log.d("matcher", "message matches \"paybill\" pattern ")

                            val payBillMessage = PayBillMessage(
                                transactionId = matcher.group(1)!!,
                                amountSent = matcher.group(2)?.replace(",", "")!!.toDouble(),
                                paidTo = matcher.group(3)!!.trim(),
                                accountName = matcher.group(4)!!.trim(),
                                date = matcher.group(5)!!,
                                time = matcher.group(6)!!,
                                newBalance = matcher.group(7)!!.replace(",", "").toDouble(),
                                transactionCost = matcher.group(8)!!.replace(",", "").toDouble(),
                                dailyLimit = matcher.group(9)!!.replace(",", "").toDouble()
                            )

                            messageRecord = MessageRecord(
                                transactionCode = payBillMessage.transactionId,
                                transactionType = TransactionType.payBill,
                                amount = payBillMessage.amountSent,
                                accountBalance = payBillMessage.newBalance,
                                transactionCost = payBillMessage.transactionCost,
                                timestamp = System.currentTimeMillis(),
                                messageReceivedOn = myMessage.receivingAddress
                            )

                        }
                        else{
                            Log.d("matcher", "message doesnt matche\"paybill\" patterns ")

                        }
                    }
                    myMessage.body.contains("sent to", ignoreCase = true) -> {
                        Log.d("Mpesa", "message contains  \"sent to\"  ")
                        //type = TransactionType.send
                        val matcher = sendRegexPattern.matcher(myMessage.body)
                        if (matcher.find()){
                            Log.d("matcher", "message matches \"sent\" pattern ")
                            val sendMessage = SendMessage(
                                transactionId = matcher.group(1)!!,
                                amountSent = matcher.group(2)!!.replace(",", "").toDouble(),
                                recipientName = matcher.group(3)!!.trim(),
                                recipientPhoneNumber = matcher.group(4)!!,
                                dateTime = "${matcher.group(5)} ${matcher.group(6)}",
                                newMpesaBalance = matcher.group(7)!!.replace(",", "").toDouble(),
                                transactionCost = matcher.group(8)!!.replace(",", "").toDouble(),
                                dailyTransactionLimit = matcher.group(9)!!.replace(",", "").toDouble()
                            )
                            messageRecord = MessageRecord(
                                transactionCode = sendMessage.transactionId,
                                transactionType = TransactionType.send,
                                amount = sendMessage.amountSent,
                                accountBalance = sendMessage.newMpesaBalance,
                                transactionCost = sendMessage.transactionCost,
                                timestamp = System.currentTimeMillis(),
                                messageReceivedOn = myMessage.receivingAddress

                            )
                        }
                        else{
                            Log.d("matcher", "message doesnt match \"sent\" pattern ")
                        }
                    }
                    myMessage.body.contains("received", ignoreCase = true) -> {
                        Log.d("Mpesa", "message contains  \"received\"  ")
                        // type = TransactionType.receive
                        val matcher = receiveRgexPattern.matcher(myMessage.body)
                        if (matcher.find()){
                            Log.d("matcher", "message  matches \"received\" pattern ")

                            val receiveMessage = ReceiveMessage(
                                transactionId = matcher.group(1)!!,
                                amountReceived = matcher.group(2)!!.replace(",", "").toDouble(),
                                senderName = matcher.group(3)!!.trim(),
                                senderNumber = matcher.group(4)!!,
                                date = matcher.group(5)!!,
                                time = matcher.group(6)!!,
                                newBalance = matcher.group(7)!!.replace(",", "").toDouble()
                            )

                            messageRecord = MessageRecord(
                                transactionCost = 0.0,
                                transactionType = TransactionType.receive,
                                transactionCode = receiveMessage.transactionId,
                                amount = receiveMessage.amountReceived,
                                accountBalance = receiveMessage.newBalance,
                                timestamp = System.currentTimeMillis(),
                                messageReceivedOn = myMessage.receivingAddress

                            )
                        }
                        else{
                            Log.d("matcher", "message doesnt match \"receive\" pattern ")
                        }

                    }
                    myMessage.body.contains("paid to", ignoreCase = true) ->{
                        Log.d("Mpesa", "message contains  \"paid to\"  ")

                        // type = TransactionType.till
                        val matcher = tillRegexPattern.matcher(myMessage.body)
                        if (matcher.find()){
                            Log.d("matcher", "message matches \"till\" pattern ")

                            val tillMessage = TillMessage(
                                transactionId = matcher.group(1)!!,
                                amountPaid = matcher.group(2)!!.replace(",", "").toDouble(),
                                paidTo = matcher.group(3)!!,
                                date = matcher.group(4)!!,
                                time = matcher.group(5)!!,
                                newBalance = matcher.group(6)!!.replace(",", "").toDouble(),
                                transactionCost = matcher.group(7)!!.replace(",", "").toDouble(),
                                dailyLimit = matcher.group(8)!!.replace(",", "").toDouble()
                            )

                            messageRecord = MessageRecord(
                                transactionCode = tillMessage.transactionId,
                                amount = tillMessage.amountPaid,
                                transactionType = TransactionType.till,
                                accountBalance = tillMessage.newBalance,
                                transactionCost = tillMessage.transactionCost,
                                timestamp = System.currentTimeMillis(),
                                messageReceivedOn = myMessage.receivingAddress

                            )
                        }
                        else{
                            Log.d("matcher", "message doesn't match \"till\" pattern ")
                        }
                    }
                    else -> {
                        //type =  TransactionType.none
                       // Log.d("Mpesa", "message contains  \"none pattern\"  ")

                       /* messageRecord = MessageRecord(
                            transactionCode = "none",
                            amount = 0.0,
                            transactionType = TransactionType.none,
                            accountBalance = 0.0,
                            transactionCost =  0.0,
                            timestamp = System.currentTimeMillis(),
                            messageReceivedOn = null

                        )*/
                    }
                }
            }

            messageRecord?.let {data->
                Log.d("Mpesa", "Transaction Type: ${data.transactionType}")
                CoroutineScope(Dispatchers.IO).launch {
                    val insertRecord = irisDao.insertMessageRecord(data)
                    if (insertRecord>0){
                        Log.d("InsertRecord", "record insert success: ")
                    }else{
                        Log.d("InsertRecord", "failed to insert: ")
                    }
                }

            }?:{
                Log.d("Mpesa", "Transaction Type: null")
            }

        }
    }
}




