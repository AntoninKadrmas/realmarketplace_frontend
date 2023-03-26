package com.example.omega1.ui.advert

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
import com.example.omega1.PermissionRequestCode
import com.example.omega1.R
import com.example.omega1.databinding.ActivityUpdateDeleteBinding
import com.example.omega1.model.AdvertModel
import com.example.omega1.model.UserTokenAuth
import com.example.omega1.rest.GenreItem
import com.example.omega1.ui.auth.AuthViewModel
import com.example.omega1.ui.create.UpdateDeleteFragment
import com.example.omega1.ui.create.crud.CrudAdvertViewModel
import com.example.omega1.viewModel.EnumViewData
import com.example.omega1.viewModel.PermissionViewModel
import kotlinx.android.synthetic.main.activity_auth.*

class UpdateDeleteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateDeleteBinding
    private lateinit var advert: AdvertModel
    private lateinit var token: UserTokenAuth
    private val crudViewModel:CrudAdvertViewModel by viewModels()
    private val advertViewModel:AdvertViewModel by viewModels()
    private val authViewModel:AuthViewModel by viewModels()
    private val enumViewModel:EnumViewData by viewModels()
    private val permissionModel:PermissionViewModel by viewModels()
    private lateinit var dataIntent: Intent
    private var favorite = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateDeleteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        token = if(intent.getSerializableExtra("token")!=null) (intent.getSerializableExtra("token") as? UserTokenAuth)!!
        else UserTokenAuth("")
        advert = (intent.getSerializableExtra("advertModel") as? AdvertModel)!!
        advertViewModel.addNewMyAdvert(advert)
        authViewModel.updateUserToken(token)
        enumViewModel.updatePriceEnum(intent.getSerializableExtra("priceEnum")as ArrayList<String>)
        enumViewModel.updateConditionEnum(intent.getSerializableExtra("conditionEnum") as ArrayList<String>)
        enumViewModel.updateGenreEnum(intent.getSerializableExtra("genreGenreEnum") as ArrayList<GenreItem>)
        loadFragment()
        my_toolbar.setNavigationOnClickListener(){
            val data = Intent()
            data.putExtra("prevAdvertId","")
            data.putExtra("logOut",false)
            data.putExtra("newAdvert",advert)
            data.putExtra("favorite",favorite)
            setResult(RESULT_OK, data)
            finish()
        }
        binding.myToolbar.title=advert.title
        binding.myToolbar.menu.getItem(0).setOnMenuItemClickListener {
            if(token.token!=""){
                favorite=!favorite
                binding.myToolbar.menu.getItem(0).isVisible = false
                binding.myToolbar.menu.getItem(1).isVisible = true
            }
            else{
                Toast.makeText(this,"For this action you have to login.",Toast.LENGTH_SHORT).show()
            }
            true
        }
        binding.myToolbar.menu.getItem(1).setOnMenuItemClickListener {
            if(token.token!=""){
                favorite=!favorite
                binding.myToolbar.menu.getItem(0).isVisible = true
                binding.myToolbar.menu.getItem(1).isVisible = false
            }
            else{
                Toast.makeText(this,"For this action you have to login.",Toast.LENGTH_SHORT).show()
            }
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
        crudViewModel.logOut.observe(this, Observer {
            dataIntent = Intent()
            dataIntent.putExtra("prevAdvertId",advert._id)
            dataIntent.putExtra("logOut",true)
            setResult(RESULT_OK, dataIntent)
            finish()
        })

    }
    private fun loadFragment(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.create_update_frame_layout, UpdateDeleteFragment.newInstance())
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(favorite){
            advertViewModel.addFavoriteAdvert(advert,token,this)
        }
        if(!this::dataIntent.isInitialized){
            dataIntent = Intent()
            dataIntent.putExtra("prevAdvertId","")
            dataIntent.putExtra("logOut",false)
            dataIntent.putExtra("newAdvert",advert)
            dataIntent.putExtra("favorite",favorite)
            setResult(RESULT_OK, dataIntent)
            finish()
        }
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