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
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.sreshtha.conversionbuddy.R
import com.sreshtha.conversionbuddy.databinding.FragmentLanguageBinding
import com.sreshtha.conversionbuddy.utils.Constants

class LanguageFragment : Fragment() {

    private var binding:FragmentLanguageBinding? = null

    var detectedLang:String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLanguageBinding.inflate(inflater,container,false)
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpSpinnerAdapter()

        binding?.etInputLang?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
               detectLang(s.toString())
               if(detectedLang!=null){
                   binding?.tvDetectedLang?.text = Constants.map[detectedLang]
               }
            }
        })


        binding?.spinnerLang?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val lang = parent?.getItemAtPosition(position)

                // check if lang model is downloaded or not.



            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }


    private fun detectLang(s:String){
        val languageIdentifier = LanguageIdentification.getClient()

        languageIdentifier.identifyLanguage(s)
            .addOnSuccessListener {
                if(it == "und"){
                    Log.i("TAG","Can't identify language")
                }
                else{
                    detectedLang = it

                    Log.i("TAG","Language:$it")
                }
            }
            .addOnFailureListener {
                Log.e("LangError",it.message.toString())
            }
    }



    private fun setUpSpinnerAdapter(){
        val arr = resources.getStringArray(R.array.lang)
        val adapter = activity?.let { ArrayAdapter(it,R.layout.spinner_custom,arr) }
        binding?.spinnerLang?.adapter = adapter
    }


}