package com.sreshtha.conversionbuddy.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sreshtha.conversionbuddy.databinding.FragmentCurrencyBinding

class CurrencyFragment : Fragment() {
    private var binding:FragmentCurrencyBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCurrencyBinding.inflate(inflater,container,false)
        return binding?.root
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }


}