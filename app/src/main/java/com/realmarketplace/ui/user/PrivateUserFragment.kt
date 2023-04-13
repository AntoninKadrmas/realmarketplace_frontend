package com.realmarketplace.ui.user

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputLayout
import com.realmarketplace.R
import com.realmarketplace.databinding.FragmentPrivateUserBinding
import com.realmarketplace.model.LightUser
import com.realmarketplace.model.UserTokenAuth
import com.realmarketplace.model.text.TextModelAuth
import com.realmarketplace.ui.auth.AuthViewModel
import com.realmarketplace.ui.auth.LogOutAuth
import com.realmarketplace.ui.search.advert.AdvertAdapter
import com.realmarketplace.ui.user.settings.UserSettings
import com.realmarketplace.viewModel.PermissionViewModel
import com.realmarketplace.viewModel.ToastObject
import com.squareup.picasso.Picasso
import id.zelory.compressor.Compressor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class PrivateUserFragment : Fragment() {
    private var _binding: FragmentPrivateUserBinding? = null
    private val userViewModel: UserViewModel by activityViewModels()
    private val permissionModel: PermissionViewModel by activityViewModels()
    private lateinit var alertBuilder: AlertDialog.Builder
    private lateinit var file:File
    private val binding get() = _binding!!
    private lateinit var user: LightUser

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        alertBuilder = AlertDialog.Builder(context)
        _binding = FragmentPrivateUserBinding.inflate(inflater, container, false)
        userViewModel.user.observe(viewLifecycleOwner, Observer {
            user = it
            loadUserInformation()
        })
        binding.privateLogout.setOnClickListener(){
            LogOutAuth.setLogOut(true)
        }
        binding.privateUpdateImage.setOnClickListener(){
            imageClickAdd()
        }
        binding.privateUpdateProfile.setOnClickListener(){
            var intent = Intent(context, UserSettings::class.java)
            intent.putExtra("user",user)
            intent.putExtra("type",0)
            startActivityForResult(intent,10)
        }
        binding.privateUpdatePassword.setOnClickListener(){
            var intent = Intent(context, UserSettings::class.java)
            intent.putExtra("user",user)
            intent.putExtra("type",1)
            startActivityForResult(intent,10)
        }
        binding.privateDeleteAccount.setOnClickListener(){
            val dialogLayout = layoutInflater.inflate(R.layout.adapter_edit_text,null)
            val editText = dialogLayout.findViewById<EditText>(R.id.edit_text_alert)
            editText.setError(TextModelAuth.INCORRECT_PASSWORD_TOOLTIP,null)
            alertBuilder.setTitle("Are you sure you want to delete your account permanently?")
                .setPositiveButton("YES"){_,_->
                    val password = editText.text.toString()
                    if(AuthViewModel.checkPassword(password))Toast.makeText(context,TextModelAuth.INCORRECT_PASSWORD,Toast.LENGTH_SHORT).show()
                    else{
                        val token = AuthViewModel.userToken.value?: UserTokenAuth("")
                        context?.let { it1 ->
                            this.userViewModel.deleteUser(password,token,
                                it1
                            )
                        }
                    }
                }
                .setNegativeButton("NO"){_,_->

                }.setView(dialogLayout)
                .show()
        }
        return binding.root
    }

    companion object {
        fun newInstance() = PrivateUserFragment()
    }
    private fun loadUserInformation(){
        Picasso.get()
            .load("https://realmarketplace.shop/user${user?.mainImageUrl}")
            .placeholder(R.drawable.baseline_account_circle)
            .into(binding.privateImageUserAdvert)
        binding.privateFirstNameText.text = user.firstName
        binding.privateLastNameText.text = user.lastName
        binding.privateMail.text = user.email
        binding.privatePhone.text = user.phone
        binding.privateCreatedIn.text = AdvertAdapter.formatDate(user.createdIn)
    }
    private fun imageClickAdd(){
        permissionModel.setPermissionStorageAsk(true)
        if(permissionModel.permissionStorage.value==true){
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            intent.type = "image/*"
            intent.putExtra(Intent.ACTION_GET_CONTENT, true)
            startActivityForResult(Intent.createChooser(intent,"Select Picture"), 15)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {//stejne
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==15){
            if(resultCode== Activity.RESULT_OK){
                try {
                    val uri = data?.data
                    val extensionList = listOf("png", "jpg", "svg", "jpeg")
                    CoroutineScope(Dispatchers.Main).launch {
                        println(uri)
                        file = context?.let { it ->
                            UriToFileConvertor.getRealPathFromURI(
                                it,
                                uri
                            )?.let { it->
                                File(
                                    it
                                )
                            }
                        }!!
                        val extension = file?.absolutePath.toString()
                            .substring(file?.absolutePath.toString().lastIndexOf(".") + 1)
                        file = context?.let { Compressor.compress(it, file!!) }!!
                        if (extensionList.contains(extension)) {
                            AuthViewModel.userToken.value?.let {
                                userViewModel.uploadUserImage(user,file,
                                    it, requireContext()
                                )
                            }
                        } else context?.let {
                            ToastObject.showToast(
                                it,
                                "($extension)Allowed file extensions are ${
                                    extensionList.joinToString(", ")
                                }.",
                                Toast.LENGTH_LONG
                            )
                        }
                    }
                }catch (e:Exception){
                    context?.let { ToastObject.showToast(it,"Image type is not supported.", Toast.LENGTH_LONG) }
                }
            }
        }
        else if (requestCode == 10) {
            if (resultCode == Activity.RESULT_OK) {
                val logOut: Boolean? = data?.getBooleanExtra("logOut",false)
                val user: LightUser = data?.getSerializableExtra("user") as LightUser
                userViewModel.updateUserInfo        (user)
                if(logOut!!) LogOutAuth.setLogOut(true)
            }
        }
    }
}