package com.example.a216155_cikguizwan_lab04

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class StudyMateViewModel : ViewModel() {

    // In StudyMateViewModel.kt
    // Change this line:
    private val _uiState = mutableStateOf(UiState(firstName = "", lastName = ""))
    val uiState: State<UiState> = _uiState
    // Inside StudyMateViewModel.kt


    fun updateUserProfile(newFirst: String, newLast: String) {

        _uiState.value = _uiState.value.copy(
            firstName = newFirst,
            lastName = newLast
        )
    }
}