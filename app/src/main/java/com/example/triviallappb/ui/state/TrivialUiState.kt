package com.example.triviallappb.ui.state

import com.example.triviallappb.model.Question

sealed class TrivialUiState {
    object Loading : TrivialUiState()
    data class Success(val questions: List<Question>) : TrivialUiState()
    object Error : TrivialUiState()
}

