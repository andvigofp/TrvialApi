package com.example.triviallappb.ui.state

import com.example.triviallappb.model.Question
import com.example.triviallappb.ui.state.TrivialViewModel


class GameFunctions(private val viewModel: TrivialViewModel) {

    // Inicia el juego con un número específico de preguntas
    fun startGame(questionCount: Int) {
        viewModel.fetchQuestions(questionCount) // Vuelve a cargar preguntas nuevas
        viewModel.updateCurrentQuestionIndex(0)
        viewModel.updateScore(0)
        viewModel.setGameOver(false)
        viewModel.setAnswerShown(false)
    }

    // Obtiene el índice de la respuesta correcta
    fun getCorrectAnswerIndex(question: Question, shuffledOptions: List<String>): Int {
        return shuffledOptions.indexOf(question.correct_answer)
    }

    // Envía la respuesta seleccionada por el usuario
    fun submitAnswer(selectedIndex: Int, shuffledOptions: List<String>) {
        val currentQuestion = viewModel.questions[viewModel.currentQuestionIndex]

        if (!viewModel.answerShown) {
            val correctAnswerIndex = getCorrectAnswerIndex(currentQuestion, shuffledOptions)
            if (selectedIndex == correctAnswerIndex) {
                viewModel.updateScore(viewModel.score + 1)
            }
            viewModel.setAnswerShown(true)
        }
    }

    // Avanza a la siguiente pregunta o finaliza el juego
    fun moveToNextQuestion() {
        if (viewModel.currentQuestionIndex < viewModel.questions.lastIndex) {
            viewModel.updateCurrentQuestionIndex(viewModel.currentQuestionIndex + 1)
        } else {
            viewModel.setGameOver(true)
            val percentage = (viewModel.score * 100 / viewModel.totalQuestions).coerceAtLeast(0)
            checkAndUpdateRecord(percentage)
        }
        viewModel.setAnswerShown(false)
    }

    // Método público para actualizar el récord
    fun checkAndUpdateRecord(percentage: Int) {
        if (percentage > viewModel.record) {
            viewModel.updateRecord(percentage)
        }
    }

    fun updateRecordAndNavigate() {
        val percentage = (viewModel.score * 100) / viewModel.totalQuestions
        if (percentage > viewModel.record) {
            viewModel.updateRecord(percentage)
        }
    }
}