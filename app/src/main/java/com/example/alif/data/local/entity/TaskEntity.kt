package com.example.alif.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.alif.domain.model.Task
import com.example.alif.domain.model.TaskStatus

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val title: String,
    val description: String,
    val deadline: Long,
    val status: TaskStatus
)

fun TaskEntity.toDomain() = Task(
    id = id,
    userId = userId,
    title = title,
    description = description,
    deadline = deadline,
    status = status
)

fun Task.toEntity() = TaskEntity(
    id = id,
    userId = userId,
    title = title,
    description = description,
    deadline = deadline,
    status = status
)
