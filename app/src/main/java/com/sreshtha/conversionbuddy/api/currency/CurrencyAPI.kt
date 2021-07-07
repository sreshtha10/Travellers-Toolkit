package com.sreshtha.conversionbuddy.api.currency

import com.sreshtha.conversionbuddy.BuildConfig
import com.sreshtha.conversionbuddy.models.CurrencyModel
import retrofit2.Response
import retrofit2.http.GET

interface CurrencyAPI {

    @GET("/latest?access_key=${BuildConfig.API_KEY}&format=1")
    suspend fun getExchangeRate():Response<CurrencyModel>

}
