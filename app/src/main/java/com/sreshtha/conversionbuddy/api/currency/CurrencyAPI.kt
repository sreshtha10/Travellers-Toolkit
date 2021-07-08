package com.sreshtha.conversionbuddy.api.currency


import com.sreshtha.conversionbuddy.models.CurrencyResponse
import retrofit2.Response
import retrofit2.http.GET


interface CurrencyAPI {

    @GET("latest/")
    suspend fun getExchangeRate():Response<CurrencyResponse>

}
