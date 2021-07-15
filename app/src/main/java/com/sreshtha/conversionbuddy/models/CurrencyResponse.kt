package com.sreshtha.conversionbuddy.models


data class CurrencyResponse(
    val base: String,
    val date: String,
    val motd: HashMap<String, String>,
    val rates: HashMap<String, Double>,
    val success: Boolean
)
