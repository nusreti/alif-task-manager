package com.example.alif.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.alif.domain.model.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val login: String,
    val passwordHash: String
)

fun UserEntity.toDomain() = User(id, login, passwordHash)
fun User.toEntity() = UserEntity(id, login, passwordHash)
