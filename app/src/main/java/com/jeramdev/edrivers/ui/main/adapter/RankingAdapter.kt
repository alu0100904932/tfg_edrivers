package com.jeramdev.edrivers.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jeramdev.edrivers.R
import com.jeramdev.edrivers.data.model.UserModel
import com.jeramdev.edrivers.databinding.ItemRankingBinding
import com.jeramdev.edrivers.ui.base.BaseViewHolder

class RankingAdapter(
    private val usersList: List<UserModel>,
) :
// El * permite que un mismo adapter pueda gestionar distintos tipos de vista
    RecyclerView.Adapter<BaseViewHolder<*>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> =
        RankingHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_ranking, parent, false)
        )

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) =
        when (holder) {
            is RankingHolder -> holder.bind(usersList[position], position)
            else -> throw IllegalArgumentException("Error en View Holder")
        }

    override fun getItemCount(): Int = usersList.size

    inner class RankingHolder(itemView: View) : BaseViewHolder<UserModel>(itemView) {

        private val binding = ItemRankingBinding.bind(itemView)

        override fun bind(item: UserModel, position: Int) {
            binding.position.text = (position + 1).toString()
            binding.username.text = item.username
            binding.userPoints.text = item.totalPoints.toString()
        }
    }
}