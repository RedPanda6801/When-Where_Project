package com.example.whenandwhere

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class resignAdapter(
    private val itemList: List<resignClass>,
    private val listener: ButtonClickListener
) :
    RecyclerView.Adapter<resignAdapter.ViewHolder>() {

    interface ButtonClickListener {
        fun onButtonClick(position: Int)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.name)
        val button: Button = itemView.findViewById(R.id.resign)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.resing_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.textView.text = currentItem.text

        holder.button.setOnClickListener {
            listener.onButtonClick(position)
        }
    }

    override fun getItemCount() = itemList.size
}