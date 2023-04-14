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
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
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
import com.realmarketplace.ui.favorite.FavoriteObject
import com.realmarketplace.ui.favorite.FavoriteViewModel
import com.realmarketplace.viewModel.PermissionViewModel
import com.realmarketplace.ui.search.SearchFragment
import com.realmarketplace.ui.search.SearchViewModel
import com.realmarketplace.ui.user.UserActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.realmarketplace.ui.auth.AuthActivity
import com.realmarketplace.viewModel.LoadingBar

/**
 * A group of *activity's*.
 *
 * Main class that is start point for all other functionalities.
 * Have three fragments createFragment, favoriteFragment, searchFragment
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var listOfFragments:List<Fragment>
    private lateinit var navView: BottomNavigationView
    private lateinit var timeOutClear:Handler
    private val enumViewDataModel: EnumViewData by viewModels()
    private val permissionModel: PermissionViewModel by viewModels()
    /**
     * A group of *override_functions*.
     *
     * On resume override function is used to check permissions.
     */
    override fun onResume() {
        super.onResume()
        navView.selectedItemId = navView.selectedItemId
        timeOutClear.removeCallbacksAndMessages(null)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED) {
            permissionModel.setPermissionStorage(true)
        }
    }
    /**
     * A group of *override_functions*.
     *
     * On destroy just call cleanUser function (viz cleanUser).
     * And set loadedSampleAdverts to false.
     */
    override fun onDestroy() {
        super.onDestroy()
        SearchViewModel.loadedSampleAdvert=false
        cleanUser()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enumViewDataModel.loadConditionEnum()
        enumViewDataModel.loadPriceEnum()
        enumViewDataModel.loadGenreEnum()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        actionBar?.hide();
        listOfFragments = listOf(
            FavoriteFragment.newInstance(),
            SearchFragment.newInstance(),
            CreateFragment.newInstance(),
            EyeAuthFragment.newInstance()
        )
        LoadingBar.mutableHideLoadingMainActivity.observe(this,Observer{
            if(!it)binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.GONE
        })
        AdvertViewModel.showToolBar.observe(this,Observer{
            if(it){
                binding.myToolbar.visibility= View.VISIBLE
                binding.progressBarLayout.visibility=View.VISIBLE
            }
            else {
                binding.myToolbar.visibility= View.GONE
                binding.progressBarLayout.visibility=View.GONE
            }
        })
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
                        if(!AdvertViewModel.loadedMyAdverts)AdvertViewModel.loadAllUserAdverts(newToken,this)
                        if(!FavoriteViewModel.loadedFavoriteAdvert)FavoriteViewModel.loadAllFavoriteAdverts(newToken,this)
                        if(!SearchViewModel.loadedSampleAdvert)SearchViewModel.loadAdvertSample(newToken,this)
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
                        if(!AdvertViewModel.loadedMyAdverts)AdvertViewModel.loadAllUserAdverts(newToken,this)
                        if(!FavoriteViewModel.loadedFavoriteAdvert)FavoriteViewModel.loadAllFavoriteAdverts(newToken,this)
                        if(AuthViewModel.userToken.value==null) AuthViewModel.updateUserToken(newToken)
                    }
                    if(!SearchViewModel.loadedSampleAdvert)SearchViewModel.loadAdvertSample(UserTokenAuth(token?:""),this)
                    showFragment(1)
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
                        if(!AdvertViewModel.loadedMyAdverts)AdvertViewModel.loadAllUserAdverts(newToken,this)
                        if(!FavoriteViewModel.loadedFavoriteAdvert)FavoriteViewModel.loadAllFavoriteAdverts(newToken,this)
                        if(!SearchViewModel.loadedSampleAdvert)SearchViewModel.loadAdvertSample(newToken,this)
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
                cleanUser()
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
    /**
     * A group of *private_functions*.
     *
     * Function that clear all user data like user token or user Adverts.
     * It return application into default public mode.
     */
    @SuppressLint("SuspiciousIndentation")
    private fun cleanUser(){
        AdvertViewModel.loadedMyAdverts=false
        FavoriteViewModel.loadedFavoriteAdvert=false
        FavoriteObject.clearAdvertId()
        AdvertViewModel.clearNewMyAdvert()
        FavoriteViewModel.clearFavoriteAdvert()
        SearchViewModel.clearSearchAdverts()
        SearchViewModel.clearSampleAdverts()
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
    /**
     * A group of *private_functions*.
     *
     * Function that show correct fragment.
     *
     * @param index used to decide which fragment is going to show
    */
    private fun showFragment(index:Int){
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_activity_main, listOfFragments[index])
            .commit()
    }
    /**
     * A group of *private_functions*.
     *
     * Function used to request for STORAGE permission
     */
    private fun requestPermissionStorage(){
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(listOf(permission).toTypedArray(),
                PermissionRequestCode.READ_EXTERNAL_STORAGE
            )
        }
        else ContextCompat.checkSelfPermission(this,permission)
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