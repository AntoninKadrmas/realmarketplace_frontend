package com.example.omega1

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.omega1.databinding.ActivityMainBinding
import com.example.omega1.rest.EnumService
import com.example.omega1.rest.EnumViewData
import com.example.omega1.rest.RetrofitInstance
import com.example.omega1.ui.create.CreateFragment
import com.example.omega1.ui.favorite.FavoriteFragment
import com.example.omega1.ui.other.EyeFeature
import com.example.omega1.ui.search.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_eye_feature.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var retroServiceEnum:EnumService
    private lateinit var listOfFragments:List<Fragment>
    private lateinit var navView: BottomNavigationView
    private lateinit var timeOutClear:Handler
    private val enumViewDataModel:EnumViewData by viewModels()
    override fun onResume() {
        super.onResume()
        navView.selectedItemId = navView.selectedItemId
        timeOutClear.removeCallbacksAndMessages(null)

    }
    private fun cleanUserToken(){
        val mainKeyValueString = R.string.real_market_place_key_value.toString()
        val userAuthTokenString = R.string.user_auth_token.toString()
        val mainKeyValue = getSharedPreferences(mainKeyValueString,Context.MODE_PRIVATE)
        mainKeyValue.edit().apply(){
            putString(userAuthTokenString,null)
            apply()
    ""    }
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
        retroServiceEnum = RetrofitInstance.getRetroFitInstance().create(EnumService::class.java)
        loadPriceEnum()
        loadConditionEnum()
        //cleanUserToken() //odkomn=entovat do provozu
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listOfFragments = listOf(
            FavoriteFragment.newInstance(),
            SearchFragment.newInstance(),
            CreateFragment.newInstance(),
            EyeFeature.newInstance()
        )
        timeOutClear = Handler(Looper.getMainLooper())
        navView = binding.navView
        navView.setOnItemSelectedListener {item->
            val mainKeyValueString = R.string.real_market_place_key_value.toString()
            val userAuthTokenString = R.string.user_auth_token.toString()
            val mainKeyValue = getSharedPreferences(mainKeyValueString,Context.MODE_PRIVATE)
            when(item.itemId){
                R.id.navigation_favorite->{
                    if(mainKeyValue.getString(userAuthTokenString,"")=="")showFragment(3)
                    else showFragment(0)
                    true
                }
                R.id.navigation_search->{
                    showFragment(1)
                    true
                }
                R.id.navigation_create->{
                    if(mainKeyValue.getString(userAuthTokenString,"")=="")showFragment(3)
                    else showFragment(2)
                    true
                }
                else->{false}
            }
        }
        navView.selectedItemId = R.id.navigation_search
    }
    private fun showFragment(index:Int){
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_activity_main, listOfFragments[index])
            .commit()
    }
    @SuppressLint("ResourceType")
    private fun loadPriceEnum(){
        val mainKeyValueString = R.string.real_market_place_key_value.toString()
        val loadTextEnum = R.string.saved_enum_price_option.toString()
        val mainKeyValue = getSharedPreferences(mainKeyValueString,Context.MODE_PRIVATE)
        lifecycleScope.launch {
            val response = try{
                retroServiceEnum.getPrice()
            }catch (e:IOException){
                setDefaultPreviousValue(loadTextEnum)
                return@launch
            }catch (e:HttpException){
                setDefaultPreviousValue(loadTextEnum)
                return@launch
            }
            if(response.isSuccessful && response.body()!=null){
                val stringEnumPrice = response.body()!!.joinToString(";")
                val oldString = mainKeyValue.getString(loadTextEnum,";")
                if(oldString==stringEnumPrice)enumViewDataModel.updatePriceEnum(oldString.split(';').toTypedArray())
                else{
                    mainKeyValue.edit().apply {
                        putString(loadTextEnum,stringEnumPrice)
                        apply()
                    }
                    enumViewDataModel.updatePriceEnum(response.body()!!.toTypedArray())
                }
            }else{
                setDefaultPreviousValue(loadTextEnum)
            }
            return@launch
        }
    }
    @SuppressLint("ResourceType")
    private fun loadConditionEnum(){
        val mainKeyValueString = R.string.real_market_place_key_value.toString()
        val loadTextEnum = R.string.saved_enum_condition_option.toString()
        val mainKeyValue = getSharedPreferences(mainKeyValueString,Context.MODE_PRIVATE)
        lifecycleScope.launch {
            val response = try{
                retroServiceEnum.getBookCondition()
            }catch (e:IOException){
                setDefaultPreviousValue(loadTextEnum)
                return@launch
            }catch (e:HttpException){
                setDefaultPreviousValue(loadTextEnum)
                return@launch
            }
            if(response.isSuccessful && response.body()!=null){
                val stringEnumPrice = response.body()!!.joinToString(";")
                val oldString = mainKeyValue.getString(loadTextEnum,";")
                if(oldString==stringEnumPrice)enumViewDataModel.updateConditionEnum(oldString.split(';').toTypedArray())
                else{
                    mainKeyValue.edit().apply {
                        putString(loadTextEnum,stringEnumPrice)
                        apply()
                    }
                    enumViewDataModel.updateConditionEnum(response.body()!!.toTypedArray())
                }
            }else{
                setDefaultPreviousValue(loadTextEnum)
            }
            return@launch
        }
    }
    private fun setDefaultPreviousValue(variable:String){
        val mainKeyValueString = R.string.real_market_place_key_value.toString()
        val mainKeyValue = getSharedPreferences(mainKeyValueString,Context.MODE_PRIVATE)
        val oldString = mainKeyValue.getString(variable,";")
        if (oldString != null) {
            if(variable.toLowerCase().contains("price")) enumViewDataModel.updatePriceEnum(oldString.split(';').toTypedArray())
            else if(variable.toLowerCase().contains("condition")) enumViewDataModel.updateConditionEnum(oldString.split(';').toTypedArray())
        }
    }
    fun onTaskRemoved(rootIntent: Intent?) {

    }
}