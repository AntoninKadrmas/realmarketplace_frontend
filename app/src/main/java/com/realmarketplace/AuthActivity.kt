package com.realmarketplace

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.realmarketplace.R

import com.realmarketplace.ui.auth.LoginFragment
import com.realmarketplace.ui.auth.AuthViewModel
import com.realmarketplace.ui.auth.RegisterFragment
import com.realmarketplace.databinding.ActivityAuthBinding


class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding

    private var actual = 0
    private lateinit var listOfFragments:List<Fragment>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("there")
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listOfFragments = listOf(
            LoginFragment.newInstance(),
            RegisterFragment.newInstance()
        )
        rotateFragment()
        AuthViewModel.selectedItem.observe(this, Observer { value->
            actual = value
            rotateFragment()
        })
        AuthViewModel.userToken.observe(this, Observer {
            if(!it.token.isNullOrEmpty()) {
                val mainKeyValueString = R.string.real_market_place_key_value.toString()
                val userAuthTokenString = R.string.user_auth_token.toString()
                val mainKeyValue = getSharedPreferences(mainKeyValueString, Context.MODE_PRIVATE)
                mainKeyValue.edit().apply() {
                    putString(userAuthTokenString, it.token)
                    apply()
                }
                finish()
            }
        })
        binding.myToolbar.setNavigationOnClickListener(){
            finish()
        }
    }
    private fun rotateFragment(){
        if(actual==0)binding.myToolbar.subtitle="Login"
        else binding.myToolbar.subtitle="Register"
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_auth_layout, listOfFragments[actual])
            .commitNow()
    }
}
