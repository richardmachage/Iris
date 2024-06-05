package com.forsythe.iris.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IrisDao {
    @Insert
    suspend fun insertMessageRecord(messageRecord: MessageRecord):Long

    @Query("SELECT * FROM message_records_tbl ORDER BY timestamp DESC")
    fun getAllMessageRecords() : Flow<List<MessageRecord>>

}