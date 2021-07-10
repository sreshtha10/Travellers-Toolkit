package com.sreshtha.conversionbuddy.models

import android.util.Log
import androidx.lifecycle.ViewModel
import com.sreshtha.conversionbuddy.repository.Repository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CurrencyViewModel(private val repository: Repository):ViewModel() {


    fun getRates() = repository.getRates()

}