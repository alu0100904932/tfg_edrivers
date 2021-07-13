package com.jeramdev.edrivers.ui.main.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestoreException
import com.jeramdev.edrivers.data.repository.DrivingLessonsRepository
import com.jeramdev.edrivers.databinding.FragmentLessonsBinding
import com.jeramdev.edrivers.ui.main.adapter.DrivingLessonAdapter
import com.jeramdev.edrivers.ui.main.view.lesson.LessonInfoFragment
import com.jeramdev.edrivers.utils.Constants
import com.jeramdev.edrivers.utils.UserPreferencesHelper
import com.jeramdev.edrivers.utils.showErrorAlert
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LessonsFragment : Fragment(), DrivingLessonAdapter.OnDrivingLessonClickListener {

    private var _binding: FragmentLessonsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLessonsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.lessonsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.lessonsRecyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val rol = UserPreferencesHelper(activity as MainActivity).getRolPref()
                if (rol != null) {
                    when (rol) {
                        Constants.DRIVING_INSTRUCTOR_ROL -> {
                            val drivingSchool =
                                UserPreferencesHelper(activity as MainActivity).getDrivingSchoolPref()
                            if (drivingSchool != null) {
                                val drivingLessonsList =
                                    DrivingLessonsRepository().getDrivingLessonsByDrivingSchool(
                                        drivingSchool
                                    )
                                withContext(Dispatchers.Main) {
                                    binding.lessonsRecyclerView.adapter =
                                        DrivingLessonAdapter(
                                            drivingLessonsList,
                                            this@LessonsFragment
                                        )
                                }
                            }
                        }
                        Constants.STUDENT_ROL -> {
                            val userEmail =
                                UserPreferencesHelper(activity as MainActivity).getEmailPref()
                            if (userEmail != null) {
                                val drivingLessonsList =
                                    DrivingLessonsRepository().getDrivingLessonsByStudent(userEmail)
                                withContext(Dispatchers.Main) {
                                    binding.lessonsRecyclerView.adapter =
                                        DrivingLessonAdapter(
                                            drivingLessonsList,
                                            this@LessonsFragment
                                        )
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

    override fun onItemClick(drivingLessonId: String) {
        val mainActivity = activity as MainActivity
        mainActivity.makeCurrentFragment(LessonInfoFragment(drivingLessonId))
        mainActivity.menuUnselectAll()
    }
}