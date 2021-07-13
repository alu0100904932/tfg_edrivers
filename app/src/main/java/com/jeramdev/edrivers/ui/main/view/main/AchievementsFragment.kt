package com.jeramdev.edrivers.ui.main.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestoreException
import com.jeramdev.edrivers.data.repository.UsersRepository
import com.jeramdev.edrivers.databinding.FragmentAchievementsBinding
import com.jeramdev.edrivers.ui.main.adapter.AchievementsAdapter
import com.jeramdev.edrivers.utils.UserPreferencesHelper
import com.jeramdev.edrivers.utils.showErrorAlert
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AchievementsFragment : Fragment() {
    private var _binding: FragmentAchievementsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAchievementsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.achievementsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.achievementsRecyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userEmail = UserPreferencesHelper(activity as MainActivity).getEmailPref()
                if (userEmail != null) {
                    val achievementsList = UsersRepository().getUserAchievements(userEmail)
                    withContext(Dispatchers.Main) {
                        binding.achievementsRecyclerView.adapter = AchievementsAdapter(achievementsList, this@AchievementsFragment.context)
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