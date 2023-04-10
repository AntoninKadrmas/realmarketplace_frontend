package com.realmarketplace.ui.auth

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.realmarketplace.R
import com.realmarketplace.databinding.FragmentLoginBinding
import com.realmarketplace.model.UserModelLogin
import com.realmarketplace.model.text.TextModelAuth
import com.realmarketplace.viewModel.ToastObject


class LoginFragment : Fragment() {
    companion object {
        fun newInstance() = LoginFragment()
    }
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    var buttonEnables = true
    override fun onResume() {
        super.onResume()
        checkAll()
    }
    private fun checkAll(){
        if(binding.emailInput.text?.isEmpty() == false)binding.emailLayout.helperText = validEmail()
        if(binding.passwordInput.text?.isEmpty() == false)binding.passwordLayout.helperText = validPassword()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.switchToSignin.setOnClickListener(){
            AuthViewModel.selectItem(1)
        }
        binding.loginButton.setOnClickListener(){
            binding.passwordInput.clearFocus()
            binding.emailInput.clearFocus()
            checkAll()
            submitForm()
        }
        AuthViewModel.buttonsEnabled.observe(viewLifecycleOwner,Observer{
            buttonEnables=true
        })
        if(binding.emailInput.error !=null)binding.emailLayout.helperText = validEmail()
        if(binding.passwordInput.error !=null)binding.passwordLayout.helperText = validPassword()
        focusEmail()
        focusPassword()
        return root
    }
    private fun submitForm(){
        val validIdNumber = binding.emailLayout.helperText==null
        val validPassword = binding.passwordLayout.helperText==null
        if(validPassword&&validIdNumber&&buttonEnables){
            val userModel: UserModelLogin = UserModelLogin(
                email= binding.emailInput.text.toString(),
                password= binding.passwordInput.text.toString(),
            )
            buttonEnables=false
            context?.let { AuthViewModel.login(userModel, it) }
        }else{
            context?.let { ToastObject.showToast(it, TextModelAuth.SOME_INVALID_FIELDS,Toast.LENGTH_LONG) }
        }
    }
    private fun focusEmail(){
        binding.emailInput.setOnFocusChangeListener(){ _, focused ->
            if (!focused && binding.emailInput.text?.isNotEmpty()==true) {
                binding.emailLayout.helperText = validEmail()
            }else if(!focused && binding.emailLayout.helperText!=resources.getString(R.string.required))binding.emailLayout.helperText = resources.getString(R.string.required)
        }
    }
    private fun validEmail():String?{
        val email = binding.emailInput.text.toString()
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailInput.error = TextModelAuth.INCORRECT_EMAIL_TOOLTIP
            return TextModelAuth.INCORRECT_EMAIL
        }
        binding.emailInput.error = null
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
        if(AuthViewModel.checkPassword(passwordText)){
            binding.passwordInput.setError(TextModelAuth.INCORRECT_PASSWORD_TOOLTIP,null)
            return TextModelAuth.INCORRECT_PASSWORD
        }
        binding.passwordInput.error = null
        return null
    }
}