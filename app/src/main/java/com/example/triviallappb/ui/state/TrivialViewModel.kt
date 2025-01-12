package com.example.triviallappb.ui.state

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.triviallappb.model.Question

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

    private val gameFunctions = GameFunctions(this)
    private val uiFunctions = UiFunctions(this)

    // Llamadas a funciones del juego y UI
    fun fetchQuestions(amount: Int, difficulty: String = "easy", type: String = "multiple", retryCount: Int = 3) {
        uiFunctions.fetchQuestions(amount, difficulty, type, retryCount)
    }

    fun startGame(questionCount: Int) = gameFunctions.startGame(questionCount)
    fun getCorrectAnswerIndex(question: Question, shuffledOptions: List<String>): Int = gameFunctions.getCorrectAnswerIndex(question, shuffledOptions)
    fun submitAnswer(selectedIndex: Int, shuffledOptions: List<String>) = gameFunctions.submitAnswer(selectedIndex, shuffledOptions)
    fun moveToNextQuestion() = gameFunctions.moveToNextQuestion()
    fun checkAndUpdateRecord(percentage: Int) = gameFunctions.checkAndUpdateRecord(percentage)
    fun updateRecordAndNavigate() = gameFunctions.updateRecordAndNavigate()

    // Funciones auxiliares para manejar el estado del ViewModel
    fun setTrivialUiState(state: TrivialUiState) {
        _trivialUiState.value = state
    }

    fun clearQuestions() {
        _questions.clear()
    }

    fun addQuestions(questionsList: List<Question>) {
        _questions.addAll(questionsList)
    }

    fun setTotalQuestions(value: Int) {
        _totalQuestions.value = value
    }

    fun updateCurrentQuestionIndex(value: Int) {
        _currentQuestionIndex.value = value
    }

    fun updateScore(value: Int) {
        _score.value = value
    }

    fun updateRecord(value: Int) {
        _record.value = value
    }

    fun setGameOver(value: Boolean) {
        _gameOver.value = value
    }

    fun setAnswerShown(value: Boolean) {
        _answerShown.value = value
    }
}