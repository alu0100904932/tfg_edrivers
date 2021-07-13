package com.jeramdev.edrivers.data.api

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jeramdev.edrivers.utils.Constants
import kotlinx.coroutines.tasks.await

class AuthServiceImpl : AuthService {

    private val auth = Firebase.auth

    override suspend fun logIn(email: String, password: String) {
        try {
            auth.signInWithEmailAndPassword(email, password).await()
        } catch (e: FirebaseAuthException) {
            throw FirebaseAuthException(e.errorCode, "Error al iniciar sesión")
        }
    }

    override suspend fun signUp(email: String, password: String) {
        try {
            auth.createUserWithEmailAndPassword(email, password).await()
        } catch (e: FirebaseAuthUserCollisionException) {
            throw FirebaseAuthUserCollisionException(e.errorCode, "Ya existe una cuenta con el email introducido")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            if (e.errorCode == Constants.ERROR_INVALID_EMAIL) {
                throw FirebaseAuthInvalidCredentialsException(e.errorCode, "El email introducido no es correcto")
            } else {
                throw FirebaseAuthInvalidCredentialsException(e.errorCode, "Error al crear la cuenta")
            }
        } catch (e: FirebaseAuthException) {
            throw FirebaseAuthException(e.errorCode, "Error al crear la cuenta") //"Error al crear la cuenta")
        }
    }

    override suspend fun deleteAccount() {
        try {
            val user = auth.currentUser
            user?.delete()?.await()
        } catch (e: FirebaseAuthException) {
            throw FirebaseAuthException(e.errorCode, "Error al iniciar sesión")
        }
    }
}