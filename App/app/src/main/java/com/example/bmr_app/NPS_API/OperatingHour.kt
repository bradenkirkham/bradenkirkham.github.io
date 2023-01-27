package ccom.example.bmr_app.NPS_API

import com.example.bmr_app.NPS_API.StandardHours

data class OperatingHour(
    val description: String,
    val exceptions: List<Exception>,
    val name: String,
    val standardHours: StandardHours
)