package com.example.alif.presentation.tasks

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alif.domain.model.Comment
import com.example.alif.domain.model.Task
import com.example.alif.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _task = mutableStateOf<Task?>(null)
    val task: State<Task?> = _task

    val comments: StateFlow<List<Comment>> = savedStateHandle.getStateFlow("taskId", -1L)
        .flatMapLatest { taskId ->
            if (taskId != -1L) taskRepository.getComments(taskId) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        savedStateHandle.get<Long>("taskId")?.let { taskId ->
            viewModelScope.launch {
                _task.value = taskRepository.getTaskById(taskId)
            }
        }
    }

    fun addComment(text: String) {
        val taskId = _task.value?.id ?: return
        if (text.isBlank()) return
        viewModelScope.launch {
            val comment = Comment(
                taskId = taskId,
                text = text,
                createdAt = System.currentTimeMillis()
            )
            taskRepository.addComment(comment)
        }
    }
}
