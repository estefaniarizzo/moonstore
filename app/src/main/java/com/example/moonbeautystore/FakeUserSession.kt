package com.example.moonbeautystore

data class User(
    val name: String,
    val email: String,
    val password: String,
    val phone: String?,
    val skinType: String,
    val hairType: String
)

object FakeUserSession {
    var registeredUser: User? = null
    var loggedInUser: User? = null

    fun isLoggedIn(): Boolean = loggedInUser != null

    fun logout() {
        loggedInUser = null
    }
}
