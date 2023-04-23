package com.realmarketplace.ui.auth

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.realmarketplace.model.UserModel
import com.realmarketplace.model.UserModelLogin
import com.realmarketplace.model.UserTokenAuth
import com.realmarketplace.rest.AuthService
import com.realmarketplace.rest.RetrofitInstance
import com.realmarketplace.rest.ReturnTypeError
import com.google.gson.Gson
import com.realmarketplace.model.GuestUserTokenAuth
import com.realmarketplace.rest.ReturnTypeSuccess
import com.realmarketplace.ui.create.crud.CrudAdvertTools
import com.realmarketplace.ui.search.SearchViewModel
import com.realmarketplace.ui.user.UserViewModel
import com.realmarketplace.viewModel.Guest
import com.realmarketplace.viewModel.LoadingBar
import com.realmarketplace.viewModel.ToastObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

/**
 * A group of *view_model*.
 *
 * Object hold user token and selected fragment (register/login).
 * Operates over some of the auth user endpoints.
 */
object AuthViewModel {
    private val mutableSelectedItem = MutableLiveData<Int>()
    val selectedItem: LiveData<Int> get() = mutableSelectedItem
    private val mutableUserToken = MutableLiveData<UserTokenAuth>()
    val userToken: LiveData<UserTokenAuth> get() = mutableUserToken
    var buttonsEnabled=MutableLiveData<Boolean>()
    private val crudTools = CrudAdvertTools()
    val mutableSuccessRestart = MutableLiveData<Boolean>()

    /**
     * A group of *view_model_function*.
     *
     * Function used for change selected item value (0 or 1).
     *
     * @param value number that decide which item (fragment) is going to be selected
     */
    fun selectItem(value: Int) {
        mutableSelectedItem.value = value
    }
    /**
     * A group of *view_model_function*.
     *
     * Function used to update user authenticate token.
     *
     * @param token new user Authenticate token viz. UserTokenAuth
     */
    fun updateUserToken(token: UserTokenAuth) {
        mutableUserToken.value = token
    }
    private var retroServiceAuth: AuthService = RetrofitInstance.getRetroFitInstance().create(
        AuthService::class.java)
    /**
     * A group of *view_model_function*.
     *
     * Function used to call registerUser function and handle the response.
     * @param email email user write into input field
     * @param password password user write into input filed
     * @param userModel user information's needed to create new user account viz. UserModel
     * @param context context of activity or fragment where is function called
     */
    fun register(email:String,password:String,userModel: UserModel, context: Context,guest:Boolean=false,userViewModel:UserViewModel?=null){
        CoroutineScope(Dispatchers.IO).launch {
            val response = try{
                retroServiceAuth.registerUser(
                    okhttp3.Credentials.basic(email, password),
                    userModel)
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
                        updateUserToken(body)
                        SearchViewModel.loadedSampleAdvert=false
                    }
                    if(guest){
                        userViewModel?.updateUserCredentials(UserModelLogin(email,password))
                        Guest.loadAsGuest=true
                    }else Guest.loadAsGuest=false
                    buttonsEnabled.value=true
                }
            }else{
                try {
                    crudTools.errorResponse(response,context)
                    withContext(Dispatchers.Main) {
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
    /**
     * A group of *view_model_function*.
     *
     * Function used to call loginUser function and handle the response.
     *
     * @param userModel user login information viz. UserModelLogin
     * @param context context of activity or fragment where is function called
     */
    fun login(userModel: UserModelLogin, context: Context,guest:Boolean=false){
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
                        updateUserToken(body)
                        SearchViewModel.loadedSampleAdvert=false
                    }
                    buttonsEnabled.value=true
                }
            }else{
                try {
                    crudTools.errorResponse(response,context)
                    withContext(Dispatchers.Main) {
                        buttonsEnabled.value=true
                        Guest.loadAsGuest = guest
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
    /**
     * A group of *view_model_function*.
     *
     * Function used to call loginUser function and handle the response.
     *
     * @param email email which temporary password would be generated
     * @param context context of activity or fragment where is function called
     */
    fun resetPassword(email: String, context: Context){
        CoroutineScope(Dispatchers.IO).launch {
            val response = try{
                retroServiceAuth.resetPassword(email)
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
                val body: ReturnTypeSuccess? = Gson().fromJson(Gson().toJson(response.body()), ReturnTypeSuccess::class.java)
                withContext(Dispatchers.Main) {
                    if (body != null) ToastObject.makeText(context,body.success,Toast.LENGTH_SHORT)
                    mutableSelectedItem.value=0
                    buttonsEnabled.value=true
                }
            }else{
                try {
                    crudTools.errorResponse(response,context)
                    withContext(Dispatchers.Main) {
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
    /**
     * A group of *view_model_function*.
     *
     * Function used for password validation.
     *
     * @param passwordText text prom password input that is going to be validated
     * @return boolean variable that decide if password was valid or not
     */
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