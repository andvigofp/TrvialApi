package com.example.triviallappb.model

// Question.kt
data class Question(
    val type: String,
    val difficulty: String,
    val category: String,
    val question: String,
    val correct_answer: String,
    val incorrect_answers: List<String>
)

