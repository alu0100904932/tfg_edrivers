package com.jeramdev.edrivers.ui.main.view.lesson

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestoreException
import com.jeramdev.edrivers.data.model.DrivingLessonModel
import com.jeramdev.edrivers.data.repository.DrivingLessonsRepository
import com.jeramdev.edrivers.databinding.FragmentLessonInfoBinding
import com.jeramdev.edrivers.ui.main.view.main.MainActivity
import com.jeramdev.edrivers.utils.getDateFromMillis
import com.jeramdev.edrivers.utils.getTimeFromMillis
import com.jeramdev.edrivers.utils.showErrorAlert
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LessonInfoFragment(private val drivingLessonId: String) : Fragment() {
    private var _binding: FragmentLessonInfoBinding? = null
    private val binding get() = _binding!!

    private var drivingLesson = DrivingLessonModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLessonInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showInfo()

        binding.showMap.setOnClickListener { showMap() }
    }

    @SuppressLint("SetTextI18n")
    private fun showInfo() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                drivingLesson = DrivingLessonsRepository().getDrivingLessonById(drivingLessonId)
                    ?: DrivingLessonModel()
                withContext(Dispatchers.Main) {
                    binding.studentTextView.text =
                        "Estudiante: ${drivingLesson.student}"
                    binding.drivingInstructorTextView.text =
                        "Docente: ${drivingLesson.drivingInstructor}"
                    binding.dateTextView.text =
                        "Fecha: ${getDateFromMillis(drivingLesson.dateTimeInMillis)}"
                    binding.timeTextView.text =
                        "Hora: ${getTimeFromMillis(drivingLesson.dateTimeInMillis)}"
                    binding.durationTextView.text =
                        "Duración: ${drivingLesson.duration}"
                    binding.pointsTextView.text =
                        "Puntos: ${drivingLesson.points}"
                    if (drivingLesson.coordinates.isNullOrEmpty())
                        binding.showMap.isVisible = false
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

    private fun showMap() {
        val mainActivity = activity as MainActivity
        mainActivity.makeCurrentFragment(
            MapFragment(
                drivingLesson.coordinates,
                drivingLesson.markers
            )
        )
        mainActivity.menuUnselectAll()
    }
}