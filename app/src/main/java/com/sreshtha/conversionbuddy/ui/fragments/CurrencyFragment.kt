package com.sreshtha.conversionbuddy.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sreshtha.conversionbuddy.databinding.FragmentCurrencyBinding
import com.sreshtha.conversionbuddy.models.CurrencyResponse
import com.sreshtha.conversionbuddy.models.CurrencyViewModel
import com.sreshtha.conversionbuddy.models.CurrencyViewModelFactory
import com.sreshtha.conversionbuddy.repository.Repository

class CurrencyFragment : Fragment() {
    private var binding:FragmentCurrencyBinding? = null
    private var currencyModel: CurrencyResponse? = null
    private lateinit var viewModel: CurrencyViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCurrencyBinding.inflate(inflater,container,false)
        val repository = Repository()
        val viewModelFactory = CurrencyViewModelFactory(repository)
        viewModel = ViewModelProvider(this,viewModelFactory).get(CurrencyViewModel::class.java)

        viewModel.currencyResponse.observe(viewLifecycleOwner,){
            if(it.isSuccessful && it.body() != null){
                currencyModel = it.body()!!
                Log.d("Response","Success")
            }
            else{
                Log.e("RetrofitError",it.message())
            }

        }

        return binding?.root
    }



    override fun onDestroy() {
        super.onDestroy()
        binding = null
        currencyModel = null
    }








}