package com.sreshtha.conversionbuddy.ui.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.sreshtha.conversionbuddy.R
import com.sreshtha.conversionbuddy.databinding.FragmentLanguageBinding
import com.sreshtha.conversionbuddy.ui.MainActivity
import com.sreshtha.conversionbuddy.ui.dialog.CustomDownloadingDialog
import com.sreshtha.conversionbuddy.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class LanguageFragment : Fragment() {

    companion object{
       const val TAG = "LanguageFragment"
    }
    private var binding: FragmentLanguageBinding? = null
    var detectedLang: String? = null
    var langOutput: String? = null
    private var textToSpeech: TextToSpeech? = null
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var audioIntent:Intent
    private var isListening = false


    private val permissionReqLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if(it == true){
            convertSpeechToText()
        }
    }

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


        initializeTextToSpeech()
        initializeSpeechToText(activity as MainActivity)
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
                    "Cannot Translate !${detectedLang}  $keyLangOutput",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        binding?.apply {
            btnTextToSpeech.setOnClickListener {
                convertTextToSpeech()
            }
        }

        binding?.apply {
            btnSpeechToText.setOnClickListener {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    convertSpeechToText()
                }
                activity?.let {
                    if (hasAudioPermission(activity as Context)) {
                        convertSpeechToText()
                    } else {
                        permissionReqLauncher.launch(
                            Manifest.permission.RECORD_AUDIO
                        )
                    }
                }
            }
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
        textToSpeech?.apply {
            stop()
            shutdown()
        }
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


    private fun translate(keyIp: String, keyOp: String, inputText: String) {
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


    private fun hasAudioPermission(context:Context):Boolean{
       return ActivityCompat.checkSelfPermission(context,Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }


    private fun initializeTextToSpeech(){
        textToSpeech = TextToSpeech(activity
        ) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale.getDefault())
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // error
                    Log.d(TAG,"failed")
                } else {
                    convertTextToSpeech()
                }
            }
        }
    }

    private fun initializeSpeechToText(context: Context){
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        audioIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        audioIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        audioIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault())
        speechRecognizer.setRecognitionListener(object :RecognitionListener{
            override fun onReadyForSpeech(params: Bundle?) {
            }

            override fun onBeginningOfSpeech() {
                binding?.apply {
                    etInputLang.setText("")
                    etInputLang.hint = "Listening..."
                }
            }

            override fun onRmsChanged(rmsdB: Float) {
            }

            override fun onBufferReceived(buffer: ByteArray?) {
            }

            override fun onEndOfSpeech() {
            }

            override fun onError(error: Int) {
            }

            override fun onResults(results: Bundle?) {
                val data = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                data?.apply {
                    binding?.apply {
                        etInputLang.setText(data[0])
                    }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
            }
        })
    }

    private fun convertTextToSpeech(){
        binding?.apply {
            val text = tvOutputLang.text
            if(text.isNotEmpty()){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    textToSpeech?.speak(text,TextToSpeech.QUEUE_FLUSH,null,null)
                } else {
                    textToSpeech?.speak(text as String?, TextToSpeech.QUEUE_FLUSH, null)
                }
            }
        }
    }

    private fun convertSpeechToText(){
        if(isListening){
            speechRecognizer.stopListening()
            isListening = false
        }
        else{
            isListening = true
            speechRecognizer.startListening(audioIntent)
        }
    }
}