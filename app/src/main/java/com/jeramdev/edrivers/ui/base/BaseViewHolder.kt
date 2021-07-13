package com.jeramdev.edrivers.ui.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.jeramdev.edrivers.data.model.AchievementModel

/**
 * Clase base para los View Holder de la aplicación
 */
abstract class BaseViewHolder<T>(itemView: View): RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: T, position: Int)
}