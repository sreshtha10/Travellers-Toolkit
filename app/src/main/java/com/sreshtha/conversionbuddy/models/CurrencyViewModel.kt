package com.sreshtha.conversionbuddy.models

import androidx.lifecycle.ViewModel
import com.sreshtha.conversionbuddy.repository.Repository


class CurrencyViewModel(private val repository: Repository):ViewModel() {


    fun getRates() = repository.getRates()

}