package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "package_transactions")
data class PackageTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userPhone: String,
    val packageName: String,
    val amountSudanPounds: Long,
    val dataMb: Long,
    val validityDays: Int,
    val receiptImageUri: String? = null,
    val status: String = "CONFIRMED", // "CONFIRMED", "PENDING"
    val timestamp: Long = System.currentTimeMillis()
)
