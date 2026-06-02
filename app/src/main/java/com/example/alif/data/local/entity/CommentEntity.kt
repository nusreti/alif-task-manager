package com.example.alif.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.alif.domain.model.Comment

@Entity(
    tableName = "comments",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: Long,
    val text: String,
    val createdAt: Long
)

fun CommentEntity.toDomain() = Comment(id, taskId, text, createdAt)
fun Comment.toEntity() = CommentEntity(id, taskId, text, createdAt)
