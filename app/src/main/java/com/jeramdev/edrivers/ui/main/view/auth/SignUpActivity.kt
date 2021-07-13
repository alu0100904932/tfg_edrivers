package com.jeramdev.edrivers.ui.main.view.auth

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.jeramdev.edrivers.R
import com.jeramdev.edrivers.data.model.UserModel
import com.jeramdev.edrivers.data.repository.DrivingSchoolRepository
import com.jeramdev.edrivers.data.repository.UsersRepository
import com.jeramdev.edrivers.databinding.ActivitySignUpBinding
import com.jeramdev.edrivers.utils.Constants
import com.jeramdev.edrivers.utils.achievementsList
import com.jeramdev.edrivers.utils.showErrorAlert
import com.jeramdev.edrivers.utils.validatePassword
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private val drivingSchoolRepository = DrivingSchoolRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setup()
    }

    /**
     * Setup de la pantalla de registro
     */
    private fun setup() {

        // Opciones de la lista de roles
        updateRols()

        // Opciones de la lista de autoescuelas
        updateDrivingSchools()

        // Botón crear cuenta
        binding.registerButton.setOnClickListener { createAccount() }

        // Botón crear nueva autoescuela
        binding.drivingSchoolDropDownList.onItemClickListener = createDrivingSchoolListener()

    }

    /**
     * Crea una cuenta de usuario
     */
    private fun createAccount() {
        if (validateInputs()) {
            val user = UserModel(
                username = binding.userEditText.text.toString(),
                email = binding.emailEditText.text.toString(),
                rol = binding.rolDropDownList.text.toString(),
                drivingSchool = binding.drivingSchoolDropDownList.text.toString(),
            )
            // La contraseña no la guardo en la base de datos sólo la uso para la autenticación
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            if (validatePassword(password)) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val usersRepository = UsersRepository()
                        usersRepository.signUp(email, password)
                        usersRepository.addUser(user)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@SignUpActivity,
                                "Cuenta creada correctamente",
                                Toast.LENGTH_SHORT
                            ).show()
                            val authIntent = Intent(this@SignUpActivity, AuthActivity::class.java)
                            startActivity(authIntent)
                        }
                    } catch (e: FirebaseAuthUserCollisionException) {
                        runOnUiThread {
                            showErrorAlert(e.message.toString(), this@SignUpActivity)
                        }
                    } catch (e: FirebaseAuthException) {
                        runOnUiThread {
                            showErrorAlert(e.message.toString(), this@SignUpActivity)
                        }
                    } catch (e: FirebaseFirestoreException) {
                        runOnUiThread {
                            showErrorAlert(e.message.toString(), this@SignUpActivity)
                        }
                    }
                }
            } else {
                showErrorAlert("La contraseña debe tener al menos 6 caracteres", this)
            }
        } else {
            showErrorAlert("Introduzca todos los datos", this)
        }
    }

    /**
     * Compruebo que haya introducido todos los datos
     */
    private fun validateInputs(): Boolean {
        return binding.userEditText.text.isNotEmpty() &&
                binding.emailEditText.text.isNotEmpty() &&
                binding.passwordEditText.text.isNotEmpty() &&
                binding.rolDropDownList.text.isNotEmpty() &&
                binding.drivingSchoolDropDownList.text.isNotEmpty()
    }

    /**
     * Actualiza la lista de roles
     */
    private fun updateRols() {
        val rolOptions = resources.getStringArray(R.array.rolOptions)
        val rolArrayAdapter = ArrayAdapter(this, R.layout.dropdown_rol, rolOptions)
        binding.rolDropDownList.setAdapter(rolArrayAdapter)
    }

    /**
     * Actualiza la lista de autoescuelas
     */
    private fun updateDrivingSchools() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val drivingSchoolArrayAdapter = ArrayAdapter(
                    this@SignUpActivity,
                    R.layout.dropdown_driving_school,
                    drivingSchoolRepository.getDrivingSchoolsNames()
                )
                // Añado opción para crear nueva
                drivingSchoolArrayAdapter.add(Constants.CREATE_NEW_DRIVING_SCHOOL)
                withContext(Dispatchers.Main) {
                    binding.drivingSchoolDropDownList.setAdapter(drivingSchoolArrayAdapter)
                }
            } catch (e: FirebaseFirestoreException) {
                runOnUiThread {
                    showErrorAlert(e.message.toString(), this@SignUpActivity)
                }
            }
        }
    }

    /**
     * Lógica para cuando se hace click en la opción "Crear nueva" de la lista de autoescuelas
     */
    private fun createDrivingSchoolListener() =
        AdapterView.OnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            if (selectedItem == Constants.CREATE_NEW_DRIVING_SCHOOL) {
                // Elimino el texto y el focus por si vuelve atrás sin crear autoescuela
                binding.drivingSchoolDropDownList.text.clear()
                binding.drivingSchoolDropDownList.clearFocus()
                // Compruebo que tenga el rol de docente
                val rol = binding.rolDropDownList.text.toString()
                if (rol == Constants.DRIVING_INSTRUCTOR_ROL) {
                    // Muestro pantalla para añadir datos de la autoescuela
                    val createDrivingSchoolIntent =
                        Intent(this, DrivingSchoolActivity::class.java)
                    startActivity(createDrivingSchoolIntent)
                } else {
                    showErrorAlert("Debe ser docente para crear una autoescuela", this)
                }
            }
        }
}