package com.forsythe.iris.utils

import android.content.Context
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager

fun getReceivingAddress(context: Context, subscriptionId : Int) : String{
    val subscriptionManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
    return subscriptionManager.getPhoneNumber(subscriptionId)
}
