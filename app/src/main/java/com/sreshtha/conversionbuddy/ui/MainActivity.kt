 package com.sreshtha.conversionbuddy.ui

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHost
import androidx.navigation.ui.setupWithNavController
import com.sreshtha.conversionbuddy.R
import com.sreshtha.conversionbuddy.databinding.ActivityMainBinding
import com.sreshtha.conversionbuddy.models.CurrencyViewModel
import com.sreshtha.conversionbuddy.models.CurrencyViewModelFactory
import com.sreshtha.conversionbuddy.repository.Repository


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var viewModel: CurrencyViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = Repository()
        val viewModelFactory = CurrencyViewModelFactory(repository)
        viewModel = ViewModelProvider(this,viewModelFactory).get(CurrencyViewModel::class.java)

        val navHost = supportFragmentManager.findFragmentById(R.id.fragment_container)  as NavHost
        binding.bottomNavigationView.setupWithNavController(navHost.navController)



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.theme_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_change_theme ->{
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
        return true
    }
}