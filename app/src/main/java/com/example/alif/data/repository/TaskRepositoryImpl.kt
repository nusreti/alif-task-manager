package com.example.alif.data.repository

import com.example.alif.data.local.dao.CommentDao
import com.example.alif.data.local.dao.TaskDao
import com.example.alif.data.local.entity.toDomain
import com.example.alif.data.local.entity.toEntity
import com.example.alif.domain.model.Comment
import com.example.alif.domain.model.Task
import com.example.alif.domain.repository.TaskRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val commentDao: CommentDao
) : TaskRepository {

    override fun getTasks(userId: Long): Flow<List<Task>> {
        return taskDao.getTasksByUserId(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getTaskById(taskId: Long): Task? {
        val taskEntity = taskDao.getTaskById(taskId) ?: return null
        return taskEntity.toDomain()
    }

    override suspend fun addTask(task: Task): Long {
        return taskDao.insertTask(task.toEntity())
    }

    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task.toEntity())
    }

    override fun getComments(taskId: Long): Flow<List<Comment>> {
        return commentDao.getCommentsByTaskId(taskId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addComment(comment: Comment): Long {
        return commentDao.insertComment(comment.toEntity())
    }
}
