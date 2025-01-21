package com.example.triviallappb.ui.state

import android.util.Log
import androidx.core.text.HtmlCompat
import androidx.lifecycle.viewModelScope
import com.example.triviallappb.model.Question
import com.example.triviallappb.model.RetrofitClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.pow

class UiFunctions(private val viewModel: TrivialViewModel) {

    fun fetchQuestions(amount: Int, difficulty: String = "easy", type: String = "multiple", retryCount: Int = 3) {
        viewModel.viewModelScope.launch {
            viewModel.setTrivialUiState(TrivialUiState.Loading)
            try {
                Log.d("TrivialViewModel", "Fetching questions with amount: $amount, difficulty: $difficulty, type: $type")
                val response = RetrofitClient.apiService.getQuestions(amount, difficulty, type)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("TrivialViewModel", "API Response: $responseBody")
                    if (responseBody?.response_code == 0) {
                        // Desescapar caracteres especiales después de recibir los datos
                        val questionsList: List<Question> = responseBody.results.map { q: Question ->
                            Question(
                                type = q.type,
                                difficulty = q.difficulty,
                                category = q.category,
                                question = HtmlCompat.fromHtml(q.question, HtmlCompat.FROM_HTML_MODE_LEGACY).toString(),
                                correct_answer = HtmlCompat.fromHtml(q.correct_answer, HtmlCompat.FROM_HTML_MODE_LEGACY).toString(),
                                incorrect_answers = q.incorrect_answers.map { ans: String ->
                                    HtmlCompat.fromHtml(ans, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                                }
                            )
                        }

                        Log.d("TrivialViewModel", "Questions fetched: ${questionsList.size}")
                        if (questionsList.isNotEmpty()) {
                            viewModel.clearQuestions()
                            viewModel.addQuestions(questionsList)
                            viewModel.setTotalQuestions(questionsList.size)
                            viewModel.setTrivialUiState(TrivialUiState.Success(questionsList))
                        } else {
                            viewModel.setTrivialUiState(TrivialUiState.Error)
                            Log.e("TrivialViewModel", "No questions found")
                        }
                    } else {
                        viewModel.setTrivialUiState(TrivialUiState.Error)
                        Log.e("TrivialViewModel", "API Response Code: ${responseBody?.response_code}")
                    }
                } else {
                    if (response.code() == 429 && retryCount > 0) {
                        // Esperar un tiempo antes de intentar nuevamente (exponencial backoff)
                        val backoffTime = (2.0.pow((3 - retryCount).toDouble()) * 1000).toLong()
                        delay(backoffTime)
                        fetchQuestions(amount, difficulty, type, retryCount - 1)
                    } else {
                        viewModel.setTrivialUiState(TrivialUiState.Error)
                        Log.e("TrivialViewModel", "API Response Error: ${response.message()} (Code: ${response.code()})")
                    }
                }
            } catch (e: Exception) {
                viewModel.setTrivialUiState(TrivialUiState.Error)
                Log.e("TrivialViewModel", "Error fetching questions: ${e.message}")
            }
        }
    }
}