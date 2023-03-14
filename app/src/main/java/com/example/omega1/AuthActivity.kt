package com.example.omega1

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

import com.example.omega1.ui.auth.LoginFragment
import com.example.omega1.ui.auth.MainViewModel
import com.example.omega1.ui.auth.RegisterFragment
import com.example.omega1.databinding.ActivityAuthBinding


class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private val viewModel:MainViewModel by viewModels()

    private var actual = 0
    private lateinit var listOfFragments:List<Fragment>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listOfFragments = listOf(
            LoginFragment.newInstance(),
            RegisterFragment.newInstance()
        )
        rotateFragment()

        viewModel.selectedItem.observe(this, Observer {value->
            actual = value
            rotateFragment()
        })
        viewModel.finishActivity.observe(this, Observer {
            val mainKeyValueString = R.string.real_market_place_key_value.toString()
            val userAuthTokenString = R.string.user_auth_token.toString()
            val mainKeyValue = getSharedPreferences(mainKeyValueString,Context.MODE_PRIVATE)
            mainKeyValue.edit().apply(){
                putString(userAuthTokenString,it.token)
                apply()
            }
            finish()
        })
    }
    private fun rotateFragment(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_auth_layout, listOfFragments[actual])
            .commitNow()
    }

}
