package com.example.omega1.ui.create

import com.example.omega1.R
import com.example.omega1.databinding.FragmentCreateBinding

class CreateVerification(val insert_binding: FragmentCreateBinding,val old_resources: android.content.res.Resources) {
    var binding: FragmentCreateBinding
    var resources: android.content.res.Resources
    init {
        binding = insert_binding
        resources=old_resources
    }

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

    fun validAdvertName(): String? {
        val advertName = binding.editAdvertNameInput.text.toString()
        val checkAdvertName = advertName.length <= 50
        if (!checkAdvertName) {
            binding.editAdvertNameInput.error = "Limit of advert name is 50 characters."
            return "Invalid advert name"
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
                "Limit of advert description is 2000 characters."
            return "Invalid advert description"
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
}