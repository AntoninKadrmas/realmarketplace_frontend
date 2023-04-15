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
import com.realmarketplace.viewModel.LoadingBar
import com.realmarketplace.viewModel.ToastObject

/**
 * A group of *fragment*.
 *
 * Class for fragment_signin layout and logic there.
 */
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
    /**
     * A group of *fragment_function*.
     *
     * Function used to check validity of all input filed.
     */
    private fun checkAll(){
        if (binding.passwordSecondInput.text?.trim()?.isEmpty() == false) binding.passwordSecondLayout.helperText = validPasswordSecond()
        if (binding.passwordFirstInput.text?.trim()?.isEmpty() == false) binding.passwordFirstLayout.helperText = validPasswordFirst()
        if (binding.firstNameInput.text?.trim()?.isEmpty() == false) binding.firstNameLayout.helperText = validFirstName()
        if (binding.lastNameInput.text?.trim()?.isEmpty() == false) binding.lastNameLayout.helperText = validLastName()
        if (binding.emailInput.text?.trim()?.isEmpty() == false) binding.emailLayout.helperText = validEmail()
        if (binding.phoneInput.text?.trim()?.isEmpty() == false) binding.phoneLayout.helperText = validPhone()
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used for handle register onclick button and if everything is valid then send register request.
     */
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
                email= binding.emailInput.text.toString().trim(),
                firstName= binding.firstNameInput.text.toString().trim(),
                lastName= binding.lastNameInput.text.toString().trim(),
                password= binding.passwordFirstInput.text.toString().trim(),
                phone= binding.phoneInput.text.toString().trim(),
                validated= Validated(false,false,false)
            )
            buttonEnables=false
            LoadingBar.mutableHideLoadingAuthActivity.value=false
            context?.let { AuthViewModel.register(newUser, it) }
        }else{
            context?.let { ToastObject.showToast(it, TextModelAuth.SOME_INVALID_FIELDS, Toast.LENGTH_LONG) }
        }
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used to set focus listener on first name input filed.
     */
    private fun focusFirstName(){
        binding.firstNameInput.setOnFocusChangeListener(){ _, focused ->
            if (!focused) {
                binding.firstNameLayout.helperText = validFirstName()
            }
        }
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used for first name validation.
     * @return string if there is still some invalid part else null.
     */
    private fun validFirstName():String?{
        val firstName = binding.firstNameInput.text.toString().trim()
        if(firstName.isNullOrEmpty()){
            binding.firstNameInput.error = TextModelAuth.INCORRECT_FIRST_NAME_TOOLTIP
            return TextModelAuth.INCORRECT_FIRST_NAME
        }
        binding.firstNameInput.error = null
        return null
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used to set focus listener on last name input filed.
     */
    private fun focusLastName(){
        binding.lastNameInput.setOnFocusChangeListener(){ _, focused ->
            if (!focused) {
                binding.lastNameLayout.helperText = validLastName()
            }
        }
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used for last name validation.
     * @return string if there is still some invalid part else null.
     */
    private fun validLastName():String?{
        val lastName = binding.lastNameInput.text.toString().trim()
        if(lastName.isNullOrEmpty()){
            binding.lastNameInput.error = TextModelAuth.INCORRECT_LAST_NAME_TOOLTIP
            return TextModelAuth.INCORRECT_LAST_NAME
        }
        binding.lastNameInput.error = null
        return null
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used to set focus listener on email input filed.
     */
    private fun focusEmail(){
        binding.emailInput.setOnFocusChangeListener(){ _, focused ->
            if (!focused && binding.emailInput.text?.trim()?.isNotEmpty()==true) {
                binding.emailLayout.helperText = validEmail()
            }else if(!focused && binding.emailLayout.helperText!=resources.getString(R.string.required))binding.emailLayout.helperText = resources.getString(R.string.required)
        }
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used for email validation.
     * @return string if there is still some invalid part else null.
     */
    private fun validEmail():String?{
        val email = binding.emailInput.text.toString().trim()
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailInput.error = TextModelAuth.INCORRECT_EMAIL_TOOLTIP
            return TextModelAuth.INCORRECT_EMAIL
        }
        binding.emailInput.error = null
        return null
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used to set focus listener on phone number input filed.
     */
    private fun focusPhone(){
        binding.phoneInput.setOnFocusChangeListener(){ _, focused ->
            if (!focused && binding.phoneInput.text?.trim()?.isNotEmpty()==true) {
                binding.phoneLayout.helperText = validPhone()
            }else if(!focused && binding.phoneLayout.helperText!=resources.getString(R.string.required))binding.phoneLayout.helperText = resources.getString(R.string.required)
        }
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used for phone number validation.
     * @return string if there is still some invalid part else null.
     */
    private fun validPhone():String?{
        val phone = binding.phoneInput.text.toString().trim()
        if(!phone.matches("[0-9]{9}".toRegex())){
            binding.phoneInput.error = TextModelAuth.INCORRECT_PHONE_NUMBER_TOOLTIP
            return TextModelAuth.INCORRECT_PHONE_NUMBER
        }
        binding.phoneInput.error = null
        return null
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used to set focus listener on first password input filed.
     */
    private fun focusPasswordFirst(){
        binding.passwordFirstInput.setOnFocusChangeListener(){ _, focused ->
            if (!focused && binding.passwordFirstInput.text?.trim()?.isNotEmpty()==true) {
                binding.passwordFirstLayout.helperText = validPasswordFirst()
            }else if(!focused && binding.passwordFirstLayout.helperText!=resources.getString(R.string.required))binding.passwordFirstLayout.helperText = resources.getString(R.string.required)
        }
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used for first password validation.
     * @return string if there is still some invalid part else null.
     */
    private fun validPasswordFirst():String?{
        val passwordText = binding.passwordFirstInput.text.toString().trim()
        if(AuthViewModel.checkPassword(passwordText)){
                binding.passwordFirstInput.setError(TextModelAuth.INCORRECT_PASSWORD_TOOLTIP,null)
            return TextModelAuth.INCORRECT_PASSWORD
        }
        if(binding.passwordSecondInput.text?.trim()?.isNotEmpty()==true)binding.passwordSecondLayout.helperText = validPasswordSecond()
        binding.passwordFirstInput.error = null
        return null
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used to set focus listener on second password input filed.
     */
    private fun focusPasswordSecond(){
        binding.passwordSecondInput.setOnFocusChangeListener(){ _, focused ->
            if (!focused && binding.passwordSecondInput.text?.trim()?.isNotEmpty()==true) {
                binding.passwordSecondLayout.helperText = validPasswordSecond()
            }else if(!focused && binding.passwordSecondLayout.helperText!=resources.getString(R.string.required))binding.passwordSecondLayout.helperText = resources.getString(R.string.required)
        }
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used for second password validation.
     * @return string if there is still some invalid part else null.
     */
    private fun validPasswordSecond():String?{
        val passwordText = binding.passwordSecondInput.text.toString().trim()
        val checkSamePassword = binding.passwordFirstInput.text.toString().trim()==passwordText
        if(AuthViewModel.checkPassword(passwordText)){
                binding.passwordSecondInput.setError(TextModelAuth.INCORRECT_PASSWORD_TOOLTIP,null)
                return TextModelAuth.INCORRECT_PASSWORD
        }
        if(!checkSamePassword)return TextModelAuth.DIFFERENT_PASSWORD
        binding.passwordSecondInput.error = null
        return null
    }
}