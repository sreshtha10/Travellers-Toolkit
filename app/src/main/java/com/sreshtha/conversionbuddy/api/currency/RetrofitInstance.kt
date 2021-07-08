package com.sreshtha.conversionbuddy.api.currency

import com.google.gson.GsonBuilder
import com.sreshtha.conversionbuddy.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitInstance {

    private val gson by lazy{
        GsonBuilder()
            .setLenient()
            .create()
    }

    private val client by lazy{
        OkHttpClient.Builder()
            .readTimeout(5000,TimeUnit.SECONDS)
            .connectTimeout(100,TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }
    
    val currency_api: CurrencyAPI by lazy { 
        retrofit.create(CurrencyAPI::class.java)
    }
}