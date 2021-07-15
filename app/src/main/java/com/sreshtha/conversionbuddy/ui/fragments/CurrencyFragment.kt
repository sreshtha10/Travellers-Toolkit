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
import androidx.lifecycle.ViewModelProvider
import com.sreshtha.conversionbuddy.R
import com.sreshtha.conversionbuddy.databinding.FragmentCurrencyBinding
import com.sreshtha.conversionbuddy.models.CurrencyResponse
import com.sreshtha.conversionbuddy.models.CurrencyViewModel
import com.sreshtha.conversionbuddy.models.CurrencyViewModelFactory
import com.sreshtha.conversionbuddy.repository.Repository
import com.sreshtha.conversionbuddy.ui.dialog.CustomLoadingDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


class CurrencyFragment : Fragment() {
    private var binding: FragmentCurrencyBinding? = null
    private var rates: HashMap<String, Double>? = null
    private lateinit var viewModel: CurrencyViewModel


    private var ipCountry = "AED"
    private var opCountry = "AED"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCurrencyBinding.inflate(inflater, container, false)

        //setting up the viewModel
        val repository = Repository()
        val viewModelFactory = CurrencyViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CurrencyViewModel::class.java)

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

        //end of network call
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        // setting up spinner's  custom adapter
        setUpCustomSpinnerAdapter()

        binding?.spCurrIp?.setSelection(66) //INR
        binding?.spCurrOp?.setSelection(150) //USD


        binding?.spCurrIp?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val currencyCode = parent?.getItemAtPosition(position).toString().lowercase()
                //updating the current ip country
                ipCountry = currencyCode.uppercase()

                //changing the flag resource.
                try {
                    if (currencyCode != "try") {
                        val imageRes = resources.getIdentifier(
                            "drawable/$currencyCode",
                            "drawable",
                            activity?.packageName
                        )
                        binding?.imageView?.setImageResource(imageRes)
                    } else {
                        // special case
                        val imageRes = resources.getIdentifier(
                            "drawable/turkey",
                            "drawable",
                            activity?.packageName
                        )
                        binding?.imageView?.setImageResource(imageRes)
                    }

                } catch (e: Exception) {
                    binding?.imageView?.setImageResource(0)
                    Log.e("CurrError",e.message.toString())
                }

                clearFields()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        //when output spinner's item is selected -> update flags & update currency
        binding?.spCurrOp?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                            "drawable/$currencyCode",
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
                    Log.e("CurrError",e.message.toString())
                }

                clearFields()

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }





        // text watcher for the currency input
        binding?.etCurrencyIp?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding!!.etCurrencyIp.text.isEmpty() || s == null) {
                    binding!!.tvCurrencyOp.text = (0).toString()
                    return
                }
                if (rates == null) {
                    return
                } else {
                    try{
                        val ipCurr = rates!![ipCountry]
                        val opCurr = rates!![opCountry]
                        val amount = (s.toString().toFloat()) * (opCurr!!) / (ipCurr!!)
                        binding!!.tvCurrencyOp.text = amount.toString()
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




}
