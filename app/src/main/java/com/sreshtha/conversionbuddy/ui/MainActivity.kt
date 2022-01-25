 package com.sreshtha.conversionbuddy.ui

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavHost
import androidx.navigation.ui.setupWithNavController
import com.sreshtha.conversionbuddy.R
import com.sreshtha.conversionbuddy.databinding.ActivityMainBinding
import com.sreshtha.conversionbuddy.viewmodel.CurrencyViewModel
import dagger.hilt.android.AndroidEntryPoint

 @AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    val viewModel: CurrencyViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_ConversionBuddy)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHost = supportFragmentManager.findFragmentById(R.id.fragment_container)  as NavHost
        binding.bottomNavigationView.setupWithNavController(navHost.navController)
    }

     fun changeTheme(){
         when(this.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)){
             Configuration.UI_MODE_NIGHT_YES ->{
                 AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
             }
             Configuration.UI_MODE_NIGHT_NO ->{
                 AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
             }
         }
    }
}