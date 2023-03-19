package com.example.omega1.ui.auth

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.omega1.R
import com.example.omega1.databinding.FragmentSigninBinding
import com.example.omega1.rest.*
import com.example.omega1.model.UserModel
import com.example.omega1.model.Validated

class RegisterFragment : Fragment() {
    companion object {
        fun newInstance() = RegisterFragment()
    }
    private val viewModel: AuthViewModel by activityViewModels()
    private var _binding: FragmentSigninBinding? = null
    private val binding get() = _binding!!
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
        if (binding.idInput.text?.isEmpty() == false) binding.idLayout.helperText = validIdNumber()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSigninBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.switchToLogin.setOnClickListener(){
            viewModel.selectItem(0)
        }
        binding.signinButton.setOnClickListener(){
            binding.passwordSecondInput.clearFocus()
            binding.passwordFirstInput.clearFocus()
            binding.firstNameInput.clearFocus()
            binding.lastNameInput.clearFocus()
            binding.emailInput.clearFocus()
            binding.phoneInput.clearFocus()
            binding.idInput.clearFocus()
            checkAll()
            submitForm()
        }
        focusPasswordSecond()
        focusPasswordFirst()
        focusFirstName()
        focusLastName()
        focusEmail()
        focusPhone()
        focusIdNumber()
        return root
    }
    private fun submitForm(){
        val validPasswordSecond= binding.passwordSecondLayout.helperText==null
        val validPasswordFirst = binding.passwordFirstLayout.helperText==null
        val validFirstName = binding.firstNameLayout.helperText==null
        val validLastName = binding.lastNameLayout.helperText==null
        val validEmail = binding.emailLayout.helperText==null
        val validPhone = binding.phoneLayout.helperText==null
        val validIdNumber = binding.idLayout.helperText==null
        if(validPasswordFirst&&
            validPasswordSecond&&
            validIdNumber&&
            validFirstName&&
            validLastName&&
            validEmail&&
            validPhone){
            val newUser:UserModel=UserModel(
                createdIn= "",
                email= binding.emailInput.text.toString(),
                first_name= binding.emailInput.text.toString(),
                last_name= binding.lastNameInput.text.toString(),
                cardId= binding.idInput.text.toString(),
                password= binding.passwordFirstInput.text.toString(),
                phone= binding.phoneInput.text.toString(),
                validated= Validated(false,false,false,false,false)
            )
            context?.let { viewModel.register(newUser, it) }
        }else{
            Toast.makeText(context,"Some of the input fields are still invalid!", Toast.LENGTH_LONG).show()
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
            binding.firstNameInput.error ="First name can't be empty."
            return "Invalid first name"
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
            binding.lastNameInput.error ="Last name can't be empty."
            return "Invalid last name"
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
            binding.emailInput.error ="Email address has to be in the format: sometext@sometex.sometext"
            return "Invalid email address"
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
            binding.phoneInput.error ="Phone number has to contains exactly nine numbers."
            return "Invalid phone number"
        }
        binding.phoneInput.error = null
        return null
    }
    private fun focusIdNumber(){
        binding.idInput.setOnFocusChangeListener(){ _, focused ->
            if (!focused && binding.idInput.text?.isNotEmpty()==true) {
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
    private fun focusPasswordFirst(){
        binding.passwordFirstInput.setOnFocusChangeListener(){ _, focused ->
            if (!focused && binding.passwordFirstInput.text?.isNotEmpty()==true) {
                binding.passwordFirstLayout.helperText = validPasswordFirst()
            }else if(!focused && binding.passwordFirstLayout.helperText!=resources.getString(R.string.required))binding.passwordFirstLayout.helperText = resources.getString(R.string.required)
        }
    }
    private fun validPasswordFirst():String?{
        val passwordText = binding.passwordFirstInput.text.toString()
        val checkLength = passwordText.length>=8
        val checkLowerCharacter = passwordText.matches(".*[a-z]+.*".toRegex())
        val checkUpperCharacter = passwordText.matches(".*[A-Z]+.*".toRegex())
        val checkDigitCharacter = passwordText.matches(".*[0-9]+.*".toRegex())
        val checkSpecialCharacter = passwordText.matches(".*[^A-Za-z0-9\\s]+.*".toRegex())
        if(
            !checkLength||
            !checkLowerCharacter||
            !checkUpperCharacter||
            !checkDigitCharacter||
            !checkSpecialCharacter){
                binding.passwordFirstInput.setError("Password has to be at minimum 8 characters long. Has to contains lower character, uppercase character, special character and number.",null)
            return "Invalid password"
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
        val checkLength = passwordText.length>=8
        val checkLowerCharacter = passwordText.matches(".*[a-z]+.*".toRegex())
        val checkUpperCharacter = passwordText.matches(".*[A-Z]+.*".toRegex())
        val checkDigitCharacter = passwordText.matches(".*[0-9]+.*".toRegex())
        val checkSpecialCharacter = passwordText.matches(".*[^A-Za-z0-9\\s]+.*".toRegex())
        val checkSamePassword = binding.passwordFirstInput.text.toString()==passwordText
        if(!checkLength||
            !checkLowerCharacter||
            !checkUpperCharacter||
            !checkDigitCharacter||
            !checkSpecialCharacter){
                binding.passwordSecondInput.setError("Password has to be at minimum 8 characters long. Has to contains lower character, uppercase character, special character and number.",null)
                return "Invalid Password"
        }
        if(!checkSamePassword)return "Password Is Different"
        binding.passwordSecondInput.error = null
        return null
    }
}