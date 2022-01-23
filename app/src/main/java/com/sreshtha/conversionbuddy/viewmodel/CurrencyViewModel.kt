package com.sreshtha.conversionbuddy.viewmodel

import androidx.lifecycle.ViewModel
import com.sreshtha.conversionbuddy.api.CurrencyAPI
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val currencyAPI: CurrencyAPI
):ViewModel() {

    fun getRates() = currencyAPI.getExchangeRate()
}