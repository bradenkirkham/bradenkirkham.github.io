package com.example.bmr_app.NPS_API

data class NationalParkData(
    val `data`: List<Data>,
    val limit: String,
    val start: String,
    val total: String
)