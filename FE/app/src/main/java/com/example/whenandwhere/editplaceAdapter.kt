package com.example.whenandwhere

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class editplaceAdapter(
    private val items: List<editplaceClass>
) : RecyclerView.Adapter<editplaceAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userNameTextView: TextView = view.findViewById(R.id.username)
        val inputButton: EditText = view.findViewById(R.id.inputbtn)
        val iconButton: ImageButton = view.findViewById(R.id.imageView_trans1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.place_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.userNameTextView.text = item.userName
        holder.inputButton.setText(item.departurePlace)
        holder.iconButton.setImageResource(
            if (item.isCarIcon) R.drawable.car_orange else R.drawable.bus_orange
        )

        holder.inputButton.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                // Save the input value when EditText loses focus
                val newPosition = holder.adapterPosition
                items[newPosition].updateDeparturePlace(holder.inputButton.text.toString())
            }
        }

        holder.iconButton.setOnClickListener {
            item.isCarIcon = !item.isCarIcon
            holder.iconButton.setImageResource(
                if (item.isCarIcon) R.drawable.car_orange else R.drawable.bus_orange
            )
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
