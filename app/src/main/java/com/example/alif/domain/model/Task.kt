package com.example.alif.domain.model

data class Task(
    val id: Long = 0,
    val userId: Long,
    val title: String,
    val description: String,
    val deadline: Long, // Timestamp
    val status: TaskStatus = TaskStatus.NEW,
    val comments: List<Comment> = emptyList()
)

data class Comment(
    val id: Long = 0,
    val taskId: Long,
    val text: String,
    val createdAt: Long // Timestamp
)
