package com.realmarketplace.ui.advert

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.realmarketplace.PermissionRequestCode
import com.realmarketplace.R
import com.realmarketplace.databinding.ActivityUpdateDeleteBinding
import com.realmarketplace.model.AdvertModel
import com.realmarketplace.model.UserTokenAuth
import com.realmarketplace.rest.GenreItem
import com.realmarketplace.ui.auth.AuthViewModel
import com.realmarketplace.ui.auth.LogOutAuth
import com.realmarketplace.ui.create.UpdateDeleteFragment
import com.realmarketplace.ui.create.crud.CrudAdvertViewModel
import com.realmarketplace.ui.favorite.FavoriteObject
import com.realmarketplace.viewModel.EnumViewData
import com.realmarketplace.viewModel.PermissionViewModel

class UpdateDeleteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateDeleteBinding
    private lateinit var advert: AdvertModel
    private lateinit var token: UserTokenAuth
    private val crudViewModel: CrudAdvertViewModel by viewModels()
    private val advertViewModel= AdvertViewModel
    private val enumViewModel: EnumViewData by viewModels()
    private val permissionModel: PermissionViewModel by viewModels()
    private lateinit var dataIntent: Intent
    private var favorite = false
    private var visibility = true

    override fun onCreate(savedInstanceState: Bundle?) {
        LogOutAuth.logOut.observe(this, Observer {
            if(it){
                dataIntent = Intent()
                dataIntent.putExtra("prevAdvertId",advert._id)
                dataIntent.putExtra("logOut",true)
                setResult(RESULT_OK, dataIntent)
                finish()
            }
        })
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateDeleteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        token = if(AuthViewModel.userToken.value!=null) AuthViewModel.userToken.value!!
        else UserTokenAuth("")
        switchFavorite(intent.getBooleanExtra("favorite",false),true)
        permissionModel.setPermissionStorage(intent.getBooleanExtra("permissionStorage",false))
        advert = (intent.getSerializableExtra("advertModel") as? AdvertModel)!!
        switchVisible(advert.visible,true)
        AdvertViewModel.showAdvert.value = advert
        AdvertViewModel.enableButton.observe(this, Observer {
            when(it.toInt()){
                in 0..1->{
                    binding.myToolbar.menu.getItem(AdvertViewModel.VISIBLE_ON).isEnabled=true
                    binding.myToolbar.menu.getItem(AdvertViewModel.VISIBLE_OFF).isEnabled=true
                }
                in 2..3->{
                    binding.myToolbar.menu.getItem(AdvertViewModel.FAVORITE_ON).isEnabled=true
                    binding.myToolbar.menu.getItem(AdvertViewModel.FAVORITE_OFF).isEnabled=true
                }
            }
            binding.myToolbar.menu.getItem(it).isEnabled=true
        })
        AuthViewModel.updateUserToken(token)
        enumViewModel.updatePriceEnum(intent.getSerializableExtra("priceEnum")as ArrayList<String>)
        enumViewModel.updateConditionEnum(intent.getSerializableExtra("conditionEnum") as ArrayList<String>)
        enumViewModel.updateGenreEnum(intent.getSerializableExtra("genreGenreEnum") as ArrayList<GenreItem>)
        loadFragment()
        binding.myToolbar.setNavigationOnClickListener(){
            finish()
        }
        binding.myToolbar.title=advert.title
        binding.myToolbar.menu.getItem(AdvertViewModel.VISIBLE_ON).setOnMenuItemClickListener {
            if(token.token!=""){
                switchVisible(false)
                AdvertViewModel.changeVisibility(advert, token, false, this)
                advert.visible=false
                AdvertViewModel.myAdverts.value?.find { it._id==advert._id }?.visible=false
            }
            else finish()
            true
        }
        binding.myToolbar.menu.getItem(AdvertViewModel.VISIBLE_OFF).setOnMenuItemClickListener {
            if(token.token!=""){
                switchVisible(true)
                AdvertViewModel.changeVisibility(advert, token, true, this)
                advert.visible=true
                AdvertViewModel.myAdverts.value?.find { it._id==advert._id }?.visible=true
            }
            else finish()
            true
        }
        binding.myToolbar.menu.getItem(AdvertViewModel.FAVORITE_OFF).setOnMenuItemClickListener {
            if(token.token!=""){
                switchFavorite(true)
                AdvertViewModel.addFavoriteAdvert(advert, token, this)
                FavoriteObject.addNewAdvertId(advert._id,advert,advert.visible)
            }
            else finish()
            true
        }
        binding.myToolbar.menu.getItem(AdvertViewModel.FAVORITE_ON).setOnMenuItemClickListener {
            if(token.token!=""){
                switchFavorite(false)
                AdvertViewModel.deleteFavoriteAdvert(advert, token, this)
                FavoriteObject.removeAdvertId(advert._id)
            }
            else finish()
            true
        }
        permissionModel.permissionStorageAsk.observe(this, Observer {
            requestPermissionStorage()
        })
        crudViewModel.returnDelete.observe(this, Observer {
            dataIntent = Intent()
            dataIntent.putExtra("prevAdvertId",advert._id)
            dataIntent.putExtra("logOut",false)
            setResult(RESULT_OK, dataIntent)
            finish()
        })
        crudViewModel.returnUpdate.observe(this, Observer {
            dataIntent = Intent()
            dataIntent.putExtra("prevAdvertId",advert._id)
            dataIntent.putExtra("logOut",false)
            dataIntent.putExtra("favorite",favorite)
            dataIntent.putExtra("newAdvert",it)
            setResult(RESULT_OK, dataIntent)
            finish()
        })

    }
    private fun loadFragment(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.create_update_frame_layout, UpdateDeleteFragment.newInstance())
            .commit()
    }
    private fun switchFavorite(state:Boolean,init:Boolean=false){
        if(!init){
            binding.myToolbar.menu.getItem(AdvertViewModel.FAVORITE_ON).isEnabled=false
            binding.myToolbar.menu.getItem(AdvertViewModel.FAVORITE_OFF).isEnabled=false
        }
        if(state){
            favorite=true
            binding.myToolbar.menu.getItem(AdvertViewModel.FAVORITE_OFF).isVisible = false
            binding.myToolbar.menu.getItem(AdvertViewModel.FAVORITE_ON).isVisible = true
        }
        else{
            favorite=false
            binding.myToolbar.menu.getItem(AdvertViewModel.FAVORITE_OFF).isVisible = true
            binding.myToolbar.menu.getItem(AdvertViewModel.FAVORITE_ON).isVisible = false
        }
    }
    private fun switchVisible(state:Boolean,init: Boolean=false){
        if(!init){
            binding.myToolbar.menu.getItem(AdvertViewModel.VISIBLE_ON).isEnabled=false
            binding.myToolbar.menu.getItem(AdvertViewModel.VISIBLE_OFF).isEnabled=false
        }
        if(state){
            visibility=true
            binding.myToolbar.menu.getItem(AdvertViewModel.VISIBLE_ON).isVisible = true
            binding.myToolbar.menu.getItem(AdvertViewModel.VISIBLE_OFF).isVisible = false
        }
        else{
            visibility=false
            binding.myToolbar.menu.getItem(AdvertViewModel.VISIBLE_ON).isVisible = false
            binding.myToolbar.menu.getItem(AdvertViewModel.VISIBLE_OFF).isVisible = true
        }
    }
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