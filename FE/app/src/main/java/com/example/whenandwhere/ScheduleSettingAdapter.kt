package com.example.whenandwhere

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ScheduleSettingAdapter(
    private val scheduleList: List<ScheduleItem>,
    private val onItemClick: (ScheduleItem) -> Unit // 클릭 리스너를 추가합니다.
) : RecyclerView.Adapter<ScheduleSettingAdapter.ScheduleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.schedule_item, parent, false)
        return ScheduleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val currentItem = scheduleList[position]
        holder.textView.text = currentItem.title
        holder.itemView.setOnClickListener {
            onItemClick(currentItem) // 항목 클릭 시 리스너를 호출합니다.
        }
    }
    override fun getItemCount() = scheduleList.size

    class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.name)
    }
}