package com.realmarketplace.ui.user

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.realmarketplace.PermissionRequestCode
import com.realmarketplace.R
import com.realmarketplace.databinding.ActivityUserBinding
import com.realmarketplace.model.AdvertModel
import com.realmarketplace.model.UserModelLogin
import com.realmarketplace.ui.auth.AuthViewModel
import com.realmarketplace.ui.auth.LogOutAuth
import com.realmarketplace.viewModel.LoadingBar
import com.realmarketplace.viewModel.PermissionViewModel

/**
 * A group of *activity*.
 *
 * Class for activity_user layout and logic there.
 * Have two fragments publicUserFragment and privateUserFragment.
 */
class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    private lateinit var advert: AdvertModel
    private val userViewModel: UserViewModel by viewModels()
    private val permissionModel: PermissionViewModel by viewModels()


    private val listOfFragments = listOf(
        PublicUserFragment.newInstance(),
        PrivateUserFragment.newInstance(),
    )
    override fun onDestroy() {
        super.onDestroy()
        LogOutAuth.mutableLogOutUser.value=false
    }

    override fun onResume() {
        super.onResume()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LoadingBar.mutableHideLoadingUserActivity.observe(this, Observer {
            if(!it)binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.GONE
        })
        LogOutAuth.mutableLogOutUser.observe(this, Observer {
            if(it){
                val dataIntent = Intent()
                if(intent.getBooleanExtra("publicUserProfile",false))dataIntent.putExtra("prevAdvertId",advert._id)
                dataIntent.putExtra("logOut",true)
                setResult(RESULT_OK, dataIntent)
                finish()
            }
        })
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(intent.getBooleanExtra("publicUserProfile",false)){
            showFragment(0)
            advert=intent.getSerializableExtra("advert") as AdvertModel
            userViewModel.updateUserInfo(advert.user!!)
            binding.myToolbar.title="Public Profile"
            binding.myToolbar.subtitle="${userViewModel.user.value?.lastName} ${userViewModel.user.value?.firstName} "
        }
        else {
            userViewModel.user.observe(this, Observer {
                binding.myToolbar.subtitle="${userViewModel.user.value?.lastName} ${userViewModel.user.value?.firstName} "
                showFragment(1)
            })
            userViewModel.buttonEnables.value=false
            LoadingBar.mutableHideLoadingUserActivity.value=false
            userViewModel.loadUserInformation(AuthViewModel.userToken.value!!, this)
            binding.myToolbar.title="Private Profile"
        }
        binding.myToolbar.setNavigationOnClickListener(){
            finish()
        }
        userViewModel.buttonEnables.observe(this, Observer {
            if(it)LoadingBar.mutableHideLoadingUserActivity.value=true
        })
        permissionModel.setPermissionStorage(intent.getBooleanExtra("permissionStorage",false))
        permissionModel.permissionStorageAsk.observe(this, Observer {
            requestPermissionStorage()
        })
    }
    /**
     * A group of *activity_functions*.
     *
     * Function that show correct fragment.
     *
     * @param index used to decide which fragment is going to be show
     */
    private fun showFragment(index:Int){
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_user_private_public_frame, listOfFragments[index])
            .commit()
    }
    /**
     * A group of *activity_functions*.
     *
     * Function used to request right way for external storage permission.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun requestPermissionStorage(){
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(listOf(permission).toTypedArray(),
                PermissionRequestCode.READ_EXTERNAL_STORAGE
            )
        }
        else ContextCompat.checkSelfPermission(this,permission)
    }
    private fun deleteUserCredential(){
        val mainKeyValueString = getString(R.string.real_market_place_key_value)
        val userAuthCredential = getString(R.string.user_auth_credential)
        val mainKeyValue = getSharedPreferences(mainKeyValueString, Context.MODE_PRIVATE)
        mainKeyValue.edit().apply() {
            remove(userAuthCredential)
            apply()
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermissionRequestCode.READ_EXTERNAL_STORAGE -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    permissionModel.setPermissionStorage(true)
                } else {
                    Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show()
                    permissionModel.setPermissionStorage(false)
                }
                return
            }
            else -> {
            }
        }
    }
}