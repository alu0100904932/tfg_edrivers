package com.jeramdev.edrivers.data.model

data class UserPrefsModel(
    val email: String = "",
    val rol: String = "",
    val drivingSchoolName: String = "",
    val stayLoggedIn: Boolean = false
)
