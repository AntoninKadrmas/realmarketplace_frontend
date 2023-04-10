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
import com.realmarketplace.databinding.FragmentUserSettingsPasswordBinding
import com.realmarketplace.model.UserTokenAuth
import com.realmarketplace.model.text.TextModelAuth
import com.realmarketplace.ui.auth.AuthViewModel
import com.realmarketplace.ui.user.UserViewModel
import com.realmarketplace.viewModel.ToastObject

class UserSettingsPassword : Fragment() {
    private var _binding: FragmentUserSettingsPasswordBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var alertBuilder:AlertDialog.Builder
    private fun checkAll(){
        if(binding.passwordNewSecondInput.text?.isEmpty() == false)binding.passwordNewSecondLayout.helperText = validPasswordSecond()
        if(binding.passwordNewFirstInput.text?.isEmpty() == false)binding.passwordNewFirstLayout.helperText = validPasswordFirst()
        if(binding.passwordPreviousInput.text?.isEmpty() == false)binding.passwordPreviousLayout.helperText = validPasswordPrevious()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserSettingsPasswordBinding.inflate(inflater, container, false)
        alertBuilder = AlertDialog.Builder(context)
        focusPasswordFirst()
        focusPasswordSecond()
        focusPasswordPrevious()
        binding.changePassword.setOnClickListener(){
            binding.passwordNewSecondInput.clearFocus()
            binding.passwordNewFirstInput.clearFocus()
            binding.passwordPreviousInput.clearFocus()
            checkAll()
            submitForm()
        }
        return binding.root
    }
    private fun submitForm(){
        if(submitFormVerification()){
            if(userViewModel.buttonEnables){
                val token: UserTokenAuth = AuthViewModel.userToken.value?: UserTokenAuth("")
                alertBuilder.setTitle("Are you sure you want to update your password.")
                    .setCancelable(true)
                    .setPositiveButton("YES"){_,it->
                        userViewModel.buttonEnables=false
                        context?.let { userViewModel.updatePassword(
                            binding.passwordPreviousInput.text.toString(),
                            binding.passwordNewSecondInput.text.toString(),
                            token,
                            it
                        ) }
                    }
                    .setNegativeButton("NO"){_,it->
                    }.show()
            }
        }else{
            if(userViewModel.buttonEnables) context?.let { ToastObject.showToast(it,
                TextModelAuth.SOME_INVALID_FIELDS,Toast.LENGTH_LONG) }
        }
    }
    private fun submitFormVerification():Boolean{
        val validPasswordPrevious = binding.passwordPreviousLayout.helperText==null
        val validPasswordFirst = binding.passwordNewFirstLayout.helperText==null
        val validPasswordSecond = binding.passwordNewSecondLayout.helperText==null
        return validPasswordPrevious&&
                validPasswordFirst&&
                validPasswordSecond
    }
    private fun focusPasswordPrevious(){
        binding.passwordPreviousInput.setOnFocusChangeListener(){ _, focused ->
            if (!focused && binding.passwordPreviousInput.text?.isNotEmpty()==true) {
                binding.passwordPreviousLayout.helperText = validPasswordPrevious()
            }else if(!focused && binding.passwordPreviousLayout.helperText!=resources.getString(R.string.required))binding.passwordPreviousLayout.helperText = resources.getString(R.string.required)
        }
    }
    private fun validPasswordPrevious():String?{
        val passwordText = binding.passwordPreviousInput.text.toString()
        if(AuthViewModel.checkPassword(passwordText)){
            binding.passwordPreviousInput.setError(TextModelAuth.INCORRECT_PASSWORD_TOOLTIP,null)
            return TextModelAuth.INCORRECT_PASSWORD
        }
        binding.passwordPreviousInput.error = null
        return null
    }
    private fun focusPasswordFirst(){
        binding.passwordNewFirstInput.setOnFocusChangeListener(){ _, focused ->
            if (!focused && binding.passwordNewFirstInput.text?.isNotEmpty()==true) {
                binding.passwordNewFirstLayout.helperText = validPasswordFirst()
            }else if(!focused && binding.passwordNewFirstLayout.helperText!=resources.getString(R.string.required))binding.passwordNewFirstLayout.helperText = resources.getString(R.string.required)
        }
    }
    private fun validPasswordFirst():String?{
        val passwordText = binding.passwordNewFirstInput.text.toString()
        val checkSamePasswordPrevious = binding.passwordPreviousInput.text.toString()==passwordText
        if(AuthViewModel.checkPassword(passwordText)){
            binding.passwordNewFirstInput.setError(TextModelAuth.INCORRECT_PASSWORD_TOOLTIP,null)
            return TextModelAuth.INCORRECT_PASSWORD
        }
        if(binding.passwordNewSecondInput.text?.isNotEmpty()==true)binding.passwordNewSecondLayout.helperText = validPasswordSecond()
        if(checkSamePasswordPrevious)return TextModelAuth.SAME_PASSWORD
        binding.passwordNewFirstInput.error = null
        return null
    }
    private fun focusPasswordSecond(){
        binding.passwordNewSecondInput.setOnFocusChangeListener(){ _, focused ->
            if (!focused && binding.passwordNewSecondInput.text?.isNotEmpty()==true) {
                binding.passwordNewSecondLayout.helperText = validPasswordSecond()
            }else if(!focused && binding.passwordNewSecondLayout.helperText!=resources.getString(R.string.required))binding.passwordNewSecondLayout.helperText = resources.getString(R.string.required)
        }
    }
    private fun validPasswordSecond():String?{
        val passwordText = binding.passwordNewSecondInput.text.toString()
        val checkSamePassword = binding.passwordNewFirstInput.text.toString()==passwordText
        val checkSamePasswordPrevious = binding.passwordPreviousInput.text.toString()==passwordText
        if(AuthViewModel.checkPassword(passwordText)){
            binding.passwordNewSecondInput.setError(TextModelAuth.INCORRECT_PASSWORD_TOOLTIP,null)
            return TextModelAuth.INCORRECT_PASSWORD
        }
        if(!checkSamePassword)return TextModelAuth.DIFFERENT_PASSWORD
        else if(checkSamePasswordPrevious)return TextModelAuth.SAME_PASSWORD
        binding.passwordNewSecondInput.error = null
        return null
    }
    companion object {
        fun newInstance() = UserSettingsPassword()
    }
}