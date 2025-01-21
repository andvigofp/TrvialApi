package com.example.triviallappb.model

data class Question(
    val type: String,
    val difficulty: String,
    val category: String,
    var question: String,
    var correct_answer: String,
    var incorrect_answers: List<String>
)
