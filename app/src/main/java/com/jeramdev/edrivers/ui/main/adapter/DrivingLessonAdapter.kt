package com.jeramdev.edrivers.ui.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jeramdev.edrivers.R
import com.jeramdev.edrivers.data.model.DrivingLessonModel
import com.jeramdev.edrivers.databinding.ItemDrivingLessonBinding
import com.jeramdev.edrivers.ui.base.BaseViewHolder
import com.jeramdev.edrivers.utils.getDateFromMillis
import com.jeramdev.edrivers.utils.getTimeFromMillis

class DrivingLessonAdapter(
    private val drivingLessonsList: List<DrivingLessonModel>,
    private val itemClickListener: OnDrivingLessonClickListener
) :
// El * permite que un mismo adapter pueda gestionar distintos tipos de vista
    RecyclerView.Adapter<BaseViewHolder<*>>() {

    // Implemento una interfaz para trabajar con la lógica del onClick en la vista y no en el adapter
    interface OnDrivingLessonClickListener {
        fun onItemClick(drivingLessonId: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> =
        DrivingLessonHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_driving_lesson, parent, false)
        )

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) =
        when (holder) {
            is DrivingLessonHolder -> holder.bind(drivingLessonsList[position], position)
            else -> throw IllegalArgumentException("Error en View Holder")
        }

    override fun getItemCount(): Int = drivingLessonsList.size

    inner class DrivingLessonHolder(itemView: View) : BaseViewHolder<DrivingLessonModel>(itemView) {

        private val binding = ItemDrivingLessonBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        override fun bind(item: DrivingLessonModel, position: Int) {
            binding.lessonText.text = "Práctica:"
            binding.lessonDateText.text = getDateFromMillis(item.dateTimeInMillis)
            binding.lessonHoursText.text = getTimeFromMillis(item.dateTimeInMillis)
            itemView.setOnClickListener { itemClickListener.onItemClick(item.id) }
        }
    }
}