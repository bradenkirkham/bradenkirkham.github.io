package com.example.bmr_app.NPS_API

data class Image(
    val altText: String,
    val caption: String,
    val credit: String,
    val crops: List<Any>,
    val title: String,
    val url: String
)