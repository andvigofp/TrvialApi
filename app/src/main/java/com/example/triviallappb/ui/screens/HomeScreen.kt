package com.example.triviallappb.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.triviallappb.ui.state.TrivialViewModel

@Composable
fun HomeScreen(
    viewModel: TrivialViewModel,
    onStartGame: (Int) -> Unit, // El parámetro amount es de tipo Int
    onNavigateToGameOver: () -> Unit
) {
    var questionCount by remember { mutableStateOf(5) } // Valor por defecto de 5 preguntas

    val gameOver = viewModel.gameOver
    val record = viewModel.record

    // Se ejecuta cuando el juego termina
    LaunchedEffect(gameOver) {
        if (gameOver) {
            onNavigateToGameOver() // Navegar a GameOverScreen
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround // Esto distribuye los elementos a lo largo de toda la pantalla
    ) {
        // Título en el centro superior de la pantalla
        Text(
            text = "Trivial App",
            style = MaterialTheme.typography.titleLarge
        )

        // Espacio entre el título y el slider
        Spacer(modifier = Modifier.height(16.dp))

        // Muestra el número de preguntas
        Text(text = "Preguntas: $questionCount")

        // Barra deslizante para seleccionar el número de preguntas
        Slider(
            value = questionCount.toFloat(),
            onValueChange = { questionCount = it.toInt() },
            valueRange = 5f..20f,
            steps = 14,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Seleccionar número de preguntas" },
            enabled = !gameOver  // Deshabilitar el Slider cuando el juego haya terminado
        )

        // Espacio entre el slider y el grupo de botones y récord
        Spacer(modifier = Modifier.height(16.dp))

        // Fila con el botón de jugar y el récord
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    viewModel.fetchQuestions(questionCount) // Llamar a la API con el número de preguntas seleccionado
                    viewModel.startGame(questionCount) // Iniciar el juego con el número de preguntas
                    onStartGame(questionCount) // Pasar el número de preguntas a la siguiente pantalla
                },
                modifier = Modifier
                    .padding(end = 8.dp)
                    .semantics { contentDescription = "Iniciar juego" }
            ) {
                Text(text = "Jugar")
            }


            Text(
                text = "Récord: $record",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


