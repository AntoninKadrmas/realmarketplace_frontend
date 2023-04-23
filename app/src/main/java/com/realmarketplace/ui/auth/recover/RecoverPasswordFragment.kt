package com.realmarketplace.ui.auth.recover

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.realmarketplace.R
import com.realmarketplace.databinding.FragmentRecoverPasswordBinding
import com.realmarketplace.model.text.TextModelAuth
import com.realmarketplace.ui.auth.AuthViewModel
import com.realmarketplace.viewModel.LoadingBar
import com.realmarketplace.viewModel.ToastObject

class RecoverPasswordFragment : Fragment() {
    private var _binding: FragmentRecoverPasswordBinding? = null
    private val binding get() = _binding!!
    var buttonEnables = true
    companion object {
        fun newInstance() = RecoverPasswordFragment()
    }
    override fun onResume() {
        super.onResume()
        checkAll()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecoverPasswordBinding.inflate(inflater, container, false)
        binding.recoverButton.setOnClickListener(){
            binding.emailInput.clearFocus()
            submitForm()
            checkAll()
        }
        AuthViewModel.buttonsEnabled.observe(viewLifecycleOwner,Observer{
            buttonEnables=true
        })
        if(binding.emailInput.error !=null)binding.emailLayout.helperText = validEmail()
        focusEmail()
        return binding.root
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used to check validity of all input filed.
     */
    private fun checkAll(){
        if(binding.emailInput.text?.trim()?.isEmpty() == false)binding.emailLayout.helperText = validEmail()
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used for handle login onclick button and if everything is valid then send login request.
     */
    private fun submitForm(){
        val validIdNumber = binding.emailLayout.helperText==null
        if(validIdNumber&&buttonEnables){
            buttonEnables=false
            LoadingBar.mutableHideLoadingAuthActivity.value=false
            context?.let { AuthViewModel.resetPassword(binding.emailInput.text.toString(), it) }
        }else{
            context?.let { ToastObject.makeText(it, TextModelAuth.SOME_INVALID_FIELDS, Toast.LENGTH_LONG) }
        }
    }
    /**
     * A group of *fragment_function*.
     *
     * Function used to set focus listener on email input filed.
     */
    private fun focusEmail(){
        binding.emailInput.setOnFocusChangeListener(){ _, focused ->
            if (!focused && binding.emailInput.text?.isNotEmpty()==true) {
                binding.emailLayout.helperText = validEmail()
            }else if(!focused && binding.emailLayout.helperText!=resources.getString(R.string.required))binding.emailLayout.helperText = resources.getString(
                R.string.required)
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
}