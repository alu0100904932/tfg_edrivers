package com.jeramdev.edrivers.ui.main.view.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestoreException
import com.jeramdev.edrivers.data.repository.DrivingLessonsRepository
import com.jeramdev.edrivers.data.repository.UsersRepository
import com.jeramdev.edrivers.databinding.FragmentHomeBinding
import com.jeramdev.edrivers.ui.main.view.lesson.LessonInfoFragment
import com.jeramdev.edrivers.ui.main.view.lesson.NewLessonFragment
import com.jeramdev.edrivers.utils.Constants
import com.jeramdev.edrivers.utils.UserPreferencesHelper
import com.jeramdev.edrivers.utils.showErrorAlert
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mostrar puntos de los estudiantes
        binding.pointsLayout.isVisible = false
        if (UserPreferencesHelper(activity as MainActivity).getRolPref() == Constants.STUDENT_ROL) {
            binding.pointsLayout.isVisible = true
            showPoints()
        }

        // Botón ver clasificación
        binding.rankButton.setOnClickListener { showRank() }

        // Botón nueva práctica
        binding.newLessonButton.setOnClickListener { newLesson() }

        // Botón última práctica
        binding.lastLessonButton.setOnClickListener { lastLesson() }

    }

    @SuppressLint("SetTextI18n")
    private fun showPoints() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val email = UserPreferencesHelper(activity as MainActivity).getEmailPref()
                var totalPoints = 0
                if (email != null) {
                    totalPoints = UsersRepository().getTotalPoints(email) ?: 0
                }
                withContext(Dispatchers.Main) {
                    binding.totalPointsTextView.text = "Puntos: $totalPoints"
                }
            } catch (e: FirebaseFirestoreException) {
                (activity as MainActivity).runOnUiThread {
                    showErrorAlert(
                        e.message.toString(),
                        activity as MainActivity
                    )
                }
            }
        }
    }

    private fun showRank() {
        val mainActivity = activity as MainActivity
        mainActivity.makeCurrentFragment(RankFragment())
        mainActivity.menuUnselectAll()
    }

    private fun newLesson() {
        val mainActivity = activity as MainActivity
        mainActivity.makeCurrentFragment(NewLessonFragment())
        mainActivity.menuUnselectAll()
    }

    private fun lastLesson() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val rol = UserPreferencesHelper(activity as MainActivity).getRolPref()
                if (rol != null) {
                    when (rol) {
                        Constants.DRIVING_INSTRUCTOR_ROL -> {
                            val drivingSchool =
                                UserPreferencesHelper(activity as MainActivity).getDrivingSchoolPref()
                            if (drivingSchool != null) {
                                val drivingLesson =
                                    DrivingLessonsRepository().getLastDrivingLessonByDrivingSchool(
                                        drivingSchool
                                    )
                                if (drivingLesson != null) {
                                    withContext(Dispatchers.Main) {
                                        val mainActivity = activity as MainActivity
                                        mainActivity.makeCurrentFragment(
                                            LessonInfoFragment(
                                                drivingLesson.id
                                            )
                                        )
                                        mainActivity.menuUnselectAll()
                                    }
                                } else {
                                    (activity as MainActivity).runOnUiThread {
                                        showErrorAlert(
                                            "No se han encontrado prácticas",
                                            activity as MainActivity
                                        )
                                    }
                                }
                            }
                        }
                        Constants.STUDENT_ROL -> {
                            val userEmail =
                                UserPreferencesHelper(activity as MainActivity).getEmailPref()
                            if (userEmail != null) {
                                val drivingLesson =
                                    DrivingLessonsRepository().getLastDrivingLessonByStudent(
                                        userEmail
                                    )
                                if (drivingLesson != null) {
                                    withContext(Dispatchers.Main) {
                                        val mainActivity = activity as MainActivity
                                        mainActivity.makeCurrentFragment(
                                            LessonInfoFragment(
                                                drivingLesson.id
                                            )
                                        )
                                        mainActivity.menuUnselectAll()
                                    }
                                } else {
                                    (activity as MainActivity).runOnUiThread {
                                        showErrorAlert(
                                            "No se han encontrado prácticas.",
                                            activity as MainActivity
                                        )
                                    }
                                }
                            }
                        }
                        else -> {
                            (activity as MainActivity).runOnUiThread {
                                showErrorAlert(
                                    "Error. Rol incorrecto o no existente.",
                                    activity as MainActivity
                                )
                            }
                        }
                    }
                }
            } catch (e: FirebaseFirestoreException) {
                (activity as MainActivity).runOnUiThread {
                    showErrorAlert(
                        e.message.toString(),
                        activity as MainActivity
                    )
                }
            }
        }
    }
}