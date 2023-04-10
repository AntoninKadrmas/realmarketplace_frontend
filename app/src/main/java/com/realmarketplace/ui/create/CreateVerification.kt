package com.realmarketplace.ui.create

import androidx.core.text.isDigitsOnly
import com.realmarketplace.R
import com.realmarketplace.databinding.FragmentCreateBinding
import com.realmarketplace.model.AdvertModel

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
        val checkAdvertAuthor = advertName.length <= binding.editAdvertAuthorLayout.counterMaxLength
        if (!checkAdvertAuthor) {
            binding.editAdvertAuthorInput.error = "Limit of book author name is ${binding.editAdvertAuthorLayout.counterMaxLength} characters."
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
        val checkAdvertName = advertName.length <= binding.editAdvertNameLayout.counterMaxLength
        if (!checkAdvertName) {
            binding.editAdvertNameInput.error = "Limit of book name is ${binding.editAdvertNameLayout.counterMaxLength} characters."
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
        val checkAdvertDescription = advertDescription.length <= binding.editAdvertDescriptionLayout.counterMaxLength
        if (!checkAdvertDescription) {
            binding.editAdvertDescriptionInput.error =
                "Limit of book description is ${binding.editAdvertDescriptionLayout.counterMaxLength} characters."
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
        val checkAdvertPrice = advertPrice.length <= binding.priceLayout.counterMaxLength
        if (!checkAdvertPrice) {
            binding.priceInput.error = "Limit of price is ${binding.priceLayout.counterMaxLength} characters."
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
    fun clearInputData(){
        binding.priceInput.text=null
        binding.conditionInput.text=null
        binding.selectGenreInput.text=null
        binding.editAdvertAuthorInput.text=null
        binding.editAdvertNameInput.text = null
        binding.editAdvertDescriptionInput.text= null
        checkAll()
    }
    fun createAdvert(): AdvertModel {
        return AdvertModel(
            _id="",
            title = binding.editAdvertNameInput.text.toString(),
            author = binding.editAdvertAuthorInput.text.toString(),
            description = binding.editAdvertDescriptionInput.text.toString(),
            condition = binding.conditionInput.text.toString(),
            price = binding.priceInput.text.toString(),
            priceOption = binding.priceOptionInput.text.toString(),
            genreName = binding.selectGenreInput.text.toString().split("/")[0],
            genreType = binding.selectGenreInput.text.toString().split("/")[1],
            imagesUrls = ArrayList(),
            mainImageUrl="",
            visible = true
        )
    }
    fun checkOldAndNewAdvertAreSimilar(oldAdvert: AdvertModel, newAdvert: AdvertModel):Boolean{
        return oldAdvert.title==newAdvert.title
                && oldAdvert.description == newAdvert.description
                && oldAdvert.author == newAdvert.author
                && oldAdvert.condition == newAdvert.condition
                && oldAdvert.price == newAdvert.price
                && oldAdvert.priceOption == newAdvert.priceOption
                && oldAdvert.genreName == newAdvert.genreName
                && oldAdvert.genreType == newAdvert.genreType
    }
    fun submitFormVerification():Boolean{
        val validAdvertName = binding.editAdvertNameLayout.helperText==null
        val validAdvertDescription = binding.editAdvertDescriptionLayout.helperText==null
        val validAdvertPrice = binding.priceLayout.helperText==null
        val validAdvertCondition = binding.conditionLayout.helperText==null
        val validAdvertGenre = binding.selectGenreLayout.helperText==null
        val validAdvertAuthor = binding.editAdvertAuthorLayout.helperText==null
        return validAdvertName&&
                validAdvertDescription&&
                validAdvertPrice&&
                validAdvertCondition&&
                validAdvertGenre&&
                validAdvertAuthor
    }
    fun clearFocus(){
        binding.editAdvertNameInput.clearFocus()
        binding.editAdvertDescriptionInput.clearFocus()
        binding.priceInput.clearFocus()
        binding.conditionInput.clearFocus()
        checkAll()
    }
    fun priceOptionHandleResult(position:Int,priceOptions:ArrayList<String>){
        if(position==0){
            binding.priceInput.isEnabled=true
            binding.priceLayout.isCounterEnabled=true
            binding.priceLayout.counterMaxLength = 6
            binding.priceLayout.helperText=resources.getString(R.string.required)
            binding.priceInput.text = null
        }else{
            binding.priceInput.isEnabled=false
            binding.priceLayout.counterMaxLength = priceOptions[position].length
            binding.priceLayout.helperText=null
            binding.priceInput.setText(priceOptions[position])
        }
    }
    fun uploadAdvertDataIntoForm(advert: AdvertModel){
        binding.editAdvertNameInput.setText(advert.title)
        binding.editAdvertAuthorInput.setText(advert.author)
        binding.editAdvertDescriptionInput.setText(advert.description)
        binding.priceInput.setText(advert.price)
        if(binding.priceInput.text?.isDigitsOnly()==false){
            binding.priceLayout.counterMaxLength=advert.price.length
            binding.priceInput.isEnabled=false
        }
        binding.priceOptionInput.setText(advert.priceOption)
        binding.conditionInput.setText(advert.condition)
        binding.selectGenreInput.setText("${advert.genreName}/${advert.genreType}")
    }
}