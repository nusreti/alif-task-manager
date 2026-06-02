package com.example.alif.domain.repository

import com.example.alif.domain.model.Comment
import com.example.alif.domain.model.Task
import com.example.alif.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasks(userId: Long): Flow<List<Task>>
    suspend fun getTaskById(taskId: Long): Task?
    suspend fun addTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    
    fun getComments(taskId: Long): Flow<List<Comment>>
    suspend fun addComment(comment: Comment): Long
}
