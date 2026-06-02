package com.example.alif.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alif.domain.AuthManager
import com.example.alif.domain.repository.UserRepository
import com.example.alif.util.HashUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository,
    val authManager: AuthManager
) : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state

    fun login(login: String, pass: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            val hash = HashUtils.sha256(pass)
            userRepository.login(login, hash)
                .onSuccess { user ->
                    authManager.login(user)
                    _state.value = AuthState.Success
                }
                .onFailure { error ->
                    _state.value = AuthState.Error(error.message ?: "Unknown error")
                }
        }
    }

    fun register(login: String, pass: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            val hash = HashUtils.sha256(pass)
            userRepository.register(login, hash)
                .onSuccess { user ->
                    authManager.login(user)
                    _state.value = AuthState.Success
                }
                .onFailure { error ->
                    _state.value = AuthState.Error(error.message ?: "Unknown error")
                }
        }
    }

    fun resetState() {
        _state.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}
