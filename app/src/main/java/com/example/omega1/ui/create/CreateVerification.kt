package com.example.omega1.ui.create

import com.example.omega1.R
import com.example.omega1.databinding.FragmentCreateBinding

class CreateVerification(private val insert_binding: FragmentCreateBinding, private val old_resources: android.content.res.Resources) {
    var binding: FragmentCreateBinding = insert_binding
    var resources: android.content.res.Resources = old_resources

    fun focusAdvertName() {
        binding.editAdvertNameInput.setOnFocusChangeListener() { _, focused ->
            if (!focused && binding.editAdvertNameInput.text?.isNotEmpty() == true) {
                binding.editAdvertNameLayout.helperText = validAdvertName()
            } else if (!focused && binding.editAdvertNameLayout.helperText != resources.getString(R.string.required)) binding.editAdvertNameLayout.helperText =
                resources.getString(
                    R.string.required
                )
        }
    }

    fun validAdvertAuthor(): String? {
        val advertName = binding.editAdvertAuthorInput.text.toString()
        val checkAdvertAuthor = advertName.length <= 50
        if (!checkAdvertAuthor) {
            binding.editAdvertAuthorInput.error = "Limit of book author name is 50 characters."
            return "Invalid book author"
        }
        binding.editAdvertAuthorInput.error = null
        return null
    }

    fun focusAdvertAuthor() {
        binding.editAdvertAuthorInput.setOnFocusChangeListener() { _, focused ->
            if (!focused && binding.editAdvertAuthorInput.text?.isNotEmpty() == true) {
                binding.editAdvertAuthorLayout.helperText = validAdvertAuthor()
            } else if (!focused && binding.editAdvertAuthorLayout.helperText != resources.getString(R.string.required)) binding.editAdvertAuthorLayout.helperText =
                resources.getString(
                    R.string.required
                )
        }
    }

    fun validAdvertName(): String? {
        val advertName = binding.editAdvertNameInput.text.toString()
        val checkAdvertName = advertName.length <= 50
        if (!checkAdvertName) {
            binding.editAdvertNameInput.error = "Limit of book name is 50 characters."
            return "Invalid book name"
        }
        binding.editAdvertNameInput.error = null
        return null
    }

    fun focusAdvertDescription() {
        binding.editAdvertDescriptionInput.setOnFocusChangeListener() { _, focused ->
            if (!focused && binding.editAdvertDescriptionInput.text?.isNotEmpty() == true) {
                binding.editAdvertDescriptionLayout.helperText = validAdvertDescription()
            } else if (!focused && binding.editAdvertDescriptionLayout.helperText != resources.getString(
                    R.string.required
                )
            ) binding.editAdvertDescriptionLayout.helperText = resources.getString(
                R.string.required
            )
        }
    }

    fun validAdvertDescription(): String? {
        val advertDescription = binding.editAdvertDescriptionInput.text.toString()
        val checkAdvertDescription = advertDescription.length <= 2000
        if (!checkAdvertDescription) {
            binding.editAdvertDescriptionInput.error =
                "Limit of book description is 2000 characters."
            return "Invalid book description"
        }
        binding.editAdvertDescriptionInput.error = null
        return null
    }

    fun focusPrice() {
        binding.priceInput.setOnFocusChangeListener() { _, focused ->
            if (!focused && binding.priceInput.text?.isNotEmpty() == true) {
                binding.priceLayout.helperText = validPrice()
            } else if (!focused && binding.priceLayout.helperText != resources.getString(R.string.required)) binding.priceLayout.helperText =
                resources.getString(
                    R.string.required
                )
        }
    }

    fun validPrice(): String? {
        val advertPrice = binding.priceInput.text.toString()
        val checkAdvertPrice = advertPrice.length <= 10
        if (!checkAdvertPrice) {
            binding.priceInput.error = "Limit of price is 10 characters."
            return "Invalid price"
        }
        binding.priceInput.error = null
        return null
    }

    fun focusCondition() {
        binding.conditionInput.setOnFocusChangeListener() { _, focused ->
            if (!focused) binding.conditionLayout.helperText = validCondition()
        }
    }

    fun validCondition(): String? {
        if (binding.conditionInput.text?.isNullOrEmpty() == true) return resources.getString(R.string.required)
        else return null
    }

    fun validGenre(): String? {
        if (binding.selectGenreInput.text?.isNullOrEmpty() == true) return resources.getString(R.string.required)
        else return null
    }
    fun checkAll(){
        if(binding.editAdvertNameInput.text?.isEmpty() == false)binding.editAdvertNameLayout.helperText=validAdvertName()
        if(binding.editAdvertAuthorInput.text?.isEmpty() == false)binding.editAdvertAuthorLayout.helperText=validAdvertAuthor()
        if(binding.editAdvertDescriptionInput.text?.isEmpty() == false)binding.editAdvertDescriptionLayout.helperText=validAdvertDescription()
        if(binding.priceInput.text?.isEmpty() == false)binding.priceLayout.helperText=validPrice()
        if(binding.conditionInput.text?.isEmpty() == false)binding.conditionLayout.helperText=validCondition()
        if(binding.selectGenreInput.text?.isEmpty() == false)binding.selectGenreLayout.helperText=validGenre()
    }
}