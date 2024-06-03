package com.forsythe.iris.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import android.widget.Toast

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION){
            Log.d("receiver", "broadcast fired")
            val bundle = intent.extras
            bundle?.let {
                val pdus = bundle.get("pdus") as Array<*>

                for (pdu in pdus){
                    val msg = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                    for(sms in msg){
                        val msgBody = sms.messageBody
                        val msgAddress  = sms.originatingAddress

                        Log.d("receiver", "SMS received from $msgAddress: $msgBody")

                    }
                }
            }
        }
    }
}