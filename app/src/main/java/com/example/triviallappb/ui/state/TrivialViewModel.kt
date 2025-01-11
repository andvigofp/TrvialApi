package com.example.triviallappb.ui.state

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triviallappb.model.Question
import com.example.triviallappb.model.RetrofitClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.pow

class TrivialViewModel : ViewModel() {

    private val _trivialUiState = mutableStateOf<TrivialUiState>(TrivialUiState.Loading)
    val trivialUiState: State<TrivialUiState> get() = _trivialUiState

    private val _questions = mutableStateListOf<Question>()
    val questions: List<Question> get() = _questions

    private val _totalQuestions = mutableStateOf(0)
    val totalQuestions: Int get() = _totalQuestions.value

    private val _currentQuestionIndex = mutableStateOf(0)
    var currentQuestionIndex: Int
        get() = _currentQuestionIndex.value
        private set(value) {
            _currentQuestionIndex.value = value
        }

    private val _score = mutableStateOf(0)
    var score: Int
        get() = _score.value
        private set(value) {
            _score.value = value
        }

    private val _record = mutableStateOf(0)
    val record: Int get() = _record.value

    private val _gameOver = mutableStateOf(false)
    val gameOver: Boolean get() = _gameOver.value

    private val _answerShown = mutableStateOf(false)
    val answerShown: Boolean get() = _answerShown.value

    // Llamada dentro de ViewModel
    fun fetchQuestions(amount: Int, difficulty: String = "easy", type: String = "multiple", retryCount: Int = 3) {
        viewModelScope.launch {
            _trivialUiState.value = TrivialUiState.Loading
            try {
                Log.d("TrivialViewModel", "Fetching questions with amount: $amount, difficulty: $difficulty, type: $type")
                val response = RetrofitClient.apiService.getQuestions(amount, difficulty, type)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("TrivialViewModel", "API Response: $responseBody")
                    if (responseBody?.response_code == 0) {
                        val questionsList = responseBody.results
                        Log.d("TrivialViewModel", "Questions fetched: ${questionsList.size}")
                        if (!questionsList.isNullOrEmpty()) {
                            _questions.clear()
                            _questions.addAll(questionsList)
                            _totalQuestions.value = questionsList.size
                            _trivialUiState.value = TrivialUiState.Success(questionsList)
                        } else {
                            _trivialUiState.value = TrivialUiState.Error
                            Log.e("TrivialViewModel", "No questions found")
                        }
                    } else {
                        _trivialUiState.value = TrivialUiState.Error
                        Log.e("TrivialViewModel", "API Response Code: ${responseBody?.response_code}")
                    }
                } else {
                    if (response.code() == 429 && retryCount > 0) {
                        // Esperar un tiempo antes de intentar nuevamente (exponencial backoff)
                        val backoffTime = (2.0.pow((3 - retryCount).toDouble()) * 1000).toLong()
                        delay(backoffTime)
                        fetchQuestions(amount, difficulty, type, retryCount - 1)
                    } else {
                        _trivialUiState.value = TrivialUiState.Error
                        Log.e("TrivialViewModel", "API Response Error: ${response.message()} (Code: ${response.code()})")
                    }
                }
            } catch (e: Exception) {
                _trivialUiState.value = TrivialUiState.Error
                Log.e("TrivialViewModel", "Error fetching questions: ${e.message}")
            }
        }
    }

    fun startGame(questionCount: Int) {
        _currentQuestionIndex.value = 0
        score = 0
        _gameOver.value = false
        _answerShown.value = false
    }

    fun submitAnswer(selectedIndex: Int) {
        if (_answerShown.value) return

        val currentQuestion = questions[currentQuestionIndex]
        val allAnswers = currentQuestion.incorrect_answers + currentQuestion.correct_answer
        val correctAnswerIndex = allAnswers.indexOf(currentQuestion.correct_answer)

        if (selectedIndex == correctAnswerIndex) {
            score++
        }

        _answerShown.value = true
    }

    fun moveToNextQuestion() {
        if (currentQuestionIndex < questions.lastIndex) {
            _currentQuestionIndex.value++
        } else {
            _gameOver.value = true
            val percentage = (score * 100 / totalQuestions).coerceAtLeast(0)
            checkAndUpdateRecord(percentage)
        }
        _answerShown.value = false
    }

    fun checkAndUpdateRecord(percentage: Int) {
        if (percentage > _record.value) {
            _record.value = percentage
        }
    }

    fun updateRecordAndNavigate() {
        val percentage = (score * 100) / totalQuestions
        if (percentage > _record.value) {
            _record.value = percentage
        }
    }
}


















