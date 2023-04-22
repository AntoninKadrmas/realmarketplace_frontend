package com.realmarketplace.ui.user.settings

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.realmarketplace.R
import com.realmarketplace.databinding.ActivityUserSettingsBinding
import com.realmarketplace.model.LightUser
import com.realmarketplace.model.UserModelLogin
import com.realmarketplace.ui.auth.AuthViewModel
import com.realmarketplace.ui.auth.LogOutAuth
import com.realmarketplace.ui.user.UserViewModel
import com.realmarketplace.viewModel.LoadingBar

/**
 * A group of *activity*.
 *
 * Class for activity_user_settings layout and logic there.
 * Have two fragments userSettingsPassword and userSettingsProfile.
 */
class UserSettings : AppCompatActivity() {
    private lateinit var binding: ActivityUserSettingsBinding
    private val userViewModel: UserViewModel by viewModels()
    private val dataIntent = Intent()
    private val listOfFragments = listOf(
        UserSettingsProfile.newInstance(),
        UserSettingsPassword.newInstance(),
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadGuestCredentials()
        userViewModel.endSettings.value=false
        userViewModel.buttonEnables.value=true
        userViewModel.buttonEnables.observe(this, Observer {
            if(it)LoadingBar.mutableHideLoadingUserSettingsActivity.value=true
        })
        LoadingBar.mutableHideLoadingUserSettingsActivity.observe(this, Observer {
            if(!it)binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.GONE
        })
        LogOutAuth.mutableLogOutUser.observe(this, Observer {
            if(it){
                dataIntent.putExtra("logOut",true)
                dataIntent.putExtra("user",userViewModel.user.value)
                setResult(RESULT_OK, dataIntent)
                finish()
            }
        })
        userViewModel.endSettings.observe(this, Observer {
            if(it){
                dataIntent.putExtra("user",userViewModel.user.value)
                setResult(RESULT_OK, dataIntent)
                finish()
            }
        })
        binding = ActivityUserSettingsBinding.inflate(layoutInflater)
        userViewModel.updateUserInfo(intent.getSerializableExtra("user") as LightUser)
        val type = intent.getIntExtra("type",0)
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_user_settings_frame, listOfFragments[type])
            .commit()
        if(type==0)binding.myToolbar.subtitle="Update Profile"
        else binding.myToolbar.subtitle="Update Password"
        setContentView(binding.root)
        binding.myToolbar.setNavigationOnClickListener(){
            finish()
        }
        userViewModel.guestUser.observe(this, Observer {
            if (!it.email.isNullOrEmpty() && !it.password.isNullOrEmpty()) {
                val mainKeyValueString = getString(R.string.real_market_place_key_value)
                val userAuthCredential = getString(R.string.user_auth_credential)
                val mainKeyValue = getSharedPreferences(mainKeyValueString, Context.MODE_PRIVATE)
                mainKeyValue.edit().apply() {
                    putString(userAuthCredential, "${it.email};${it.password}")
                    apply()
                }
            }
        })
    }
    //guest function
    fun loadGuestCredentials(){
        val mainKeyValueString = getString(R.string.real_market_place_key_value)
        val userAuthCredential = getString(R.string.user_auth_credential)
        val mainKeyValue = getSharedPreferences(mainKeyValueString, Context.MODE_PRIVATE)
        val userPas = mainKeyValue.getString(userAuthCredential, ";")?.split(";")
        if(!userPas?.get(0)?.isNullOrEmpty()!! && !userPas?.get(1)?.isNullOrEmpty()!!){
            userViewModel.updateUserCredentials(UserModelLogin(userPas[0], userPas[1]))
        }
    }
}