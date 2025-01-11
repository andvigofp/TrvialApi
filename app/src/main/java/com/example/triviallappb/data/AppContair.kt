package com.example.triviallappb.data

import com.example.triviallappb.network.TriviaApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val apiService: TriviaApiService
}

class DefaultAppContainer : AppContainer {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://opentdb.com/") // Tu URL base de la API
        .addConverterFactory(GsonConverterFactory.create()) // Usando Gson para la conversión
        .build()

    override val apiService: TriviaApiService by lazy {
        retrofit.create(TriviaApiService::class.java)
    }
}