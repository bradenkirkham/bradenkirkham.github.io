package com.example.bmr_app.NPS_API

data class Exception(
    val endDate: String,
    val exceptionHours: ExceptionHours,
    val name: String,
    val startDate: String
)