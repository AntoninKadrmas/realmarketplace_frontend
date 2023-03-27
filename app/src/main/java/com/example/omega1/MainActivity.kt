package com.example.omega1

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.omega1.databinding.ActivityMainBinding
import com.example.omega1.model.UserTokenAuth
import com.example.omega1.ui.advert.AdvertViewModel
import com.example.omega1.viewModel.EnumViewData
import com.example.omega1.ui.auth.AuthViewModel
import com.example.omega1.ui.create.CreateFragment
import com.example.omega1.ui.favorite.FavoriteFragment
import com.example.omega1.ui.auth.eye.EyeAuthFragment
import com.example.omega1.ui.create.crud.CrudAdvertViewModel
import com.example.omega1.viewModel.PermissionViewModel
import com.example.omega1.ui.search.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_eye_feature.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var listOfFragments:List<Fragment>
    private lateinit var navView: BottomNavigationView
    private lateinit var timeOutClear:Handler
    private val enumViewDataModel: EnumViewData by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val permissionModel: PermissionViewModel by viewModels()
    private val crudAdvertViewModel:CrudAdvertViewModel by viewModels()
    private val advertViewModel= AdvertViewModel
    override fun onResume() {
        super.onResume()
        navView.selectedItemId = navView.selectedItemId
        timeOutClear.removeCallbacksAndMessages(null)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED) {
            permissionModel.setPermissionStorage(true)
        }
    }
    @SuppressLint("SuspiciousIndentation")
    private fun cleanUserToken(){
        advertViewModel.loadedMyAdverts=false
        val mainKeyValueString = R.string.real_market_place_key_value.toString()
        val userAuthTokenString = R.string.user_auth_token.toString()
        val mainKeyValue = getSharedPreferences(mainKeyValueString,Context.MODE_PRIVATE)
            authViewModel.updateUserToken(
                UserTokenAuth(
                token = ""
            )
        )
        mainKeyValue.edit().apply(){
            putString(userAuthTokenString,null)
            apply()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        cleanUserToken()
    }
    override fun onStop() {
        super.onStop()
        timeOutClear.postDelayed({
            cleanUserToken()
            },
            300000//5 minutes
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enumViewDataModel.loadConditionEnum()
        enumViewDataModel.loadPriceEnum()
        enumViewDataModel.loadGenreEnum()
//        cleanUserToken() //odkomn=entovat do provozu
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        actionBar?.hide();
//        setSupportActionBar(binding.myToolbar)
        listOfFragments = listOf(
            FavoriteFragment.newInstance(),
            SearchFragment.newInstance(),
            CreateFragment.newInstance(),
            EyeAuthFragment.newInstance()
        )
        timeOutClear = Handler(Looper.getMainLooper())
        navView = binding.navView
        navView.setOnItemSelectedListener {item->
            val mainKeyValueString = R.string.real_market_place_key_value.toString()
            val userAuthTokenString = R.string.user_auth_token.toString()
            val mainKeyValue = getSharedPreferences(mainKeyValueString,Context.MODE_PRIVATE)
            when(item.itemId){
                R.id.navigation_favorite->{
                    val token = mainKeyValue.getString(userAuthTokenString,"")
                    if(token==""){
                        showFragment(3)
                        binding.myToolbar.subtitle= EyeAuthFragment.NAME
                    }
                    else {
                        val newToken = UserTokenAuth(
                            token = token.toString()
                        )
                        if(!advertViewModel.loadedMyAdverts)advertViewModel.loadAllUserAdverts(newToken,this)
                        authViewModel.updateUserToken(newToken)
                        showFragment(0)
                        binding.myToolbar.subtitle=FavoriteFragment.NAME
                    }
                    true
                }
                R.id.navigation_search->{
                    val token = mainKeyValue.getString(userAuthTokenString,"")
                    if(token!=""){
                        val newToken = UserTokenAuth(
                            token = token.toString()
                        )
                        if(!advertViewModel.loadedMyAdverts)advertViewModel.loadAllUserAdverts(newToken,this)
                        authViewModel.updateUserToken(newToken)
                    }
                    showFragment(1)
                    binding.myToolbar.subtitle=SearchFragment.NAME
                    true
                }
                R.id.navigation_create->{
                    val token = mainKeyValue.getString(userAuthTokenString,"")
                    if(token==""){
                        showFragment(3)
                        binding.myToolbar.subtitle= EyeAuthFragment.NAME
                    }
                    else {
                        val newToken = UserTokenAuth(
                            token = token.toString()
                        )
                        if(!advertViewModel.loadedMyAdverts)advertViewModel.loadAllUserAdverts(newToken,this)
                        authViewModel.updateUserToken(newToken)
                        showFragment(2)
                        binding.myToolbar.subtitle=CreateFragment.NAME
                    }
                    true
                }
                else->{false}
            }
        }
        navView.selectedItemId = R.id.navigation_search
        permissionModel.permissionStorageAsk.observe(this, Observer {
            requestPermissionStorage()
        })
        crudAdvertViewModel.logOut.observe(this, Observer {
            cleanUserToken()
            navView.selectedItemId = navView.selectedItemId
        })
        binding.myToolbar.menu.getItem(0).setOnMenuItemClickListener {
            println(authViewModel.userToken.value)
            if(authViewModel.userToken.value!=null){
                if(authViewModel.userToken.value!!.token!=""){

                }else{
                    startActivity(Intent(this,AuthActivity::class.java))
                }
            }else{
                startActivity(Intent(this,AuthActivity::class.java))
            }
            true
        }
    }
    private fun showFragment(index:Int){
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_activity_main, listOfFragments[index])
            .commit()
    }
    @RequiresApi(Build.VERSION_CODES.M)
    fun requestPermissionStorage(){
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        requestPermissions(listOf(permission).toTypedArray(),PermissionRequestCode.READ_EXTERNAL_STORAGE)
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
                    Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show()
                    permissionModel.setPermissionStorage(false)
                }
                return
            }
            else -> {
            }
        }
    }
}
object PermissionRequestCode{
    var READ_EXTERNAL_STORAGE = 105
    const val CAMERA = 106
}