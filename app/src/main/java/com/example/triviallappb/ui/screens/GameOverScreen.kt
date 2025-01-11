package com.example.triviallappb.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.triviallappb.ui.state.TrivialViewModel

@Composable
fun GameOverScreen(
    viewModel: TrivialViewModel,
    onHome: () -> Unit,
    onReplay: () -> Unit
) {
    // Calcula el porcentaje final de aciertos
    val finalScorePercentage = if (viewModel.totalQuestions > 0) {
        (viewModel.score * 100) / viewModel.totalQuestions
    } else {
        0
    }

    // Limitar el porcentaje entre 0 y 100
    val clampedScore = finalScorePercentage.coerceIn(0, 100)

    // Determinar el color de la puntuación final
    val scoreColor = when {
        clampedScore >= 80 -> Color.Green // Alta puntuación: Verde
        clampedScore >= 50 -> Color.Yellow // Puntuación media
        else -> Color.Red // Baja puntuación: Rojo
    }

    // Solo actualizar el récord si es necesario
    val shouldUpdateRecord = clampedScore > viewModel.record
    if (shouldUpdateRecord) {
        viewModel.checkAndUpdateRecord(clampedScore)
    }

    // Animación para el puntaje
    val transition = remember { Animatable(0f) }
    LaunchedEffect(clampedScore) {
        transition.animateTo(clampedScore.toFloat(), animationSpec = tween(durationMillis = 1000))
    }

    // Color de fondo para el porcentaje
    val lilacBackgroundColor = when {
        clampedScore >= 80 -> Color.Green
        clampedScore >= 50 -> Color.Yellow
        else -> Color.Red
    }

    // Color de texto
    val textColor = Color.White

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Juego Terminado",
            style = MaterialTheme.typography.headlineMedium
        )

        // Animación para mostrar el porcentaje de puntuación
        Text(
            text = "Puntuación final: ${transition.value.toInt()}%",
            style = MaterialTheme.typography.bodyLarge,
            color = textColor,
            modifier = Modifier
                .background(lilacBackgroundColor)
                .padding(8.dp)
        )

        // Mostrar el récord actualizado con mensaje si hay un nuevo récord
        val isNewRecord = clampedScore > viewModel.record
        Text(
            text = if (isNewRecord) "¡Nuevo récord!" else "Récord: ${viewModel.record}%",
            style = MaterialTheme.typography.bodyLarge,
            color = scoreColor
        )

        // Botón para ir al inicio
        Button(
            onClick = {
                // Asegura que el récord esté actualizado antes de volver al inicio
                if (shouldUpdateRecord) {
                    viewModel.checkAndUpdateRecord(clampedScore)
                }
                onHome()
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Inicio")
        }

        // Botón para reiniciar el juego
        Button(
            onClick = onReplay,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(text = "Volver a jugar")
        }
    }
}


