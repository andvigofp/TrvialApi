package com.example.triviallappb.model

data class ApiResponse(
    val response_code: Int,
    val results: List<Question>
)


