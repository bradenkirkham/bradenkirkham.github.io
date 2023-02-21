package com.example.bmr_app.NPS_API

import ccom.example.bmr_app.NPS_API.OperatingHour

data class Data(
    val addresses: List<Addresse>,
    val amenities: List<String>,
    val audioDescription: String,
    val contacts: Contacts,
    val description: String,
    val directionsInfo: String,
    val directionsUrl: String,
    val geometryPoiId: String,
    val id: String,
    val images: List<Image>,
    val isPassportStampLocation: String,
    val lastIndexedDate: String,
    val latLong: String,
    val latitude: String,
    val longitude: String,
    val name: String,
    val operatingHours: List<OperatingHour>,
    val parkCode: String,
    val passportStampImages: List<PassportStampImage>,
    val passportStampLocationDescription: String,
    val url: String
)