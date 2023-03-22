package com.example.omega1.ui.auth

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.omega1.model.UserModel
import com.example.omega1.model.UserModelLogin
import com.example.omega1.model.UserTokenAuth
import com.example.omega1.rest.AuthService
import com.example.omega1.rest.RetrofitInstance
import com.example.omega1.rest.ReturnTypeError
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class AuthViewModel : ViewModel() {
    private val mutableSelectedItem = MutableLiveData<Int>()
    val selectedItem: LiveData<Int> get() = mutableSelectedItem
    private val mutableUserToken = MutableLiveData<UserTokenAuth>()
    val userToken: LiveData<UserTokenAuth> get() = mutableUserToken
    fun selectItem(item: Int) {
        mutableSelectedItem.value = item
    }
    fun updateUserToken(item: UserTokenAuth) {
        mutableUserToken.value = item
    }
    private var retroServiceAuth: AuthService = RetrofitInstance.getRetroFitInstance().create(AuthService::class.java)
    fun register(userModel: UserModel, context: Context){
        CoroutineScope(Dispatchers.IO).launch {
            val response = try{
                retroServiceAuth.registerUser(userModel)
            }catch (e: IOException){
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }catch (e: HttpException){
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }
            if(response.isSuccessful && response.body()!=null){
                val body: UserTokenAuth? = Gson().fromJson(Gson().toJson(response.body()), UserTokenAuth::class.java)
                withContext(Dispatchers.Main) {
                    if (body != null) {
                        updateUserToken(body)
                    }
                }
            }else{
                val errorBody = response.errorBody()
                try {
                    val errorResponse: ReturnTypeError? = Gson().fromJson(errorBody?.charStream(), ReturnTypeError::class.java)
                    withContext(Dispatchers.Main){
                        Toast.makeText(context,"${errorResponse?.error}", Toast.LENGTH_LONG).show()
                    }
                }
                catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(context,"Server dose not respond.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            return@launch
        }
    }
    fun login(userModel: UserModelLogin, context: Context){
        CoroutineScope(Dispatchers.IO).launch {
            val response = try{
                retroServiceAuth.loginUser(okhttp3.Credentials.basic("username", userModel.password))
            }catch (e: IOException){
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }catch (e: HttpException){
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }
            if(response.isSuccessful && response.body()!=null){
                val body: UserTokenAuth? = Gson().fromJson(Gson().toJson(response.body()), UserTokenAuth::class.java)
                withContext(Dispatchers.Main) {
                    if (body != null) {
                        updateUserToken(body)
                    }
                }
            }else{
                val errorBody = response.errorBody()
                try {
                    val errorResponse: ReturnTypeError? = Gson().fromJson(errorBody?.charStream(), ReturnTypeError::class.java)
                    withContext(Dispatchers.Main){
                        Toast.makeText(context,"${errorResponse?.error}", Toast.LENGTH_LONG).show()
                    }
                }
                catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(context,"Server dose not respond.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            return@launch
        }
    }
}