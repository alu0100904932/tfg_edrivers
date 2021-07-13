package com.jeramdev.edrivers.data.model

data class UserModel (
    val username: String = "",
    val email: String = "",
    val rol: String = "",
    val drivingSchool: String = "",
    val totalPoints: Int = 0,
    val rankVisibility: Boolean = true,
    val notifications: Boolean = true
)