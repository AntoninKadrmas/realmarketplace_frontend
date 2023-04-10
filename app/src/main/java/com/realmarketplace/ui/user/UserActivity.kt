package com.realmarketplace.ui.user

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import com.realmarketplace.PermissionRequestCode
import com.realmarketplace.R
import com.realmarketplace.databinding.ActivityUserBinding
import com.realmarketplace.model.AdvertModel
import com.realmarketplace.ui.auth.AuthViewModel
import com.realmarketplace.ui.auth.LogOutAuth
import com.realmarketplace.viewModel.PermissionViewModel

class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    private lateinit var advert: AdvertModel
    private val userViewModel: UserViewModel by viewModels()
    private val permissionModel: PermissionViewModel by viewModels()


    private val listOfFragments = listOf(
        PublicUserFragment.newInstance(),
        PrivateUserFragment.newInstance(),
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogOutAuth.logOut.observe(this, Observer {
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
            userViewModel.loadUserInformation(AuthViewModel.userToken.value!!, this)
            binding.myToolbar.title="Private Profile"
        }
        binding.myToolbar.setNavigationOnClickListener(){
            finish()
        }
        permissionModel.setPermissionStorage(intent.getBooleanExtra("permissionStorage",false))
        permissionModel.permissionStorageAsk.observe(this, Observer {
            requestPermissionStorage()
        })
    }
    private fun showFragment(index:Int){
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_user_private_public_frame, listOfFragments[index])
            .commit()
    }
    @RequiresApi(Build.VERSION_CODES.M)
    fun requestPermissionStorage(){
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        requestPermissions(listOf(permission).toTypedArray(), PermissionRequestCode.READ_EXTERNAL_STORAGE)
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