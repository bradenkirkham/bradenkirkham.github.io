package com.example.bmr_app.NPS_API

data class PassportStampImage(
    val altText: String,
    val caption: String,
    val credit: String,
    val crops: List<Crop>,
    val description: String,
    val title: String,
    val url: String
)