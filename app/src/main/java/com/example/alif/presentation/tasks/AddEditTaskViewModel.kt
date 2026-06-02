package com.example.alif.presentation.tasks

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.alif.domain.AuthManager
import com.example.alif.domain.model.Task
import com.example.alif.domain.model.TaskStatus
import com.example.alif.domain.repository.TaskRepository
import com.example.alif.worker.DeadlineWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val authManager: AuthManager,
    private val workManager: WorkManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _taskTitle = mutableStateOf("")
    val taskTitle: State<String> = _taskTitle

    private val _taskDescription = mutableStateOf("")
    val taskDescription: State<String> = _taskDescription

    private val _taskDeadline = mutableStateOf(System.currentTimeMillis())
    val taskDeadline: State<Long> = _taskDeadline

    private val _taskStatus = mutableStateOf(TaskStatus.NEW)
    val taskStatus: State<TaskStatus> = _taskStatus

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentTaskId: Long? = null

    init {
        savedStateHandle.get<Long>("taskId")?.let { taskId ->
            if (taskId != -1L) {
                currentTaskId = taskId
                viewModelScope.launch {
                    taskRepository.getTaskById(taskId)?.let { task ->
                        _taskTitle.value = task.title
                        _taskDescription.value = task.description
                        _taskDeadline.value = task.deadline
                        _taskStatus.value = task.status
                    }
                }
            }
        }
    }

    fun onTitleChange(title: String) {
        _taskTitle.value = title
    }

    fun onDescriptionChange(description: String) {
        _taskDescription.value = description
    }

    fun onDeadlineChange(deadline: Long) {
        _taskDeadline.value = deadline
    }

    fun onStatusChange(status: TaskStatus) {
        _taskStatus.value = status
    }

    fun saveTask() {
        viewModelScope.launch {
            if (_taskTitle.value.isBlank()) {
                _eventFlow.emit(UiEvent.ShowSnackbar("Title cannot be empty"))
                return@launch
            }
            val userId = authManager.currentUser.value?.id ?: return@launch
            val task = Task(
                id = currentTaskId ?: 0,
                userId = userId,
                title = _taskTitle.value,
                description = _taskDescription.value,
                deadline = _taskDeadline.value,
                status = _taskStatus.value
            )
            if (currentTaskId == null) {
                taskRepository.addTask(task)
            } else {
                taskRepository.updateTask(task)
            }
            scheduleNotification(task)
            _eventFlow.emit(UiEvent.SaveTask)
        }
    }

    private fun scheduleNotification(task: Task) {
        val delay = task.deadline - System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1)
        if (delay > 0) {
            val workRequest = OneTimeWorkRequestBuilder<DeadlineWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(workDataOf("taskTitle" to task.title))
                .build()
            workManager.enqueue(workRequest)
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object SaveTask : UiEvent()
    }
}
