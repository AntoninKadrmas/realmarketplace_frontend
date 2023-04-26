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
import com.realmarketplace.viewModel.LoadingBar
import com.realmarketplace.viewModel.ToastObject

/**
 * A group of *fragment*.
 *
 * Class for fragment_user_settings_password layout and logic there.
 */
class UserSettingsPassword : Fragment() {
    private var _binding: FragmentUserSettingsPasswordBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var alertBuilder:AlertDialog.Builder
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserSettingsPasswordBinding.inflate(inflater, container, false)
        alertBuilder = AlertDialog.Builder(context)
        focusPasswordFirst()
        focusPasswordSecond()
        focusPasswordPrevious()
        checkAll()
        binding.changePassword.setOnClickListener(){
            binding.passwordNewSecondInput.clearFocus()
            binding.passwordNewFirstInput.clearFocus()
            binding.passwordPreviousInput.clearFocus()
            checkAll()
            submitForm()
        }
        return binding.root
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used to check validity of all input filed.
     */
    private fun checkAll(){
        if(binding.passwordNewSecondInput.text?.trim()?.isEmpty() == false)binding.passwordNewSecondLayout.helperText = validPasswordSecond()
        if(binding.passwordNewFirstInput.text?.trim()?.isEmpty() == false)binding.passwordNewFirstLayout.helperText = validPasswordFirst()
        if(binding.passwordPreviousInput.text?.trim()?.isEmpty() == false)binding.passwordPreviousLayout.helperText = validPasswordPrevious()
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used for handle change onclick button and if everything is valid then send change password request.
     */
    private fun submitForm(){
        if(submitFormVerification()){
            if(userViewModel.buttonEnables.value!!){
                val token: UserTokenAuth = AuthViewModel.userToken.value?: UserTokenAuth("")
                alertBuilder.setTitle("Are you sure you want to update your password.")
                    .setCancelable(true)
                    .setPositiveButton("YES"){_,it->
                        userViewModel.buttonEnables.value=false
                        LoadingBar.mutableHideLoadingUserSettingsActivity.value=false
                        context?.let { userViewModel.updatePassword(
                            binding.passwordPreviousInput.text.toString().trim(),
                            binding.passwordNewSecondInput.text.toString().trim(),
                            token,
                            it
                        ) }
                    }
                    .setNegativeButton("NO"){_,it->
                    }.show()
            }
        }else{
            if(userViewModel.buttonEnables.value!!) context?.let { ToastObject.makeText(it,
                TextModelAuth.SOME_INVALID_FIELDS,Toast.LENGTH_LONG) }
        }
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used to check if all input filed are valid.
     * @return true if all field are valid else false
     */
    private fun submitFormVerification():Boolean{
        val validPasswordPrevious = binding.passwordPreviousLayout.helperText==null
        val validPasswordFirst = binding.passwordNewFirstLayout.helperText==null
        val validPasswordSecond = binding.passwordNewSecondLayout.helperText==null
        return validPasswordPrevious&&
                validPasswordFirst&&
                validPasswordSecond
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used to set focus listener on password previous input filed.
     */
    private fun focusPasswordPrevious(){
        binding.passwordPreviousInput.setOnFocusChangeListener(){ _, focused ->
            if (!focused && binding.passwordPreviousInput.text?.trim()?.isNotEmpty()==true) {
                binding.passwordPreviousLayout.helperText = validPasswordPrevious()
            }else if(!focused && binding.passwordPreviousLayout.helperText!=resources.getString(R.string.required))binding.passwordPreviousLayout.helperText = resources.getString(R.string.required)
        }
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used for password previous validation.
     * @return string if there is still some invalid part else null.
     */
    private fun validPasswordPrevious():String?{
        val passwordText = binding.passwordPreviousInput.text.toString().trim()
        if(AuthViewModel.checkPassword(passwordText)){
            binding.passwordPreviousInput.setError(TextModelAuth.INCORRECT_PASSWORD_TOOLTIP,null)
            return TextModelAuth.INCORRECT_PASSWORD
        }
        binding.passwordPreviousInput.error = null
        return null
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used to set focus listener on password first input filed.
     */
    private fun focusPasswordFirst(){
        binding.passwordNewFirstInput.setOnFocusChangeListener(){ _, focused ->
            if (!focused && binding.passwordNewFirstInput.text?.trim()?.isNotEmpty()==true) {
                binding.passwordNewFirstLayout.helperText = validPasswordFirst()
            }else if(!focused && binding.passwordNewFirstLayout.helperText!=resources.getString(R.string.required))binding.passwordNewFirstLayout.helperText = resources.getString(R.string.required)
        }
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used for password first validation.
     * @return string if there is still some invalid part else null.
     */
    private fun validPasswordFirst():String?{
        val passwordText = binding.passwordNewFirstInput.text.toString().trim()
        val checkSamePasswordPrevious = binding.passwordPreviousInput.text.toString().trim()==passwordText
        if(AuthViewModel.checkPassword(passwordText)){
            binding.passwordNewFirstInput.setError(TextModelAuth.INCORRECT_PASSWORD_TOOLTIP,null)
            return TextModelAuth.INCORRECT_PASSWORD
        }
        if(binding.passwordNewSecondInput.text?.trim()?.isNotEmpty()==true)binding.passwordNewSecondLayout.helperText = validPasswordSecond()
        if(checkSamePasswordPrevious)return TextModelAuth.SAME_PASSWORD
        binding.passwordNewFirstInput.error = null
        return null
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used to set focus listener on password second input filed.
     */
    private fun focusPasswordSecond(){
        binding.passwordNewSecondInput.setOnFocusChangeListener(){ _, focused ->
            if (!focused && binding.passwordNewSecondInput.text?.trim()?.isNotEmpty()==true) {
                binding.passwordNewSecondLayout.helperText = validPasswordSecond()
            }else if(!focused && binding.passwordNewSecondLayout.helperText!=resources.getString(R.string.required))binding.passwordNewSecondLayout.helperText = resources.getString(R.string.required)
        }
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used for password second validation.
     * @return string if there is still some invalid part else null.
     */
    private fun validPasswordSecond():String?{
        val passwordText = binding.passwordNewSecondInput.text.toString().trim()
        val checkSamePassword = binding.passwordNewFirstInput.text.toString().trim()==passwordText
        val checkSamePasswordPrevious = binding.passwordPreviousInput.text.toString().trim()==passwordText
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