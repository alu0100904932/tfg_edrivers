package com.jeramdev.edrivers.ui.main.view.main

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.jeramdev.edrivers.R
import com.jeramdev.edrivers.data.repository.DrivingSchoolRepository
import com.jeramdev.edrivers.data.repository.UsersRepository
import com.jeramdev.edrivers.databinding.FragmentProfileBinding
import com.jeramdev.edrivers.ui.main.view.auth.AuthActivity
import com.jeramdev.edrivers.utils.Constants
import com.jeramdev.edrivers.utils.UserPreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup
        setup()

    }

    private fun setup() {

        val rol = UserPreferencesHelper(activity as MainActivity).getRolPref()
        if (rol == Constants.DRIVING_INSTRUCTOR_ROL) {
            binding.rankSwitch.isVisible = false
        }

        initTexts()

        showInfo()

        // Botón cerrar sesión
        binding.signOutButton.setOnClickListener { signOut() }

        // Usuario en ránking
        binding.rankSwitch.setOnCheckedChangeListener { _, isChecked ->
            CoroutineScope(Dispatchers.IO).launch {
                val userEmail = UserPreferencesHelper(activity as MainActivity).getEmailPref()
                if (userEmail != null) {
                    UsersRepository().updateRankVisibility(isChecked, userEmail)
                }
            }
        }

        // Botón eliminar cuenta
        binding.deleteAccountButton.setOnClickListener { deleteAccount() }
    }

    private fun initTexts() {
        binding.usernameTextView.text = getString(R.string.usernameInfoString)
        binding.emailTextView.text = getString(R.string.emailInfoString)
        binding.rolTextView.text = getString(R.string.rolInfoString)
        binding.drivingSchoolTextView.text = getString(R.string.drivingSchoolString)
        binding.drivingSchoolEmailTextView.text = getString(R.string.emailInfoString)
        binding.phoneTextView.text = getString(R.string.phoneInfoString)
        binding.addressTextView.text = getString(R.string.addressInfoString)
    }

    /**
     * Muestra la información del usuario y la autoescuela
     */
    @SuppressLint("SetTextI18n")
    private fun showInfo() {
        val userEmail = UserPreferencesHelper(activity as MainActivity).getUserPrefs().email
        CoroutineScope(Dispatchers.IO).launch {
            val userInfo = UsersRepository().getUser(userEmail)
            if (userInfo != null) {
                val drivingSchoolInfo =
                    DrivingSchoolRepository().getDrivingSchoolByName(userInfo.drivingSchool)
                withContext(Dispatchers.Main) {
                    binding.usernameTextView.text =
                        getString(R.string.usernameInfoString) + " " + userInfo.username
                    binding.emailTextView.text =
                        getString(R.string.emailInfoString) + " " + userInfo.email
                    binding.rolTextView.text =
                        getString(R.string.rolInfoString) + " " + userInfo.rol
                    binding.notificationsSwitch.isChecked =
                        userInfo.notifications
                    binding.rankSwitch.isChecked =
                        userInfo.rankVisibility
                    if (drivingSchoolInfo != null) {
                        binding.drivingSchoolTextView.text =
                            getString(R.string.drivingSchoolString) + " " + drivingSchoolInfo.name
                        binding.drivingSchoolEmailTextView.text =
                            getString(R.string.emailInfoString) + " " + drivingSchoolInfo.email
                        binding.phoneTextView.text =
                            getString(R.string.phoneInfoString) + " " + drivingSchoolInfo.phone
                        binding.addressTextView.text =
                            getString(R.string.addressInfoString) + " " + drivingSchoolInfo.address
                    }
                }
            }
        }
    }

    /**
     * Cerrar sesión
     */
    private fun signOut() {
        UserPreferencesHelper(activity as MainActivity).deleteUserPrefs()
        val authActivity =
            Intent((activity as MainActivity).applicationContext, AuthActivity::class.java)
        (activity as MainActivity).startActivity(authActivity)
    }

    /**
     * Borrar cuenta de usuario
     */
    private fun deleteAccount() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity as MainActivity)
        builder.setTitle("Confirmar")
        builder.setMessage("¿Desea eliminar su cuenta?")
        builder.setPositiveButton("Si, Eliminar") { _, _ ->
            CoroutineScope(Dispatchers.IO).launch {
                val userEmail = UserPreferencesHelper(activity as MainActivity).getEmailPref()
                if (userEmail != null) {
                    UsersRepository().deleteAccount(userEmail = userEmail)
                    signOut()
                }
            }
        }
        builder.setNegativeButton("Cancelar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}