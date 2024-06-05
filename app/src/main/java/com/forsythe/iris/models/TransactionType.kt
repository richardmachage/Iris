package com.forsythe.iris.models
class TransactionType{
    companion object{
        const val none = "not specified"
        const val send  = "send money"
        const val receive  = "receive money"
        const val payBill  = "pay bill"
        const val till = "till"
    }
}
