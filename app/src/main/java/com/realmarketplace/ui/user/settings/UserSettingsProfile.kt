package com.realmarketplace.ui.user.settings

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.realmarketplace.R
import com.realmarketplace.databinding.FragmentUserSettingsProfileBinding
import com.realmarketplace.model.LightUser
import com.realmarketplace.model.UserTokenAuth
import com.realmarketplace.model.text.TextModelAuth
import com.realmarketplace.ui.auth.AuthViewModel
import com.realmarketplace.ui.user.UserViewModel
import com.realmarketplace.viewModel.ToastObject

class UserSettingsProfile : Fragment() {
    private var _binding: FragmentUserSettingsProfileBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var alertBuilder:AlertDialog.Builder
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserSettingsProfileBinding.inflate(inflater, container, false)
        alertBuilder = AlertDialog.Builder(context)
        loadUser()
        checkAll()
        focusFirstName()
        focusLastName()
        focusPhone()
        binding.settingsUpdateUserProfile.setOnClickListener(){
            binding.settingsFirstNameInput.clearFocus()
            binding.settingsLastNameInput.clearFocus()
            binding.settingsPhoneInput.clearFocus()
            checkAll()
            submitForm()
        }
        return binding.root
    }
    private fun submitForm(){
        if(submitFormVerification()){
            if(userViewModel.buttonEnables&&!checkChanges()){
                val token: UserTokenAuth = AuthViewModel.userToken.value?: UserTokenAuth("")
                alertBuilder.setTitle("Are you sure you want to update your profile?")
                    .setCancelable(true)
                    .setPositiveButton("YES"){_,it->
                        userViewModel.buttonEnables=false
                        var newUser: LightUser = userViewModel.user.value!!
                        newUser.lastName = binding.settingsLastNameInput.text.toString()
                        newUser.firstName = binding.settingsFirstNameInput.text.toString()
                        newUser.phone = binding.settingsPhoneInput.text.toString()
                        context?.let {
                            userViewModel.updateUser(newUser,token,it)
                        }
                    }
                    .setNegativeButton("NO"){_,it->
                    }.show()
            }else{
                if(userViewModel.buttonEnables)context?.let { ToastObject.showToast(it,"You didn't do any changes.",
                    Toast.LENGTH_LONG) }
            }
        }else{
            if(userViewModel.buttonEnables) context?.let { ToastObject.showToast(it, TextModelAuth.SOME_INVALID_FIELDS,
                Toast.LENGTH_LONG) }
        }
    }
    private fun submitFormVerification():Boolean{
        val validFirstName = binding.settingsFirstNameLayout.helperText==null
        val validLastName = binding.settingsLastNameLayout.helperText==null
        val validPhone = binding.settingsPhoneLayout.helperText==null
        return validFirstName&&
                validLastName&&
                validPhone
    }
    private fun loadUser(){
        binding.settingsFirstNameInput.setText(userViewModel.user.value?.firstName)
        binding.settingsLastNameInput.setText(userViewModel.user.value?.lastName)
        binding.settingsPhoneInput.setText(userViewModel.user.value?.phone)
        binding.settingsEmailInput.setText(userViewModel.user.value?.email)
    }
    private fun checkAll(){
        if(binding.settingsFirstNameInput.text?.isEmpty()==false)binding.settingsFirstNameLayout.helperText = validFirstName()
        if(binding.settingsLastNameInput.text?.isEmpty()==false)binding.settingsLastNameLayout.helperText = validLastName()
        if(binding.settingsPhoneInput.text?.isEmpty()==false)binding.settingsPhoneLayout.helperText = validPhone()
    }
    private fun checkChanges():Boolean{
        val changeFirstName = binding.settingsFirstNameInput.text.toString()==userViewModel.user.value?.firstName
        val changeLastName = binding.settingsLastNameInput.text.toString()==userViewModel.user.value?.lastName
        val changePhone = binding.settingsPhoneInput.text.toString()==userViewModel.user.value?.phone
        return changeFirstName&&
                changeLastName&&
                changePhone
    }
    private fun focusFirstName(){
        binding.settingsFirstNameInput.setOnFocusChangeListener(){ _, focused ->
            if (!focused) {
                binding.settingsFirstNameLayout.helperText = validFirstName()
            }
        }
    }
    private fun validFirstName():String?{
        val firstName = binding.settingsFirstNameInput.text.toString()
        if(firstName.isNullOrEmpty()){
            binding.settingsFirstNameInput.error = TextModelAuth.INCORRECT_FIRST_NAME_TOOLTIP
            return TextModelAuth.INCORRECT_FIRST_NAME
        }
        binding.settingsFirstNameInput.error = null
        return null
    }
    private fun focusLastName(){
        binding.settingsLastNameInput.setOnFocusChangeListener(){ _, focused ->
            if (!focused) {
                binding.settingsLastNameLayout.helperText = validLastName()
            }
        }
    }
    private fun validLastName():String?{
        val lastName = binding.settingsLastNameInput.text.toString()
        if(lastName.isNullOrEmpty()){
            binding.settingsLastNameInput.error = TextModelAuth.INCORRECT_LAST_NAME_TOOLTIP
            return TextModelAuth.INCORRECT_LAST_NAME
        }
        binding.settingsLastNameInput.error = null
        return null
    }
    private fun focusPhone(){
        binding.settingsPhoneInput.setOnFocusChangeListener(){ _, focused ->
            if (!focused && binding.settingsPhoneInput.text?.isNotEmpty()==true) {
                binding.settingsPhoneLayout.helperText = validPhone()
            }else if(!focused && binding.settingsPhoneLayout.helperText!=resources.getString(R.string.required))binding.settingsPhoneLayout.helperText = resources.getString(R.string.required)
        }
    }
    private fun validPhone():String?{
        val phone = binding.settingsPhoneInput.text.toString()
        if(!phone.matches("[0-9]{9}".toRegex())){
            binding.settingsPhoneInput.error = TextModelAuth.INCORRECT_PHONE_NUMBER_TOOLTIP
            return TextModelAuth.INCORRECT_PHONE_NUMBER
        }
        binding.settingsPhoneInput.error = null
        return null
    }
    companion object {
        fun newInstance() = UserSettingsProfile()
    }
}