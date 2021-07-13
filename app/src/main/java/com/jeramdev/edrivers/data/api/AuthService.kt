package com.jeramdev.edrivers.data.api

interface AuthService {

    suspend fun logIn(email: String, password: String)

    suspend fun signUp(email: String, password: String)

    suspend fun deleteAccount()

}