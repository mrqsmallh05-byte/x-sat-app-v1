package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.data.model.PackageTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert
    suspend fun insertTransaction(tx: PackageTransactionEntity): Long

    @Query("SELECT * FROM package_transactions WHERE userPhone = :phone ORDER BY timestamp DESC")
    fun getTransactionsForUser(phone: String): Flow<List<PackageTransactionEntity>>
}
