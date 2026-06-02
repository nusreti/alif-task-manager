package com.example.alif.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.alif.data.local.dao.CommentDao
import com.example.alif.data.local.dao.TaskDao
import com.example.alif.data.local.dao.UserDao
import com.example.alif.data.local.entity.CommentEntity
import com.example.alif.data.local.entity.TaskEntity
import com.example.alif.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, TaskEntity::class, CommentEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao
    abstract fun commentDao(): CommentDao
}
