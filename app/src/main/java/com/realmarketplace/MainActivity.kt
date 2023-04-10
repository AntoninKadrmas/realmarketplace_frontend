package com.realmarketplace

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
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
import com.realmarketplace.databinding.ActivityMainBinding
import com.realmarketplace.model.UserTokenAuth
import com.realmarketplace.ui.advert.AdvertViewModel
import com.realmarketplace.viewModel.EnumViewData
import com.realmarketplace.ui.auth.AuthViewModel
import com.realmarketplace.ui.auth.LogOutAuth
import com.realmarketplace.ui.create.CreateFragment
import com.realmarketplace.ui.favorite.FavoriteFragment
import com.realmarketplace.ui.auth.eye.EyeAuthFragment
import com.realmarketplace.ui.create.crud.CrudAdvertViewModel
import com.realmarketplace.ui.favorite.FavoriteObject
import com.realmarketplace.ui.favorite.FavoriteViewModel
import com.realmarketplace.viewModel.PermissionViewModel
import com.realmarketplace.ui.search.SearchFragment
import com.realmarketplace.ui.search.SearchViewModel
import com.realmarketplace.ui.user.UserActivity
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var listOfFragments:List<Fragment>
    private lateinit var navView: BottomNavigationView
    private lateinit var timeOutClear:Handler
    private val enumViewDataModel: EnumViewData by viewModels()
    private val permissionModel: PermissionViewModel by viewModels()
    private val crudAdvertViewModel: CrudAdvertViewModel by viewModels()
    private val searchViewModel: SearchViewModel by viewModels()
    private val favoriteAdvertModel= FavoriteViewModel
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
        favoriteAdvertModel.loadedFavoriteAdvert=false
        FavoriteObject.clearAdvertId()
        AdvertViewModel.clearNewMyAdvert()
        FavoriteViewModel.clearFavoriteAdvert()
        searchViewModel.clearSearchedAdverts()
        val mainKeyValueString = R.string.real_market_place_key_value.toString()
        val userAuthTokenString = R.string.user_auth_token.toString()
        val mainKeyValue = getSharedPreferences(mainKeyValueString,Context.MODE_PRIVATE)
            AuthViewModel.updateUserToken(
                UserTokenAuth(
                token = ""
            )
        )
        mainKeyValue.edit().apply(){
            putString(userAuthTokenString,null)
            apply()
        }
        LogOutAuth.setLogOut(false)
    }
    override fun onDestroy() {
        super.onDestroy()
        cleanUserToken()
    }
    override fun onStop() {
        super.onStop()
        timeOutClear.postDelayed({
            cleanUserToken()
            Toast.makeText(this,"Automatically sign out due the inactivity.",Toast.LENGTH_LONG)
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
                R.id.navigation_favorite ->{
                    val token = mainKeyValue.getString(userAuthTokenString,"")
                    if(token==""){
                        showFragment(3)
                        binding.myToolbar.subtitle= EyeAuthFragment.NAME
                    }
                    else {
                        val newToken = AuthViewModel.userToken.value ?: UserTokenAuth(
                            token = token.toString()
                        )
                        if(!advertViewModel.loadedMyAdverts)advertViewModel.loadAllUserAdverts(newToken,this)
                        if(!favoriteAdvertModel.loadedFavoriteAdvert)favoriteAdvertModel.loadAllFavoriteAdverts(newToken,this)
                        if(AuthViewModel.userToken.value==null) AuthViewModel.updateUserToken(newToken)
                        showFragment(0)
                        binding.myToolbar.subtitle= FavoriteFragment.NAME
                    }
                    true
                }
                R.id.navigation_search ->{
                    val token = mainKeyValue.getString(userAuthTokenString,"")
                    if(token!=""){
                        val newToken = AuthViewModel.userToken.value ?: UserTokenAuth(
                            token = token.toString()
                        )
                        if(!advertViewModel.loadedMyAdverts)advertViewModel.loadAllUserAdverts(newToken,this)
                        if(!favoriteAdvertModel.loadedFavoriteAdvert)favoriteAdvertModel.loadAllFavoriteAdverts(newToken,this)
                        if(AuthViewModel.userToken.value==null) AuthViewModel.updateUserToken(newToken)
                    }
                    showFragment(1)
                    binding.myToolbar.subtitle= SearchFragment.NAME
                    true
                }
                R.id.navigation_create ->{
                    val token = mainKeyValue.getString(userAuthTokenString,"")
                    if(token==""){
                        showFragment(3)
                        binding.myToolbar.subtitle= EyeAuthFragment.NAME
                    }
                    else {
                        val newToken = AuthViewModel.userToken.value ?: UserTokenAuth(
                            token = token.toString()
                        )
                        if(!advertViewModel.loadedMyAdverts)advertViewModel.loadAllUserAdverts(newToken,this)
                        if(!favoriteAdvertModel.loadedFavoriteAdvert)favoriteAdvertModel.loadAllFavoriteAdverts(newToken,this)
                        if(AuthViewModel.userToken.value==null) AuthViewModel.updateUserToken(newToken)
                        showFragment(2)
                        binding.myToolbar.subtitle= CreateFragment.NAME
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
        LogOutAuth.logOut.observe(this, Observer {
            if(it) {
                cleanUserToken()
                navView.selectedItemId = navView.selectedItemId
            }
        })
        binding.myToolbar.menu.getItem(0).setOnMenuItemClickListener {
            if(AuthViewModel.userToken.value!=null){
                if(AuthViewModel.userToken.value!!.token!=""){
                    val intent = Intent(this, UserActivity::class.java)
                    intent.putExtra("publicUserProfile",false)
                    intent.putExtra("permissionStorage",permissionModel.permissionStorage.value)
                    startActivityForResult(intent,10)
                }
                else if(AuthViewModel.userToken.value!!.token==""){
                    startActivity(Intent(this, AuthActivity::class.java))

                }
                else{
                    startActivity(Intent(this, AuthActivity::class.java))
                }
            }else{
                startActivity(Intent(this, AuthActivity::class.java))
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
        requestPermissions(listOf(permission).toTypedArray(),
            PermissionRequestCode.READ_EXTERNAL_STORAGE
        )
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10) {
            if (resultCode == Activity.RESULT_OK) {
                val logOut: Boolean? = data?.getBooleanExtra("logOut",false)
                if(logOut!!) LogOutAuth.setLogOut(true)
            }
        }
    }
}

object PermissionRequestCode{
    var READ_EXTERNAL_STORAGE = 105
    const val CAMERA = 106
}