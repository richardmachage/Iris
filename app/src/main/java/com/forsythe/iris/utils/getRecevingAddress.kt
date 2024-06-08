package com.forsythe.iris.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import androidx.core.app.ActivityCompat

fun getReceivingAddress(context: Context, subscriptionId : Int) : String?{
    val subscriptionManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
    return if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_PHONE_NUMBERS
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return     subscriptionManager.getPhoneNumber(subscriptionId)
    }else null
}
