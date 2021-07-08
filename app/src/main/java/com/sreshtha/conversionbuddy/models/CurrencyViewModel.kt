package com.sreshtha.conversionbuddy.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sreshtha.conversionbuddy.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class CurrencyViewModel(private val repository: Repository):ViewModel() {

    val currencyResponse : MutableLiveData<Response<CurrencyResponse>> = MutableLiveData()

    fun getRates(){
        viewModelScope.launch {
            val response = repository.getRates()
            currencyResponse.value = response
        }
    }

}