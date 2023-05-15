package com.example.accomodationexpense

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)val id:Int,
    @ColumnInfo(name = "Expenses")val label:String,
    @ColumnInfo(name = "Amount")val amount:Double,
    @ColumnInfo(name = "Details")val description:String): Serializable {
}