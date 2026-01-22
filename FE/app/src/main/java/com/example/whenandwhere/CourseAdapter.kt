package com.example.whenandwhere

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CourseAdapter(val Courselist: ArrayList<Courses>) :
    RecyclerView.Adapter<CourseAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CourseAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.course_item, parent, false)
        return CustomViewHolder(view)
    }

    override fun getItemCount(): Int {
        return Courselist.size
    }

    override fun onBindViewHolder(holder: CourseAdapter.CustomViewHolder, position: Int) {
        holder.name1.text = Courselist.get(position).name1
        holder.distance1.text = Courselist.get(position).distance1
        holder.address1.text = Courselist.get(position).address1
        holder.name2.text = Courselist.get(position).name2
        holder.distance2.text = Courselist.get(position).distance2
        holder.address2.text = Courselist.get(position).address2
        holder.name3.text = Courselist.get(position).name3
        holder.distance3.text = Courselist.get(position).distance3
        holder.address3.text = Courselist.get(position).address3
    }

    class CustomViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val name1 = itemview.findViewById<TextView>(R.id.restaurantname)
        val distance1 = itemview.findViewById<TextView>(R.id.restaddress)
        val address1 = itemview.findViewById<TextView>(R.id.restphone)
        val name2 = itemview.findViewById<TextView>(R.id.cafeplace)
        val distance2 = itemview.findViewById<TextView>(R.id.cafeaddress)
        val address2 = itemview.findViewById<TextView>(R.id.cafephone)
        val name3 = itemview.findViewById<TextView>(R.id.drinkname)
        val distance3 = itemview.findViewById<TextView>(R.id.drinkaddress)
        val address3 = itemview.findViewById<TextView>(R.id.drinkphone)

    }
}