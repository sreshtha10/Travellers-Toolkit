package com.sreshtha.conversionbuddy.repository

import com.sreshtha.conversionbuddy.api.currency.RetrofitInstance
import com.sreshtha.conversionbuddy.models.CurrencyResponse
import retrofit2.Call


class Repository {

    fun getRates(): Call<CurrencyResponse> {
        return RetrofitInstance.currency_api.getExchangeRate()
    }
}