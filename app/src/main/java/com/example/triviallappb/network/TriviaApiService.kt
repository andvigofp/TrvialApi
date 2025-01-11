package com.example.triviallappb.network

import com.example.triviallappb.model.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TriviaApiService {
    @GET("api.php")
    suspend fun getQuestions(
        @Query("amount") amount: Int, // Número de preguntas
        @Query("difficulty") difficulty: String = "easy", // Dificultad por defecto: "easy"
        @Query("type") type: String = "multiple" // Tipo por defecto: "multiple"
    ): Response<ApiResponse>
}
