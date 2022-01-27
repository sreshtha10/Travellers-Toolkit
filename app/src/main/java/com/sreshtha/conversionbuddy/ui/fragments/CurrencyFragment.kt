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
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.sreshtha.conversionbuddy.R
import com.sreshtha.conversionbuddy.databinding.FragmentCurrencyBinding
import com.sreshtha.conversionbuddy.models.CurrencyResponse
import com.sreshtha.conversionbuddy.ui.MainActivity
import com.sreshtha.conversionbuddy.ui.dialog.CustomLoadingDialog
import com.sreshtha.conversionbuddy.viewmodel.CurrencyViewModel
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.RoundingMode
import java.text.DecimalFormat


@AndroidEntryPoint
class CurrencyFragment : Fragment() {
    private var binding: FragmentCurrencyBinding? = null
    private var rates: HashMap<String, Double>? = null
    private lateinit var viewModel: CurrencyViewModel
    private var isApiCallCompleted:MutableLiveData<Boolean> = MutableLiveData()

    private var ipCountry = "AED"
    private var opCountry = "AED"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCurrencyBinding.inflate(inflater, container, false)
        viewModel = (activity as MainActivity).viewModel
        isApiCallCompleted.value = false
        apiCall()
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // setting up spinner's  custom adapter
        setUpCustomSpinnerAdapter()
        initListeners()
        initValues()

        isApiCallCompleted.observe(viewLifecycleOwner, Observer {
            if(it){
                setConversionRates()
                Log.d("comp",rates.toString())
            }
        })

    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun setUpCustomSpinnerAdapter() {
        val arr = resources.getStringArray(R.array.codes)
        val adapter = activity?.let { ArrayAdapter(it, R.layout.spinner_custom, arr) }
        adapter?.setDropDownViewResource(R.layout.spinner_custom)
        binding?.spCurrIp?.adapter = adapter
        binding?.spCurrOp?.adapter = adapter
    }


    private fun clearFields(){
        binding?.tvCurrencyOp?.text = (0).toString()
        binding?.etCurrencyIp?.text?.clear()
    }


    private fun apiCall(){
        // loading custom dialog till the network call is completed.
        val loadingDialog = activity?.let { CustomLoadingDialog(it) }
        loadingDialog?.startLoadingDialog()

        //network call
        viewModel.getRates().enqueue(object : Callback<CurrencyResponse> {
            override fun onResponse(
                call: Call<CurrencyResponse>,
                response: Response<CurrencyResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    rates = response.body()?.rates
                    loadingDialog?.dismissDialog()
                    isApiCallCompleted.value = true
                    Log.d("Rates", "Success")

                } else {
                    loadingDialog?.dismissDialog()
                    Log.d("Rates", "Failure")
                }
            }

            override fun onFailure(call: Call<CurrencyResponse>, t: Throwable) {
                t.printStackTrace()
                loadingDialog?.dismissDialog()
                Log.d("Rates", "Failure")
            }
        })

    }

    private fun initListeners(){
        binding?.apply {
            spCurrIp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val currencyCode = parent?.getItemAtPosition(position).toString().lowercase()
                    //updating the current ip country
                    ipCountry = currencyCode.uppercase()
                    tvIpCurrCode.text = ipCountry
                    tvCodeOne.text = ipCountry

                    setConversionRates()

                    //changing the flag resource.
                    try {
                        if (currencyCode != "try") {
                            val imageRes = resources.getIdentifier(
                                "drawable/$currencyCode",
                                "drawable",
                                activity?.packageName
                            )
                            ivFlagsIp.setImageResource(imageRes)
                        } else {
                            // special case
                            val imageRes = resources.getIdentifier(
                                "drawable/turkey",
                                "drawable",
                                activity?.packageName
                            )
                            ivFlagsIp.setImageResource(imageRes)
                        }

                    } catch (e: Exception) {
                        ivFlagsIp.setImageResource(0)
                        Log.e("CurrError",e.message.toString())
                    }

                    clearFields()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }

            //when output spinner's item is selected -> update flags & update currency
            spCurrOp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val currencyCode = parent?.getItemAtPosition(position).toString().lowercase()
                    opCountry = currencyCode.uppercase()
                    tvOpCurrCode.text = opCountry
                    tvCodeNotOne.text = opCountry
                    setConversionRates()
                    try {
                        if (currencyCode != "try") {
                            val imageRes = resources.getIdentifier(
                                "drawable/$currencyCode",
                                "drawable",
                                activity?.packageName
                            )
                            ivFlagsOp.setImageResource(imageRes)
                        } else {
                            // special case
                            val imageRes = resources.getIdentifier(
                                "drawable/turkey",
                                "drawable",
                                activity?.packageName
                            )
                            ivFlagsOp.setImageResource(imageRes)
                        }

                    } catch (e: Exception) {
                        ivFlagsOp.setImageResource(0)
                        Log.e("CurrError",e.message.toString())
                    }

                    clearFields()

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }

            // text watcher for the currency input
            etCurrencyIp.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (etCurrencyIp.text.isEmpty() || s == null) {
                        tvCurrencyOp.text = (0).toString()
                        return
                    }
                    if (rates == null) {
                        return
                    } else {
                        try{
                            val ipCurr = rates!![ipCountry]
                            val opCurr = rates!![opCountry]
                            val amount = (s.toString().toFloat()) * (opCurr!!) / (ipCurr!!)
                            tvCurrencyOp.text = roundOffDecimal(amount).toString()
                        }
                        catch (e:Exception){
                            Log.e("ConversionError",e.message.toString())
                            clearFields()
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {

                }
            })

            ivThemeBtn.setOnClickListener {
                (activity as MainActivity).changeTheme()
            }

        }
    }

    private fun initValues(){
        //default selection
        binding?.apply {
            spCurrIp.setSelection(66) //INR
            spCurrOp.setSelection(150) //USD
        }
    }


    private fun setConversionRates(){
        binding?.apply {
            val ipCurr = rates?.get(ipCountry)
            val opCurr = rates?.get(opCountry)
            Log.d("comp","$ipCurr $opCurr")
            opCurr?.apply {
                ipCurr?.apply {
                    if(ipCurr < opCurr){
                        tvCurrOne.text = "1"
                        tvCurrNotOne.text = roundOffDecimal(opCurr/ipCurr).toString()
                    }
                    else{
                        tvCurrNotOne.text = "1"
                        tvCurrOne.text= roundOffDecimal(ipCurr/opCurr).toString()

                    }
                }
            }
        }
    }

    private fun roundOffDecimal(number: Double): Double {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }


}
