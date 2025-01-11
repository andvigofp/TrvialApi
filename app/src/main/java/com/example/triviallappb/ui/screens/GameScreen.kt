package com.example.triviallappb.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.triviallappb.ui.state.TrivialUiState
import com.example.triviallappb.ui.state.TrivialViewModel

@Composable
fun GameScreen(viewModel: TrivialViewModel, onGameOver: () -> Unit, amount: Int) {
    LaunchedEffect(amount) {
        Log.d("GameScreen", "Fetching questions for amount: $amount")
        if (viewModel.questions.isEmpty()) {
            viewModel.fetchQuestions(amount)
        }
    }

    val trivialUiState = viewModel.trivialUiState.value

    when (trivialUiState) {
        is TrivialUiState.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Text(text = "Cargando preguntas...", style = MaterialTheme.typography.bodyLarge)
            }
        }
        is TrivialUiState.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Error al cargar las preguntas. Por favor, intenta nuevamente.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Red
                )
            }
        }
        is TrivialUiState.Success -> {
            val question = trivialUiState.questions.getOrNull(viewModel.currentQuestionIndex)

            if (question == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Error al cargar la pregunta. Por favor, reinicia el juego.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Red
                    )
                }
                return
            }

            var shuffledOptions by remember(question) { mutableStateOf(listOf<String>()) }
            LaunchedEffect(question) {
                shuffledOptions = (question.incorrect_answers + question.correct_answer).shuffled()
            }

            var selectedAnswerIndex by remember { mutableStateOf(-1) }
            var showResult by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text(text = "Pregunta ${viewModel.currentQuestionIndex + 1}/${viewModel.totalQuestions}")

                Text(text = question.question, style = MaterialTheme.typography.bodyLarge)

                shuffledOptions.forEachIndexed { index, option ->
                    val backgroundColor = when {
                        !showResult -> MaterialTheme.colorScheme.surface
                        option == question.correct_answer -> Color.Green // Respuesta correcta en verde
                        index == selectedAnswerIndex -> Color.Red // Respuesta incorrecta en rojo
                        else -> MaterialTheme.colorScheme.surface // Opciones no seleccionadas
                    }

                    val borderColor = when {
                        !showResult -> MaterialTheme.colorScheme.surface
                        option == question.correct_answer -> Color.Green // Respuesta correcta: borde verde
                        index == selectedAnswerIndex -> Color.Red // Respuesta incorrecta: borde rojo
                        else -> MaterialTheme.colorScheme.surface // Opciones no seleccionadas: borde predeterminado
                    }

                    val textColor = when {
                        index == selectedAnswerIndex && option != question.correct_answer -> Color.White // Respuesta incorrecta seleccionada: texto blanco
                        else -> Color.Black // Respuesta correcta o no seleccionada: texto negro
                    }

                    Button(
                        onClick = {
                            if (!showResult) {
                                selectedAnswerIndex = index
                                showResult = true
                                viewModel.submitAnswer(index)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(backgroundColor, RoundedCornerShape(8.dp))
                            .border(2.dp, borderColor, RoundedCornerShape(8.dp)), // Añadir borde para opciones no seleccionadas
                        enabled = !showResult // Deshabilitar el botón después de seleccionar una respuesta
                    ) {
                        Text(text = option, color = textColor) // Aplicar color de texto adecuado
                    }
                }

                if (showResult) {
                    Text(
                        text = if (shuffledOptions[selectedAnswerIndex] == question.correct_answer)
                            "¡Correcto!"
                        else
                            "Incorrecto. La respuesta correcta es: ${question.correct_answer}",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    val isLastQuestion = viewModel.currentQuestionIndex + 1 >= viewModel.totalQuestions
                    Button(
                        onClick = {
                            if (isLastQuestion) {
                                onGameOver()
                            } else {
                                viewModel.moveToNextQuestion()
                                selectedAnswerIndex = -1
                                showResult = false
                            }
                        },
                        modifier = Modifier.padding(top = 16.dp),
                        enabled = showResult
                    ) {
                        Text(text = if (isLastQuestion) "Terminar" else "Siguiente")
                    }
                }

                // Mostrar la puntuación
                Text(text = "Puntuación: ${viewModel.score}/${viewModel.totalQuestions}", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}











