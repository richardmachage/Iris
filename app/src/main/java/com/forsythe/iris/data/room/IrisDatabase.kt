package com.forsythe.iris.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.util.foreignKeyCheck


@Database(entities = [MessageRecord::class], version = 1)
abstract class IrisDatabase : RoomDatabase() {
    abstract fun irisDao(): IrisDao

    companion object {
        private var INSTANCE: IrisDatabase? = null
        fun getInstance(context: Context): IrisDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context = context.applicationContext,
                        klass = IrisDatabase::class.java,
                        name = "iris_database"
                    )
                        .fallbackToDestructiveMigrationFrom()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

}