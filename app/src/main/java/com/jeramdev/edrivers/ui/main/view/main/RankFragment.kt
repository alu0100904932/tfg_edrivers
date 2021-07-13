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
import com.jeramdev.edrivers.databinding.FragmentRankBinding
import com.jeramdev.edrivers.ui.main.adapter.RankingAdapter
import com.jeramdev.edrivers.utils.showErrorAlert
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RankFragment : Fragment() {

    private var _binding: FragmentRankBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRankBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.rankRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.rankRecyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val usersList = UsersRepository().getRanking()
                withContext(Dispatchers.Main) {
                    binding.rankRecyclerView.adapter = RankingAdapter(usersList)
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