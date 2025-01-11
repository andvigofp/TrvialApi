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
fun GameScreen(viewModel: TrivialViewModel, amount: Int, onGameOver: () -> Unit) {
    LaunchedEffect(amount) {
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

                val allOptions = question.incorrect_answers + question.correct_answer
                val shuffledOptions = remember(question) { allOptions.shuffled() }

                shuffledOptions.forEachIndexed { index, option ->

                    // Definir el color de fondo y bordes según la respuesta seleccionada
                    val backgroundColor = when {
                        !showResult -> MaterialTheme.colorScheme.surface
                        index == shuffledOptions.indexOf(question.correct_answer) -> Color.Green // Respuesta correcta en verde
                        index == selectedAnswerIndex -> Color.Red // Respuesta incorrecta en rojo
                        else -> Color.Gray // Opciones no seleccionadas en gris
                    }

                    val borderColor = when {
                        !showResult -> MaterialTheme.colorScheme.surface
                        index == shuffledOptions.indexOf(question.correct_answer) -> Color.Green // Respuesta correcta: borde verde
                        index == selectedAnswerIndex -> Color.Red // Respuesta incorrecta: borde rojo
                        else -> Color.Black // Opciones no seleccionadas: borde negro
                    }

                    val textColor = when {
                        index == selectedAnswerIndex && index != shuffledOptions.indexOf(question.correct_answer) -> Color.White // Respuesta incorrecta seleccionada: texto blanco
                        else -> Color.Black // Respuesta correcta o no seleccionada: texto negro
                    }

                    Button(
                        onClick = {
                            if (!showResult) {
                                selectedAnswerIndex = index
                                showResult = true

                                // Llamada a submitAnswer para procesar la respuesta
                                viewModel.submitAnswer(index, shuffledOptions)
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

                // Mostrar el resultado solo después de haber seleccionado una respuesta
                if (showResult) {
                    Text(
                        text = if (selectedAnswerIndex == shuffledOptions.indexOf(question.correct_answer))
                            "¡Correcto!"
                        else
                            "Incorrecto. La respuesta correcta es: ${question.correct_answer}",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Button(
                        onClick = {
                            if (viewModel.currentQuestionIndex + 1 >= viewModel.totalQuestions) {
                                onGameOver() // Finaliza el juego si es la última pregunta
                            } else {
                                viewModel.moveToNextQuestion()
                                selectedAnswerIndex = -1
                                showResult = false
                            }
                        },
                        modifier = Modifier.padding(top = 16.dp),
                        enabled = showResult // Deshabilitar el botón "Siguiente" hasta que se haya mostrado el resultado
                    ) {
                        Text(text = "Siguiente")
                    }
                }

                Text(text = "Puntuación: ${viewModel.score}/${viewModel.totalQuestions}")
            }
        }
    }
}














