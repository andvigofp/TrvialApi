package com.example.triviallappb.model

import com.example.triviallappb.network.TriviaApiService
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// RetrofitClient.kt
object RetrofitClient {
    private const val BASE_URL = "https://opentdb.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: TriviaApiService = retrofit.create(TriviaApiService::class.java)
}


// ApiService.kt
interface ApiService {
    @GET("path/to/questions")  // Cambia esta URL según el endpoint de tu API
    suspend fun getQuestions(@Query("amount") amount: Int, difficulty: String, type: String): Response<ApiResponse>
}