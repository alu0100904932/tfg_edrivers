package com.jeramdev.edrivers.ui.main.view.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.jeramdev.edrivers.data.model.DrivingSchoolModel
import com.jeramdev.edrivers.databinding.ActivityDrivingSchoolBinding
import com.jeramdev.edrivers.utils.Constants
import com.jeramdev.edrivers.utils.showErrorAlert

class DrivingSchoolActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDrivingSchoolBinding

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrivingSchoolBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Setup
        setup()
    }

    private fun setup() {
        binding.createDrivingSchoolButton.setOnClickListener { createDrivingSchool() }
    }

    /**
     * Crea una autoescuela
     */
    private fun createDrivingSchool() {
        if (validateInputs()) {
            val drivingSchool = DrivingSchoolModel(
                name = binding.drivingSchoolNameEditText.text.toString(),
                email = binding.drivingSchoolEmailEditText.text.toString(),
                phone = binding.drivingSchoolPhoneEditText.text.toString(),
                address = binding.drivingSchoolAddressEditText.text.toString()
            )
            addDrivingSchool(drivingSchool)
            // Volver a signUp
            val signUpIntent = Intent(this, SignUpActivity::class.java)
            startActivity(signUpIntent)
        } else {
            showErrorAlert("Introduzca todos los datos", this)
        }
    }

    /**
     * Añade una nueva autoescuela a la base de datos
     */
    private fun addDrivingSchool(drivingSchool: DrivingSchoolModel) {
        db.collection(Constants.DRIVING_SCHOOL_DB).document(drivingSchool.name).set(
            hashMapOf(
                "name" to drivingSchool.name,
                "email" to drivingSchool.email,
                "phone" to drivingSchool.phone,
                "address" to drivingSchool.address
            )
        )
    }

    /**
     * Compruebo que haya introducido todos los datos
     */
    private fun validateInputs(): Boolean {
        return binding.drivingSchoolNameEditText.text.isNotEmpty() &&
                binding.drivingSchoolEmailEditText.text.isNotEmpty() &&
                binding.drivingSchoolPhoneEditText.text.isNotEmpty() &&
                binding.drivingSchoolAddressEditText.text.isNotEmpty()
    }
}