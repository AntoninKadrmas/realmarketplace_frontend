package com.example.omega1.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.omega1.R
import com.example.omega1.databinding.FragmentLoginBinding
import com.example.omega1.rest.AuthController
import com.example.omega1.model.UserModelLogin


class LoginFragment : Fragment() {
    companion object {
        fun newInstance() = LoginFragment()
    }
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    override fun onResume() {
        super.onResume()

        if(binding.idInput.text?.isEmpty() != true)binding.idLayout.helperText = validIdNumber()
        if(binding.passwordInput.text?.isEmpty()!=true)binding.passwordLayout.helperText = validPassword()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.switchToSignin.setOnClickListener(){
            viewModel.selectItem(1)
        }
        binding.loginButton.setOnClickListener(){
            binding.passwordInput.clearFocus()
            binding.idInput.clearFocus()
            submitForm()
        }
        if(binding.idInput.error !=null)binding.idLayout.helperText = validIdNumber()
        if(binding.passwordInput.error !=null)binding.passwordLayout.helperText = validPassword()
        focusIdNumber()
        focusPassword()
        return root
    }
    private fun submitForm(){
        val validIdNumber = binding.idLayout.helperText==null
        val validPassword = binding.passwordLayout.helperText==null
        if(validPassword&&validIdNumber){
            val userModel:UserModelLogin = UserModelLogin(
                cardId= binding.idInput.text.toString(),
                password= binding.passwordInput.text.toString(),
            )
            context?.let { viewModel.login(userModel, it) }
        }else{
            Toast.makeText(context,"Some of the input fields are still invalid!",Toast.LENGTH_LONG).show()
        }
    }
    private fun focusIdNumber(){
        binding.idInput.setOnFocusChangeListener(){ _, focused ->
            if (!focused && binding.idInput.text?.isNotEmpty() == true) {
                binding.idLayout.helperText = validIdNumber()
            }else if(!focused && binding.idLayout.helperText!=resources.getString(R.string.required))binding.idLayout.helperText = resources.getString(R.string.required)
        }
    }
    private fun validIdNumber():String?{
        val idNumber = binding.idInput.text.toString()
        val regex = Regex("^[0-9]{6}[0-9]{3,4}\$")
        if(!regex.matches(idNumber)||(idNumber.toInt()%11!=0||(idNumber.length<9||idNumber.length>10))){
            binding.idInput.error ="You have to enter there your National ID number from your id card. It should be 9 or 10 digits."
            return "Invalid ID number"
        }
        binding.idInput.error = null
        return null
    }
    private fun focusPassword(){
        binding.passwordInput.setOnFocusChangeListener(){ _, focused ->
            if (!focused && binding.passwordInput.text?.isNotEmpty()==true) {
                binding.passwordLayout.helperText = validPassword()
            }else if(!focused && binding.passwordLayout.helperText!=resources.getString(R.string.required))binding.passwordLayout.helperText = resources.getString(R.string.required)
        }
    }
    private fun validPassword():String?{
        val passwordText = binding.passwordInput.text.toString()
        val checkLength = passwordText.length>=8
        val checkLowerCharacter = passwordText.matches(".*[a-z]+.*".toRegex())
        val checkUpperCharacter = passwordText.matches(".*[A-Z]+.*".toRegex())
        val checkDigitCharacter = passwordText.matches(".*[0-9]+.*".toRegex())
        val checkSpecialCharacter = passwordText.matches(".*[^A-Za-z0-9\\s]+.*".toRegex())
        if(!checkLength||!checkLowerCharacter||!checkUpperCharacter||!checkDigitCharacter||!checkSpecialCharacter){
            binding.passwordInput.setError("Password has to be at minimum 8 characters long. Has to contains lower character, uppercase character, special character and number.",null)
            return "Invalid password"
        }
        binding.passwordInput.error = null
        return null
    }
}