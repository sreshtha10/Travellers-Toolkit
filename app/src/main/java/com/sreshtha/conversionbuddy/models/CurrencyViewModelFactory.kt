package com.sreshtha.conversionbuddy.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sreshtha.conversionbuddy.repository.Repository

class CurrencyViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CurrencyViewModel(repository) as T
    }
}