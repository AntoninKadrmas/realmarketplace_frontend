package com.realmarketplace.ui.auth.eye

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.realmarketplace.MainActivity
import com.realmarketplace.R
import com.realmarketplace.ui.auth.AuthActivity
import com.realmarketplace.databinding.FragmentEyeFeatureBinding

/**
 * A group of *fragment*.
 *
 * Class for fragment_eye_feature layout and logic there.
 */
class EyeAuthFragment : Fragment() {
    private var _binding: FragmentEyeFeatureBinding? = null
    private val binding get() = _binding!!
    companion object {
        fun newInstance() = EyeAuthFragment()
        const val NAME = "Authentication"
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEyeFeatureBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.loginButton.setOnClickListener(){
            startActivity(Intent(activity, AuthActivity::class.java))
        }
        return root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}