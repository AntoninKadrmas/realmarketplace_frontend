package com.realmarketplace.ui.user.settings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.realmarketplace.R
import com.realmarketplace.databinding.ActivityUserSettingsBinding
import com.realmarketplace.model.LightUser
import com.realmarketplace.ui.auth.LogOutAuth
import com.realmarketplace.ui.user.UserViewModel

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
        userViewModel.endSettings.value=false
        LogOutAuth.logOut.observe(this, Observer {
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

    }
}