package com.drew.themoviedatabase.screens.Profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drew.themoviedatabase.POJO.UserDetails
import com.drew.themoviedatabase.repository.room.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val getUser: StateFlow<UserDetails?> =
        userRepository.getUser()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

    fun insert(userDetails: UserDetails) {
        viewModelScope.launch {
            userRepository.insert(userDetails)
        }
    }

    fun delete(userDetails: UserDetails) {
        viewModelScope.launch {
            userRepository.delete(userDetails)
        }
    }
    fun update(userDetails: UserDetails) {
        viewModelScope.launch {
            userRepository.update(userDetails)
        }
    }

}