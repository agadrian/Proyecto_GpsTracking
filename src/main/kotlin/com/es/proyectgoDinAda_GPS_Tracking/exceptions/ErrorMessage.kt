package com.es.proyectgoDinAda_GPS_Tracking.exceptions

data class ErrorMessage(
    val status: Int,
    val message: String,
    val path: String
)