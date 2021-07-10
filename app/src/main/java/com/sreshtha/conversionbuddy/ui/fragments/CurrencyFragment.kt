package com.sreshtha.conversionbuddy.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sreshtha.conversionbuddy.R
import com.sreshtha.conversionbuddy.databinding.FragmentCurrencyBinding
import com.sreshtha.conversionbuddy.models.CurrencyResponse
import com.sreshtha.conversionbuddy.models.CurrencyViewModel
import com.sreshtha.conversionbuddy.models.CurrencyViewModelFactory
import com.sreshtha.conversionbuddy.repository.Repository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


class CurrencyFragment : Fragment() {
    private var binding:FragmentCurrencyBinding? = null
    private var rates: HashMap<String,Double>? = null
    private lateinit var viewModel: CurrencyViewModel

    private var ipCountry = "AED"
    private var opCountry = "AED"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCurrencyBinding.inflate(inflater,container,false)
        val repository = Repository()
        val viewModelFactory = CurrencyViewModelFactory(repository)
        viewModel = ViewModelProvider(this,viewModelFactory).get(CurrencyViewModel::class.java)

        viewModel.getRates().enqueue(object :Callback<CurrencyResponse>{
            override fun onResponse(
                call: Call<CurrencyResponse>,
                response: Response<CurrencyResponse>
            ) {
                if(response.isSuccessful && response.body() != null){
                    rates = response.body()?.rates
                    Log.d("Rates","Success")
                }
                else{
                    Toast.makeText(
                        activity,
                        response.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<CurrencyResponse>, t: Throwable) {
                t.printStackTrace()
            }

        })
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.spCurrIp?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val currencyCode = parent?.getItemAtPosition(position).toString().lowercase()
                ipCountry = currencyCode.uppercase()
                try{
                    if(currencyCode != "try"){
                        val imageRes = resources.getIdentifier("drawable/"+currencyCode,"drawable",activity?.packageName)
                        binding?.imageView?.setImageResource(imageRes)
                    }
                    else{
                        // special case
                        val imageRes = resources.getIdentifier("drawable/turkey","drawable",activity?.packageName)
                        binding?.imageView?.setImageResource(imageRes)
                    }
                }
                catch(e:Exception){
                    binding?.imageView?.setImageResource(0)
                }


            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }


        binding?.spCurrOp?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val currencyCode = parent?.getItemAtPosition(position).toString().lowercase()
                opCountry = currencyCode.uppercase()
                try {
                    if (currencyCode != "try") {
                        val imageRes = resources.getIdentifier(
                            "drawable/" + currencyCode,
                            "drawable",
                            activity?.packageName
                        )
                        binding?.imageView2?.setImageResource(imageRes)
                    } else {
                        // special case
                        val imageRes = resources.getIdentifier(
                            "drawable/turkey",
                            "drawable",
                            activity?.packageName
                        )
                        binding?.imageView2?.setImageResource(imageRes)
                    }
                } catch (e: Exception) {
                    binding?.imageView2?.setImageResource(0)
                }


            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }



        binding?.etCurrencyIp?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(rates == null){
                    return
                }
                else{
                    val ipCurr = rates!![ipCountry]
                    val opCurr = rates!![opCountry]
                    val amount = (s.toString().toFloat()) * (ipCurr!!)/(opCurr!!)
                    binding!!.tvCurrencyOp.text = amount.toString()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        setUpCustomSpinner()

    }



    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }


    private fun setUpCustomSpinner(){
        val arr = resources.getStringArray(R.array.codes)
        val adapter = activity?.let { ArrayAdapter<String>(it,R.layout.spinner_custom,arr) }
        adapter?.setDropDownViewResource(R.layout.spinner_custom)
        binding?.spCurrIp?.adapter = adapter
        binding?.spCurrOp?.adapter = adapter
    }

}