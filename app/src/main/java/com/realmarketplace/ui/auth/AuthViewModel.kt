package com.realmarketplace.ui.auth

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.realmarketplace.model.UserModel
import com.realmarketplace.model.UserModelLogin
import com.realmarketplace.model.UserTokenAuth
import com.realmarketplace.rest.AuthService
import com.realmarketplace.rest.RetrofitInstance
import com.realmarketplace.rest.ReturnTypeError
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

object AuthViewModel{
    private val mutableSelectedItem = MutableLiveData<Int>()
    val selectedItem: LiveData<Int> get() = mutableSelectedItem
    private val mutableUserToken = MutableLiveData<UserTokenAuth>()
    val userToken: LiveData<UserTokenAuth> get() = mutableUserToken
    var buttonsEnabled=MutableLiveData<Boolean>()
    fun selectItem(item: Int) {
        mutableSelectedItem.value = item
    }
    fun updateUserToken(item: UserTokenAuth) {
        mutableUserToken.value = item
    }
    private var retroServiceAuth: AuthService = RetrofitInstance.getRetroFitInstance().create(
        AuthService::class.java)
    fun register(userModel: UserModel, context: Context){
        CoroutineScope(Dispatchers.IO).launch {
            val response = try{
                retroServiceAuth.registerUser(userModel)
            }catch (e: IOException){
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                    buttonsEnabled.value=true
                }
                return@launch
            }catch (e: HttpException){
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                    buttonsEnabled.value=true
                }
                return@launch
            }
            if(response.isSuccessful && response.body()!=null){
                val body: UserTokenAuth? = Gson().fromJson(Gson().toJson(response.body()), UserTokenAuth::class.java)
                withContext(Dispatchers.Main) {
                    if (body != null) {
                        println(body)
                        updateUserToken(body)
                    }
                    buttonsEnabled.value=true
                }
            }else{
                val errorBody = response.errorBody()
                try {
                    val errorResponse: ReturnTypeError? = Gson().fromJson(errorBody?.charStream(), ReturnTypeError::class.java)
                    withContext(Dispatchers.Main){
                        Toast.makeText(context,"${errorResponse?.error}", Toast.LENGTH_LONG).show()
                        buttonsEnabled.value=true
                    }
                }
                catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(context,"Server dose not respond.", Toast.LENGTH_SHORT).show()
                        buttonsEnabled.value=true
                    }
                }
            }
            return@launch
        }
    }
    fun login(userModel: UserModelLogin, context: Context){
        CoroutineScope(Dispatchers.IO).launch {
            val response = try{
                retroServiceAuth.loginUser(okhttp3.Credentials.basic(userModel.email, userModel.password))
            }catch (e: IOException){
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                    buttonsEnabled.value=true
                }
                return@launch
            }catch (e: HttpException){
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                    buttonsEnabled.value=true
                }
                return@launch
            }
            if(response.isSuccessful && response.body()!=null){
                val body: UserTokenAuth? = Gson().fromJson(Gson().toJson(response.body()), UserTokenAuth::class.java)
                withContext(Dispatchers.Main) {
                    if (body != null) {
                        println(body)
                        updateUserToken(body)
                    }
                    buttonsEnabled.value=true
                }
            }else{
                val errorBody = response.errorBody()
                try {
                    val errorResponse: ReturnTypeError? = Gson().fromJson(errorBody?.charStream(), ReturnTypeError::class.java)
                    withContext(Dispatchers.Main){
                        Toast.makeText(context,"${errorResponse?.error}", Toast.LENGTH_LONG).show()
                        buttonsEnabled.value=true
                    }
                }
                catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(context,"Server dose not respond.", Toast.LENGTH_SHORT).show()
                        buttonsEnabled.value=true
                    }
                }
            }
            return@launch
        }
    }
    fun checkPassword(passwordText:String):Boolean{
        val checkLength = passwordText.length>=8
        val checkLowerCharacter = passwordText.matches(".*[a-z]+.*".toRegex())
        val checkUpperCharacter = passwordText.matches(".*[A-Z]+.*".toRegex())
        val checkDigitCharacter = passwordText.matches(".*[0-9]+.*".toRegex())
        val checkSpecialCharacter = passwordText.matches(".*[^A-Za-z0-9\\s]+.*".toRegex())
        val checkContainsSpecial = passwordText.contains(":")
        return !checkLength||
                !checkLowerCharacter||
                !checkUpperCharacter||
                !checkDigitCharacter||
                !checkSpecialCharacter||
                checkContainsSpecial
    }
}