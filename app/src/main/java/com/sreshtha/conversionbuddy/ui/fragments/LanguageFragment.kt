package com.sreshtha.conversionbuddy.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.sreshtha.conversionbuddy.R
import com.sreshtha.conversionbuddy.databinding.FragmentLanguageBinding
import com.sreshtha.conversionbuddy.ui.dialog.CustomDownloadingDialog
import com.sreshtha.conversionbuddy.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LanguageFragment : Fragment() {

    private var binding: FragmentLanguageBinding? = null
    var detectedLang: String? = null
    var langOutput: String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLanguageBinding.inflate(inflater, container, false)

        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpSpinnerAdapter()

        binding?.tvOutputLang?.movementMethod = ScrollingMovementMethod()

        binding?.etInputLang?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                detectLang(s.toString())
                if (detectedLang != null) {
                    binding?.tvDetectedLang?.text = Constants.map[detectedLang]
                }
            }
        })


        binding?.spinnerLang?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                langOutput = parent?.getItemAtPosition(position) as String?
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }



        binding?.button?.setOnClickListener {
            var keyLangOutput: String? = null
            val inputText = binding!!.etInputLang.text.toString()
            for ((key, value) in Constants.map.entries) {
                if (value == langOutput) {
                    keyLangOutput = key
                }
            }

            if (detectedLang != null && keyLangOutput != null) {
                translate(detectedLang!!, keyLangOutput, inputText)
            } else {
                Toast.makeText(
                    activity,
                    "Cannot Translate !${detectedLang}  ${keyLangOutput}",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }


    private fun detectLang(s: String) {
        val languageIdentifier = LanguageIdentification.getClient()

        languageIdentifier.identifyLanguage(s)
            .addOnSuccessListener {
                if (it == "und") {
                    Log.i("TAG", "Can't identify language")
                } else {
                    detectedLang = it

                    Log.i("TAG", "Language:$it")
                }
            }
            .addOnFailureListener {
                Log.e("LangError", it.message.toString())
            }
    }


    private fun setUpSpinnerAdapter() {
        val arr = resources.getStringArray(R.array.lang)
        val adapter = activity?.let { ArrayAdapter(it, R.layout.spinner_custom, arr) }
        binding?.spinnerLang?.adapter = adapter
    }


    fun translate(keyIp: String, keyOp: String, inputText: String) {
        try {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.fromLanguageTag(keyIp))
                .setTargetLanguage(TranslateLanguage.fromLanguageTag(keyOp))
                .build()

            val translator = Translation.getClient(options)

            val conditions = DownloadConditions.Builder()
                .build()

            // loading custom dialog till the network call is completed.
            val downloadingDialog = activity?.let { CustomDownloadingDialog(it) }
            downloadingDialog?.startLoadingDialog()

            lifecycleScope.launch(Dispatchers.IO) {
                translator.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener {
                        Log.d("LangDownloadModel", "success")
                        downloadingDialog?.dismissDialog()
                        try {
                            translator.translate(inputText)
                                .addOnSuccessListener {
                                    Log.d("LangTranslateModel", "success")
                                    binding!!.tvOutputLang.text = it
                                }
                                .addOnFailureListener {
                                    Log.d("LangTranslateModel", it.message.toString())
                                    downloadingDialog?.dismissDialog()
                                }
                        } catch (e: Exception) {
                            Log.e("TranslateError", e.message.toString())
                        }

                    }
                    .addOnFailureListener {
                        Log.d("LangDownloadModel", it.message.toString())
                        downloadingDialog?.dismissDialog()
                    }
            }
        } catch (e: Exception) {
            Toast.makeText(
                activity,
                e.message.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }

    }


}