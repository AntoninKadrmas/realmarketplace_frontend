package com.realmarketplace.model

/**
 * A group of *models*.
 *
 * Model for User.
 *
 * @param createdIn time when the user was created
 * @param email user email address
 * @param firstName user first name
 * @param lastName user last name
 * @param password user password
 * @param phone user phone number
 * @param validated object that said if someone validate himself or not (viz Validated)
 * @param mainImageUrl the main image url that would be displayed when someone look at your profile
 */
data class UserModel(
    val createdIn: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val phone: String,
    val validated: Validated,
    val mainImageUrl: String = ""
)
/**
 * A group of *models*.
 *
 * Model for User Login.
 *
 * @param email user email address
 * @param password user password
 */
data class UserModelLogin(
    val email: String,
    val password: String,
)
/**
 * A group of *models*.
 *
 * Model for Authentication User Token.
 *
 * @param token validation token user use to verify himself
 */
data class UserTokenAuth(
    val token:String
):java.io.Serializable
/**
 * A group of *models*.
 *
 * Model for public (Light) User.
 *
 * @param createdIn time when the user was created
 * @param email user email address
 * @param firstName user first name
 * @param lastName user last name
 * @param phone user phone number
 * @param validated object that said if someone validate himself or not (viz Validated)
 * @param mainImageUrl the main image url that would be displayed when someone look at your profile
 */
data class LightUser(
    var createdIn: String,
    var email: String,
    var firstName: String,
    var lastName: String,
    var phone: String,
    var validated: Validated,
    var mainImageUrl: String
):java.io.Serializable
/**
 * A group of *models*.
 *
 * Model for validation of User.
 *
 * @param validID true if user verify himself by ID
 * @param validEmail true if user verify himself by email
 * @param validSMS true if user verify himself by sms
 */
data class Validated(
    val validID:Boolean,
    val validEmail:Boolean,
    val validSMS:Boolean,
):java.io.Serializable