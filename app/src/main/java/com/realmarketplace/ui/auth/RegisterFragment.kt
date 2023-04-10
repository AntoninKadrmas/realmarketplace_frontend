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
import com.realmarketplace.databinding.FragmentSigninBinding
import com.realmarketplace.model.text.TextModelAuth
import com.realmarketplace.model.UserModel
import com.realmarketplace.model.Validated
import com.realmarketplace.viewModel.ToastObject

class RegisterFragment : Fragment() {
    companion object {
        fun newInstance() = RegisterFragment()
    }
    private var _binding: FragmentSigninBinding? = null
    private val binding get() = _binding!!
    var buttonEnables = true
    override fun onResume() {
        super.onResume()
        checkAll()
    }
    private fun checkAll(){
        if (binding.passwordSecondInput.text?.isEmpty() == false) binding.passwordSecondLayout.helperText = validPasswordSecond()
        if (binding.passwordFirstInput.text?.isEmpty() == false) binding.passwordFirstLayout.helperText = validPasswordFirst()
        if (binding.firstNameInput.text?.isEmpty() == false) binding.firstNameLayout.helperText = validFirstName()
        if (binding.lastNameInput.text?.isEmpty() == false) binding.lastNameLayout.helperText = validLastName()
        if (binding.emailInput.text?.isEmpty() == false) binding.emailLayout.helperText = validEmail()
        if (binding.phoneInput.text?.isEmpty() == false) binding.phoneLayout.helperText = validPhone()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSigninBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.switchToLogin.setOnClickListener(){
            AuthViewModel.selectItem(0)
        }
        binding.signinButton.setOnClickListener(){
            binding.passwordSecondInput.clearFocus()
            binding.passwordFirstInput.clearFocus()
            binding.firstNameInput.clearFocus()
            binding.lastNameInput.clearFocus()
            binding.emailInput.clearFocus()
            binding.phoneInput.clearFocus()
            checkAll()
            submitForm()
        }
        AuthViewModel.buttonsEnabled.observe(viewLifecycleOwner,Observer{
                buttonEnables=true
        })
        focusPasswordSecond()
        focusPasswordFirst()
        focusFirstName()
        focusLastName()
        focusEmail()
        focusPhone()
        return root
    }
    private fun submitForm(){
        val validPasswordSecond= binding.passwordSecondLayout.helperText==null
        val validPasswordFirst = binding.passwordFirstLayout.helperText==null
        val validFirstName = binding.firstNameLayout.helperText==null
        val validLastName = binding.lastNameLayout.helperText==null
        val validEmail = binding.emailLayout.helperText==null
        val validPhone = binding.phoneLayout.helperText==null
        if(validPasswordFirst&&
            validPasswordSecond&&
            validFirstName&&
            validLastName&&
            validEmail&&
            validPhone){
            val newUser= UserModel(
                createdIn= "",
                email= binding.emailInput.text.toString(),
                firstName= binding.firstNameInput.text.toString(),
                lastName= binding.lastNameInput.text.toString(),
                password= binding.passwordFirstInput.text.toString(),
                phone= binding.phoneInput.text.toString(),
                validated= Validated(false,false,false)
            )
                buttonEnables=false
            context?.let { AuthViewModel.register(newUser, it) }
        }else{
            context?.let { ToastObject.showToast(it, TextModelAuth.SOME_INVALID_FIELDS, Toast.LENGTH_LONG) }
        }
    }
    private fun focusFirstName(){
        binding.firstNameInput.setOnFocusChangeListener(){ _, focused ->
            if (!focused) {
                binding.firstNameLayout.helperText = validFirstName()
            }
        }
    }
    private fun validFirstName():String?{
        val firstName = binding.firstNameInput.text.toString()
        if(firstName.isNullOrEmpty()){
            binding.firstNameInput.error = TextModelAuth.INCORRECT_FIRST_NAME_TOOLTIP
            return TextModelAuth.INCORRECT_FIRST_NAME
        }
        binding.firstNameInput.error = null
        return null
    }
    private fun focusLastName(){
        binding.lastNameInput.setOnFocusChangeListener(){ _, focused ->
            if (!focused) {
                binding.lastNameLayout.helperText = validLastName()
            }
        }
    }
    private fun validLastName():String?{
        val lastName = binding.lastNameInput.text.toString()
        if(lastName.isNullOrEmpty()){
            binding.lastNameInput.error = TextModelAuth.INCORRECT_LAST_NAME_TOOLTIP
            return TextModelAuth.INCORRECT_LAST_NAME
        }
        binding.lastNameInput.error = null
        return null
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
    private fun focusPhone(){
        binding.phoneInput.setOnFocusChangeListener(){ _, focused ->
            if (!focused && binding.phoneInput.text?.isNotEmpty()==true) {
                binding.phoneLayout.helperText = validPhone()
            }else if(!focused && binding.phoneLayout.helperText!=resources.getString(R.string.required))binding.phoneLayout.helperText = resources.getString(R.string.required)
        }
    }
    private fun validPhone():String?{
        val phone = binding.phoneInput.text.toString()
        if(!phone.matches("[0-9]{9}".toRegex())){
            binding.phoneInput.error = TextModelAuth.INCORRECT_PHONE_NUMBER_TOOLTIP
            return TextModelAuth.INCORRECT_PHONE_NUMBER
        }
        binding.phoneInput.error = null
        return null
    }
    private fun focusPasswordFirst(){
        binding.passwordFirstInput.setOnFocusChangeListener(){ _, focused ->
            if (!focused && binding.passwordFirstInput.text?.isNotEmpty()==true) {
                binding.passwordFirstLayout.helperText = validPasswordFirst()
            }else if(!focused && binding.passwordFirstLayout.helperText!=resources.getString(R.string.required))binding.passwordFirstLayout.helperText = resources.getString(R.string.required)
        }
    }
    private fun validPasswordFirst():String?{
        val passwordText = binding.passwordFirstInput.text.toString()
        if(AuthViewModel.checkPassword(passwordText)){
                binding.passwordFirstInput.setError(TextModelAuth.INCORRECT_PASSWORD_TOOLTIP,null)
            return TextModelAuth.INCORRECT_PASSWORD
        }
        if(binding.passwordSecondInput.text?.isNotEmpty()==true)binding.passwordSecondLayout.helperText = validPasswordSecond()
        binding.passwordFirstInput.error = null
        return null
    }
    private fun focusPasswordSecond(){
        binding.passwordSecondInput.setOnFocusChangeListener(){ _, focused ->
            if (!focused && binding.passwordSecondInput.text?.isNotEmpty()==true) {
                binding.passwordSecondLayout.helperText = validPasswordSecond()
            }else if(!focused && binding.passwordSecondLayout.helperText!=resources.getString(R.string.required))binding.passwordSecondLayout.helperText = resources.getString(R.string.required)
        }
    }
    private fun validPasswordSecond():String?{
        val passwordText = binding.passwordSecondInput.text.toString()
        val checkSamePassword = binding.passwordFirstInput.text.toString()==passwordText
        if(AuthViewModel.checkPassword(passwordText)){
                binding.passwordSecondInput.setError(TextModelAuth.INCORRECT_PASSWORD_TOOLTIP,null)
                return TextModelAuth.INCORRECT_PASSWORD
        }
        if(!checkSamePassword)return TextModelAuth.DIFFERENT_PASSWORD
        binding.passwordSecondInput.error = null
        return null
    }
}