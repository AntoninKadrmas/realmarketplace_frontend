package com.realmarketplace.model.text

class TextModelAuth {
    companion object{
        const val DIFFERENT_PASSWORD="Password Is Different"
        const val SAME_PASSWORD="Same as Actual Password."
        const val INCORRECT_PASSWORD="Invalid Password"
        const val INCORRECT_PASSWORD_TOOLTIP="Password has to be at minimum 8 characters long. Has to contains lower letter, uppercase letter, special character (not ':') and number."

        const val INCORRECT_PHONE_NUMBER="Invalid phone number"
        const val INCORRECT_PHONE_NUMBER_TOOLTIP="Phone number has to contains exactly nine numbers."

        const val INCORRECT_EMAIL = "Invalid email address"
        const val INCORRECT_EMAIL_TOOLTIP="Email address has to be in the format: sometext@sometex.sometext"

        const val INCORRECT_LAST_NAME="Invalid last name"
        const val INCORRECT_LAST_NAME_TOOLTIP="Last name can't be empty."

        const val INCORRECT_FIRST_NAME="Invalid first name"
        const val INCORRECT_FIRST_NAME_TOOLTIP="First name can't be empty."

        const val SOME_INVALID_FIELDS="Some of the input fields are still invalid!"
    }
}