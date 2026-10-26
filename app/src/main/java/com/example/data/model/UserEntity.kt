package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["phone"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val phone: String,
    val region: String,
    val phoneModel: String,
    val password: String,
    val balanceMb: Long = 200,
    val consumedMb: Double = 0.0,
    val expiryTimestamp: Long,
    val registeredAt: Long = System.currentTimeMillis(),
    val isBanned: Boolean = false,
    val deviceId: String = "",
    val activePackageName: String = "المجاني 200MB",
    val hasClaimedFreeGift: Boolean = true
)
