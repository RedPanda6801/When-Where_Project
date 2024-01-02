package com.example.whenandwhere

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter

class GroupAdapter(val Grouplist: ArrayList<Groups>) : RecyclerView.Adapter<GroupAdapter.CustomViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int ): GroupAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
        return CustomViewHolder(view)


    }

    override fun onBindViewHolder(holder: GroupAdapter.CustomViewHolder, position: Int) {
        holder.Groupimage.setImageResource(Grouplist.get(position).Groupimage)
        holder.Groupname.text =  Grouplist.get(position).Groupname

        holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView?.context,MainActivity::class.java)
            ContextCompat.startActivity(holder.itemView.context,intent,null)
        }
    }

    override fun getItemCount(): Int {
        return Grouplist.size
    }
    class CustomViewHolder(itemview: View) :RecyclerView.ViewHolder(itemview) {
        val Groupname = itemview.findViewById<TextView> (R.id.groupname)
        val Groupimage = itemview.findViewById<ImageView>(R.id.groupimage)

    }
}