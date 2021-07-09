package com.sreshtha.conversionbuddy.models

import android.util.Log
import androidx.lifecycle.ViewModel
import com.sreshtha.conversionbuddy.repository.Repository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CurrencyViewModel(private val repository: Repository):ViewModel() {



    fun getRates(){
        repository.getRates().enqueue(object : Callback<CurrencyResponse> {
            override fun onResponse(
                call: Call<CurrencyResponse>,
                response: Response<CurrencyResponse>
            ) {
                if(response.isSuccessful && response.body() != null){
                    Log.d("Rates",response.body()!!.rates.toString())
                }
                else{
                    Log.e("NetworkCallError",response.message())
                }
            }

            override fun onFailure(call: Call<CurrencyResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

}