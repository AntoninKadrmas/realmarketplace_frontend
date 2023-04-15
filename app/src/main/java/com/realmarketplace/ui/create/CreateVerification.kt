package com.realmarketplace.ui.create

import androidx.core.text.isDigitsOnly
import com.realmarketplace.R
import com.realmarketplace.databinding.FragmentCreateBinding
import com.realmarketplace.model.AdvertModel

/**
 * A group of *tool*.
 *
 * Class contains functions used in creating request for create or update advert.
 * @param insert_binding binding of create fragment to operate on all elements in layout
 * @param old_resources to use getString function
 */
class CreateVerification(insert_binding: FragmentCreateBinding, old_resources: android.content.res.Resources) {
    var binding: FragmentCreateBinding = insert_binding
    var resources: android.content.res.Resources = old_resources
    /**
     * A group of *tool_function*.
     *
     * Function used to set focus listener on advert author input filed.
     */
    fun focusAdvertAuthor() {
        binding.editAdvertAuthorInput.setOnFocusChangeListener() { _, focused ->
            if (!focused && binding.editAdvertAuthorInput.text?.trim()?.isNotEmpty() == true) {
                binding.editAdvertAuthorLayout.helperText = validAdvertAuthor()
            } else if (!focused && binding.editAdvertAuthorLayout.helperText != resources.getString(R.string.required)) binding.editAdvertAuthorLayout.helperText =
                resources.getString(
                    R.string.required
                )
        }
    }
    /**
     * A group of *tool_function*.
     *
     * Function used for advert author validation.
     * @return string if there is still some invalid part else null.
     */
    fun validAdvertAuthor(): String? {
        val authorName = binding.editAdvertAuthorInput.text.toString().trim()
        val checkAdvertAuthor = authorName.length <= binding.editAdvertAuthorLayout.counterMaxLength && authorName.isNotEmpty()
        if (!checkAdvertAuthor) {
            binding.editAdvertAuthorInput.error = "Limit of book author name is ${binding.editAdvertAuthorLayout.counterMaxLength} characters."
            return "Invalid book author"
        }
        binding.editAdvertAuthorInput.error = null
        return null
    }
    /**
     * A group of *tool_function*.
     *
     * Function used to set focus listener on advert name input filed.
     */
    fun focusAdvertName() {
        binding.editAdvertNameInput.setOnFocusChangeListener() { _, focused ->
            if (!focused && binding.editAdvertNameInput.text?.trim()?.isNotEmpty() == true) {
                binding.editAdvertNameLayout.helperText = validAdvertName()
            } else if (!focused && binding.editAdvertNameLayout.helperText != resources.getString(R.string.required)) binding.editAdvertNameLayout.helperText =
                resources.getString(
                    R.string.required
                )
        }
    }
    /**
     * A group of *tool_function*.
     *
     * Function used for advert name validation.
     * @return string if there is still some invalid part else null.
     */
    fun validAdvertName(): String? {
        val advertName = binding.editAdvertNameInput.text.toString().trim()
        val checkAdvertName = advertName.length <= binding.editAdvertNameLayout.counterMaxLength && advertName.isNotEmpty()
        if (!checkAdvertName) {
            binding.editAdvertNameInput.error = "Limit of book name is ${binding.editAdvertNameLayout.counterMaxLength} characters."
            return "Invalid book name"
        }
        binding.editAdvertNameInput.error = null
        return null
    }
    /**
     * A group of *tool_function*.
     *
     * Function used to set focus listener on advert describe input filed.
     */
    fun focusAdvertDescription() {
        binding.editAdvertDescriptionInput.setOnFocusChangeListener() { _, focused ->
            if (!focused && binding.editAdvertDescriptionInput.text?.trim()?.isNotEmpty() == true) {
                binding.editAdvertDescriptionLayout.helperText = validAdvertDescription()
            } else if (!focused && binding.editAdvertDescriptionLayout.helperText != resources.getString(
                    R.string.required
                )
            ) binding.editAdvertDescriptionLayout.helperText = resources.getString(
                R.string.required
            )
        }
    }
    /**
     * A group of *tool_function*.
     *
     * Function used for advert describe validation.
     * @return string if there is still some invalid part else null.
     */
    fun validAdvertDescription(): String? {
        val advertDescription = binding.editAdvertDescriptionInput.text.toString().trim()
        val checkAdvertDescription = advertDescription.length <= binding.editAdvertDescriptionLayout.counterMaxLength && advertDescription.isNotEmpty()
        if (!checkAdvertDescription) {
            binding.editAdvertDescriptionInput.error =
                "Limit of book description is ${binding.editAdvertDescriptionLayout.counterMaxLength} characters."
            return "Invalid book description"
        }
        binding.editAdvertDescriptionInput.error = null
        return null
    }
    /**
     * A group of *tool_function*.
     *
     * Function used to set focus listener on price option input filed.
     */
    fun focusPrice() {
        binding.priceInput.setOnFocusChangeListener() { _, focused ->
            if (!focused && binding.priceInput.text?.trim()?.isNotEmpty() == true) {
                binding.priceLayout.helperText = validPrice()
            } else if (!focused && binding.priceLayout.helperText != resources.getString(R.string.required)) binding.priceLayout.helperText =
                resources.getString(
                    R.string.required
                )
        }
    }
    /**
     * A group of *tool_function*.
     *
     * Function used for price option validation.
     * @return string if there is still some invalid part else null.
     */
    fun validPrice(): String? {
        val advertPrice = binding.priceInput.text.toString().trim()
        val checkAdvertPrice = advertPrice.length <= binding.priceLayout.counterMaxLength && advertPrice.isNotEmpty()
        val doNotStartWithZero = advertPrice.length>1 && advertPrice[0]=='0'
        if (!checkAdvertPrice) {
            binding.priceInput.error = "Limit of price is ${binding.priceLayout.counterMaxLength} characters."
            return "Invalid price"
        }
        else if(doNotStartWithZero){
            binding.priceInput.error = "Price bigger than one number can't start with 0."
            return "Invalid price"
        }
        binding.priceInput.error = null
        return null
    }
    /**
     * A group of *tool_function*.
     *
     * Function used to set focus listener on condition input filed.
     */
    fun focusCondition() {
        binding.conditionInput.setOnFocusChangeListener() { _, focused ->
            if (!focused) binding.conditionLayout.helperText = validCondition()
        }
    }
    /**
     * A group of *tool_function*.
     *
     * Function used for condition validation.
     * @return string if there is still some invalid part else null.
     */
    fun validCondition(): String? {
        if (binding.conditionInput.text?.trim()?.isNullOrEmpty() == true) return resources.getString(R.string.required)
        else return null
    }
    /**
     * A group of *tool_function*.
     *
     * Function used for genre name validation.
     * @return string if there is still some invalid part else null.
     */
    fun validGenre(): String? {
        if (binding.selectGenreInput.text?.trim()?.isNullOrEmpty() == true) return resources.getString(R.string.required)
        else return null
    }
    /**
     * A group of *tool_function*.
     *
     * Function used to check validity of all input filed.
     */
    fun checkAll(){
        if(binding.editAdvertNameInput.text?.trim()?.isEmpty() == false)binding.editAdvertNameLayout.helperText=validAdvertName()
        if(binding.editAdvertAuthorInput.text?.trim()?.isEmpty() == false)binding.editAdvertAuthorLayout.helperText=validAdvertAuthor()
        if(binding.editAdvertDescriptionInput.text?.trim()?.isEmpty() == false)binding.editAdvertDescriptionLayout.helperText=validAdvertDescription()
        if(binding.priceInput.text?.trim()?.isEmpty() == false)binding.priceLayout.helperText=validPrice()
        if(binding.conditionInput.text?.trim()?.isEmpty() == false)binding.conditionLayout.helperText=validCondition()
        if(binding.selectGenreInput.text?.trim()?.isEmpty() == false)binding.selectGenreLayout.helperText=validGenre()
    }
    /**
     * A group of *tool_function*.
     *
     * Function used to clear all input filed.
     */
    fun clearInputData(){
        binding.priceInput.text=null
        binding.conditionInput.text=null
        binding.selectGenreInput.text=null
        binding.editAdvertAuthorInput.text=null
        binding.editAdvertNameInput.text = null
        binding.editAdvertDescriptionInput.text= null
        checkAll()
    }
    /**
     * A group of *tool_function*.
     *
     * Function used to create new advert used with data from input filed.
     * @return return newly crated advert with data from input filed viz. AdvertModel
     */
    fun createAdvert(): AdvertModel {
        return AdvertModel(
            _id="",
            title = binding.editAdvertNameInput.text.toString().trim(),
            author = binding.editAdvertAuthorInput.text.toString().trim(),
            description = binding.editAdvertDescriptionInput.text.toString().trim(),
            condition = binding.conditionInput.text.toString().trim(),
            price = binding.priceInput.text.toString().trim(),
            priceOption = binding.priceOptionInput.text.toString().trim(),
            genreName = binding.selectGenreInput.text.toString().trim().split("/")[0],
            genreType = binding.selectGenreInput.text.toString().trim().split("/")[1],
            imagesUrls = ArrayList(),
            mainImageUrl="",
            visible = true
        )
    }
    /**
     * A group of *tool_function*.
     *
     * Function used to find some changes in old and new advert.
     * @param oldAdvert old advert viz. AdvertModel
     * @param newAdvert new advert viz. AdvertModel
     * @return true if adverts are same else false
     */
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
    /**
     * A group of *tool_function*.
     *
     * Function used to check if all field are valid.
     * @return true if all field are valid else false
     */
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
    /**
     * A group of *tool_function*.
     *
     * Function used to clear focus from all input filed.
     */
    fun clearFocus(){
        binding.editAdvertNameInput.clearFocus()
        binding.editAdvertDescriptionInput.clearFocus()
        binding.priceInput.clearFocus()
        binding.conditionInput.clearFocus()
        checkAll()
    }
    /**
     * A group of *tool_function*.
     *
     * Function used to handle selection of price option drop down.
     * @param position index which option user selected. If 0 user can input number else filed would be blocked.
     * @param priceOptions list of all price options
     */
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
    /**
     * A group of *tool_function*.
     *
     * Function used to load information from given advert.
     * @param advert advert model with information's that is going to be displayed
     */
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