package com.example.alif.domain.model

data class User(
    val id: Long = 0,
    val login: String,
    val passwordHash: String
)
