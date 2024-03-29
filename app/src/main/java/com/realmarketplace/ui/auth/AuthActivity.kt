package com.realmarketplace.ui.auth

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.realmarketplace.R

import com.realmarketplace.databinding.ActivityAuthBinding
import com.realmarketplace.ui.auth.recover.RecoverPasswordFragment
import com.realmarketplace.viewModel.LoadingBar

/**
 * A group of *activity*.
 *
 * Class for activity_auth layout and logic there.
 * Have two fragments loginFragment and registerFragment
 */
class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private var actual = 0
    private lateinit var listOfFragments:List<Fragment>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listOfFragments = listOf(
            LoginFragment.newInstance(),
            RegisterFragment.newInstance(),
            RecoverPasswordFragment.newInstance()
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
            if(AuthViewModel.selectedItem.value!=null)
                if(AuthViewModel.selectedItem.value==2)
                    AuthViewModel.selectItem(0)
            else finish()
        }
        AuthViewModel.buttonsEnabled.observe(this, Observer {
            if(it)LoadingBar.mutableHideLoadingAuthActivity.value=true
        })
        LoadingBar.mutableHideLoadingAuthActivity.observe(this,Observer{
            if(!it)binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.GONE
        })
        if(AuthViewModel.selectedItem.value!=null)
            if(AuthViewModel.selectedItem.value==2)AuthViewModel.selectItem(0)
    }
    /**
     * A group of *activity_functions*.
     *
     * Function that switch between two fragments.
     */
    private fun rotateFragment(){
        if(actual==0)binding.myToolbar.subtitle="Login"
        else if(actual==1) binding.myToolbar.subtitle="Register"
        else binding.myToolbar.subtitle="Reset Password"
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_auth_layout, listOfFragments[actual])
            .commitNow()
    }
}
