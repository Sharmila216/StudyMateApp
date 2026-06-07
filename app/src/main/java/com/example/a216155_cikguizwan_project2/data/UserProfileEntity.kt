package com.example.a216155_cikguizwan_project2.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val firstName: String = "",
    val lastName: String = "",
    val profilePicturePath: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)