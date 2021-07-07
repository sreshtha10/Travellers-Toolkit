package com.sreshtha.conversionbuddy.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.sreshtha.conversionbuddy.api.currency.RetrofitInstance
import com.sreshtha.conversionbuddy.databinding.FragmentCurrencyBinding
import com.sreshtha.conversionbuddy.models.CurrencyModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class CurrencyFragment : Fragment() {
    private var binding:FragmentCurrencyBinding? = null
    private var currencyModel:CurrencyModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCurrencyBinding.inflate(inflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fetchRatesFromAPI()
        Toast.makeText(
            activity,
            currencyModel?.rates.toString(),
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }


    private fun fetchRatesFromAPI(){
        lifecycleScope.launch(Dispatchers.IO){
            val response = try{
                RetrofitInstance.currency_api.getExchangeRate()
            }
            catch (e:IOException) {
                Log.e("ErrorInRetrofitAPI", "You may not have internet connection")
                return@launch
            }
            catch (e : HttpException) {
                Log.e("ErrorInRetrofitAPI", "Unexpected Response")
                return@launch
            }

            Toast.makeText(
                activity,
                response.toString(),
                Toast.LENGTH_LONG
            ).show()

            if(response.isSuccessful && response.body() != null){
                currencyModel = response.body()!!
            }
        }

    }


}