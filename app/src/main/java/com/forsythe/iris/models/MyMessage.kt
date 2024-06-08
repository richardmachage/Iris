package com.forsythe.iris.models

data class MyMessage(
    var body: String = "",
    var originatingAddress: String = "",
    var receivingAddress : String? = null
)