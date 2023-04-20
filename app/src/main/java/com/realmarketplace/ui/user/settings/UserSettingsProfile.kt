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
import com.realmarketplace.viewModel.LoadingBar
import com.realmarketplace.viewModel.ToastObject

/**
 * A group of *fragment*.
 *
 * Class for fragment_user_settings_profile layout and logic there.
 */
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
    /**
     * A group of *fragment_function*.
     *
     * Function used for handle change onclick button and if everything is valid then send change user profile request.
     */
    private fun submitForm(){
        if(submitFormVerification()){
            if(userViewModel.buttonEnables.value!!&&!checkChanges()){
                val token: UserTokenAuth = AuthViewModel.userToken.value?: UserTokenAuth("")
                alertBuilder.setTitle("Are you sure you want to update your profile?")
                    .setCancelable(true)
                    .setPositiveButton("YES"){_,it->
                        userViewModel.buttonEnables.value=false
                        var newUser: LightUser = userViewModel.user.value!!
                        newUser.lastName = binding.settingsLastNameInput.text.toString().trim()
                        newUser.firstName = binding.settingsFirstNameInput.text.toString().trim()
                        newUser.phone = binding.settingsPhoneInput.text.toString().trim()
                        context?.let {
                            userViewModel.buttonEnables.value=false
                            LoadingBar.mutableHideLoadingUserSettingsActivity.value=false
                            userViewModel.updateUser(newUser,token,it)
                        }
                    }
                    .setNegativeButton("NO"){_,it->
                    }.show()
            }else{
                if(userViewModel.buttonEnables.value!!)context?.let { ToastObject.makeText(it,"You didn't do any changes.",
                    Toast.LENGTH_LONG) }
            }
        }else{
            if(userViewModel.buttonEnables.value!!) context?.let { ToastObject.makeText(it, TextModelAuth.SOME_INVALID_FIELDS,
                Toast.LENGTH_LONG) }
        }
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used to check if all input filed are valid.
     * @return true if all field are valid else false
     */
    private fun submitFormVerification():Boolean{
        val validFirstName = binding.settingsFirstNameLayout.helperText==null
        val validLastName = binding.settingsLastNameLayout.helperText==null
        val validPhone = binding.settingsPhoneLayout.helperText==null
        return validFirstName&&
                validLastName&&
                validPhone
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used to load all user information into input filed.
     */
    private fun loadUser(){
        binding.settingsFirstNameInput.setText(userViewModel.user.value?.firstName)
        binding.settingsLastNameInput.setText(userViewModel.user.value?.lastName)
        binding.settingsPhoneInput.setText(userViewModel.user.value?.phone)
        binding.settingsEmailInput.setText(userViewModel.user.value?.email)
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used to check validity of all input filed.
     */
    private fun checkAll(){
        if(binding.settingsFirstNameInput.text?.trim()?.isEmpty()==false)binding.settingsFirstNameLayout.helperText = validFirstName()
        if(binding.settingsLastNameInput.text?.trim()?.isEmpty()==false)binding.settingsLastNameLayout.helperText = validLastName()
        if(binding.settingsPhoneInput.text?.trim()?.isEmpty()==false)binding.settingsPhoneLayout.helperText = validPhone()
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used to check if some changes were made.
     * @return true if input filed are not edited so hold still same information's else false
     */
    private fun checkChanges():Boolean{
        val changeFirstName = binding.settingsFirstNameInput.text.toString().trim()==userViewModel.user.value?.firstName
        val changeLastName = binding.settingsLastNameInput.text.toString().trim()==userViewModel.user.value?.lastName
        val changePhone = binding.settingsPhoneInput.text.toString().trim()==userViewModel.user.value?.phone
        return changeFirstName&&
                changeLastName&&
                changePhone
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used to set focus listener on first name input filed.
     */
    private fun focusFirstName(){
        binding.settingsFirstNameInput.setOnFocusChangeListener(){ _, focused ->
            if (!focused) {
                binding.settingsFirstNameLayout.helperText = validFirstName()
            }
        }
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used for first name previous validation.
     * @return string if there is still some invalid part else null.
     */
    private fun validFirstName():String?{
        val firstName = binding.settingsFirstNameInput.text.toString().trim()
        if(firstName.isNullOrEmpty()){
            binding.settingsFirstNameInput.error = TextModelAuth.INCORRECT_FIRST_NAME_TOOLTIP
            return TextModelAuth.INCORRECT_FIRST_NAME
        }
        binding.settingsFirstNameInput.error = null
        return null
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used to set focus listener on last name input filed.
     */
    private fun focusLastName(){
        binding.settingsLastNameInput.setOnFocusChangeListener(){ _, focused ->
            if (!focused) {
                binding.settingsLastNameLayout.helperText = validLastName()
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
        val lastName = binding.settingsLastNameInput.text.toString().trim()
        if(lastName.isNullOrEmpty()){
            binding.settingsLastNameInput.error = TextModelAuth.INCORRECT_LAST_NAME_TOOLTIP
            return TextModelAuth.INCORRECT_LAST_NAME
        }
        binding.settingsLastNameInput.error = null
        return null
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used to set focus listener on phone number input filed.
     */
    private fun focusPhone(){
        binding.settingsPhoneInput.setOnFocusChangeListener(){ _, focused ->
            if (!focused && binding.settingsPhoneInput.text?.trim()?.isNotEmpty()==true) {
                binding.settingsPhoneLayout.helperText = validPhone()
            }else if(!focused && binding.settingsPhoneLayout.helperText!=resources.getString(R.string.required))binding.settingsPhoneLayout.helperText = resources.getString(R.string.required)
        }
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used for phone number validation.
     * @return string if there is still some invalid part else null.
     */
    private fun validPhone():String?{
        val phone = binding.settingsPhoneInput.text.toString().trim()
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