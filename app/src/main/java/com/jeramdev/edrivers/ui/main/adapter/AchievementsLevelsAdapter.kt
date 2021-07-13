package com.jeramdev.edrivers.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jeramdev.edrivers.R
import com.jeramdev.edrivers.data.model.AchievementLevelModel
import com.jeramdev.edrivers.databinding.ItemAchievementLevelBinding
import com.jeramdev.edrivers.ui.base.BaseViewHolder

class AchievementsLevelsAdapter(
    private val achievementsLevelsList: List<AchievementLevelModel>,
) :
// El * permite que un mismo adapter pueda gestionar distintos tipos de vista
    RecyclerView.Adapter<BaseViewHolder<*>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> =
        AchievementsLevelsHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_achievement_level, parent, false)
        )

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) =
        when (holder) {
            is AchievementsLevelsHolder -> holder.bind(achievementsLevelsList[position], position)
            else -> throw IllegalArgumentException("Error en View Holder")
        }

    override fun getItemCount(): Int = achievementsLevelsList.size

    inner class AchievementsLevelsHolder(itemView: View) :
        BaseViewHolder<AchievementLevelModel>(itemView) {

        private val binding = ItemAchievementLevelBinding.bind(itemView)

        override fun bind(item: AchievementLevelModel, position: Int) {
            binding.achievementDescription.text = item.description
            binding.achievementImage.setImageResource(R.drawable.ic_star_off)
            if (item.completed) {
                binding.achievementImage.setImageResource(R.drawable.ic_star_on)
            }
        }
    }
}