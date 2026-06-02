package com.example.alif.domain

import android.content.SharedPreferences
import com.example.alif.domain.model.User
import com.example.alif.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManager @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val userRepository: UserRepository
) {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val scope = CoroutineScope(Dispatchers.Main)

    init {
        val savedUserId = sharedPreferences.getLong(KEY_USER_ID, -1L)
        if (savedUserId != -1L) {
            scope.launch {
                val user = userRepository.getUserById(savedUserId)
                _currentUser.value = user
            }
        }
    }

    fun login(user: User) {
        _currentUser.value = user
        sharedPreferences.edit().putLong(KEY_USER_ID, user.id).apply()
    }

    fun logout() {
        _currentUser.value = null
        sharedPreferences.edit().remove(KEY_USER_ID).apply()
    }

    fun isLoggedIn() = _currentUser.value != null

    companion object {
        private const val KEY_USER_ID = "current_user_id"
    }
}
