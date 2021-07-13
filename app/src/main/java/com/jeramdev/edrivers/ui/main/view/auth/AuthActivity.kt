package com.jeramdev.edrivers.ui.main.view.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.jeramdev.edrivers.data.model.UserPrefsModel
import com.jeramdev.edrivers.data.repository.UsersRepository
import com.jeramdev.edrivers.databinding.ActivityAuthBinding
import com.jeramdev.edrivers.ui.main.view.main.MainActivity
import com.jeramdev.edrivers.utils.UserPreferencesHelper
import com.jeramdev.edrivers.utils.showErrorAlert
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        checkLogIn()
        setup()
    }

    /**
     * Si la sesión está iniciada va directamente a la pantalla de inicio
     */
    private fun checkLogIn() {
        val userPrefs = UserPreferencesHelper(this).getUserPrefs()
        if (userPrefs.stayLoggedIn) {
            val homeIntent = Intent(this, MainActivity::class.java)
            startActivity(homeIntent)
        }
    }

    /**
     * Setup de la pantalla de inicio de sesión
     */
    private fun setup() {
        binding.logInButton.setOnClickListener { login() }

        binding.signUpButton.setOnClickListener { signUp() }
    }

    /**
     * Iniciar sesión
     */
    private fun login() {
        val email = binding.emailLoginEditText.text.toString()
        val password = binding.passwordLoginEditText.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    UsersRepository().logIn(email, password)
                    withContext(Dispatchers.Main) {
                        savePrefs()
                    }
                } catch (e: FirebaseAuthException) {
                    runOnUiThread {
                        showErrorAlert(e.message.toString(), this@AuthActivity)
                    }
                }
            }
        } else {
            showErrorAlert("Introduzca todos los datos", this)
        }
    }

    /**
     * Ir a la pantalla de registro
     */
    private fun signUp() {
        val createAccountIntent = Intent(this, SignUpActivity::class.java)
        startActivity(createAccountIntent)
    }

    /**
     * Guarda las preferencias del usuario
     */
    private suspend fun savePrefs() {
        withContext(Dispatchers.IO) {
            val email = binding.emailLoginEditText.text.toString()
            val stayLoggedIn = binding.saveDataCheckBox.isChecked
            try {
                val user = UsersRepository().getUser(email)
                withContext(Dispatchers.Main) {
                    if (user != null) {
                        val userPrefs = UserPrefsModel(
                            email = email,
                            rol = user.rol,
                            drivingSchoolName = user.drivingSchool,
                            stayLoggedIn = stayLoggedIn
                        )
                        UserPreferencesHelper(this@AuthActivity).saveUserPrefs(userPrefs)
                        val homeIntent = Intent(this@AuthActivity, MainActivity::class.java)
                        startActivity(homeIntent)
                    }
                }
            } catch (e: FirebaseFirestoreException) {
                runOnUiThread {
                    showErrorAlert(e.message.toString(), this@AuthActivity)
                }
            }
        }
    }
}