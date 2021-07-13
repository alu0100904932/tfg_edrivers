package com.jeramdev.edrivers.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jeramdev.edrivers.R
import com.jeramdev.edrivers.data.model.AchievementLevelModel
import com.jeramdev.edrivers.data.model.AchievementModel
import com.jeramdev.edrivers.databinding.ItemAchievementBinding
import com.jeramdev.edrivers.ui.base.BaseViewHolder

class AchievementsAdapter(
    private val achievementsList: List<AchievementModel>,
    private val context: Context?
) :
// El * permite que un mismo adapter pueda gestionar distintos tipos de vista
    RecyclerView.Adapter<BaseViewHolder<*>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> =
        AchievementsHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_achievement, parent, false)
        )

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) =
        when (holder) {
            is AchievementsHolder -> holder.bind(achievementsList[position], position)
            else -> throw IllegalArgumentException("Error en View Holder")
        }

    override fun getItemCount(): Int = achievementsList.size

    inner class AchievementsHolder(itemView: View) : BaseViewHolder<AchievementModel>(itemView) {

        private val binding = ItemAchievementBinding.bind(itemView)

        override fun bind(item: AchievementModel, position: Int) {
            binding.achievementTitle.text = item.title
            setupLevelsRecyclerView(item)
        }

        private fun setupLevelsRecyclerView(item: AchievementModel) {
            binding.achievementLevelsRecyclerView.layoutManager = LinearLayoutManager(context)
            val achievementsLevelsList = mutableListOf<AchievementLevelModel>()
            item.levels.toSortedMap().forEach { level ->
                achievementsLevelsList.add(level.value)
            }
            binding.achievementLevelsRecyclerView.adapter =
                AchievementsLevelsAdapter(achievementsLevelsList)
        }
    }
}