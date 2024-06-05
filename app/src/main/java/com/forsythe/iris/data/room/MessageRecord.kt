package com.forsythe.iris.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "message_records_tbl")
data class MessageRecord(
    @PrimaryKey(autoGenerate = true) val recordId : Int = 0,
    val transactionCode : String,
    val transactionType : String,
    val amount : Double,
    val accountBalance : Double,
    val transactionCost : Double = 0.0,
    val timestamp : Long
)
