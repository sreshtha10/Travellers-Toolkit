package com.sreshtha.conversionbuddy.api


import com.sreshtha.conversionbuddy.models.CurrencyResponse
import retrofit2.Call
import retrofit2.http.GET


interface CurrencyAPI {

    @GET("/latest")
    fun getExchangeRate(): Call<CurrencyResponse>

}
