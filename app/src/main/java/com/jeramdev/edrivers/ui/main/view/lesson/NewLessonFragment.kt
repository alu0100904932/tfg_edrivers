package com.jeramdev.edrivers.ui.main.view.lesson

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jeramdev.edrivers.databinding.FragmentNewLessonBinding
import com.jeramdev.edrivers.ui.main.view.main.MainActivity
import com.jeramdev.edrivers.utils.showErrorAlert

class NewLessonFragment : Fragment() {
    private var _binding: FragmentNewLessonBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewLessonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startNewLessonButton.setOnClickListener { startNewLesson() }
    }

    private fun startNewLesson() {
        val studentEmail = binding.studentEmailEditText.text.toString()
        val drivingInstructorEmail = binding.drivingInstructorEmailEditText.text.toString()
        if (studentEmail.isNotEmpty() &&
            drivingInstructorEmail.isNotEmpty()
        ) {
            val mainActivity = activity as MainActivity
            mainActivity.makeCurrentFragment(LessonMainFragment(studentEmail, drivingInstructorEmail))
            mainActivity.menuUnselectAll()
        } else {
            showErrorAlert(
                "Introduzca todos los datos para continuar",
                activity as MainActivity
            )
        }
    }
}