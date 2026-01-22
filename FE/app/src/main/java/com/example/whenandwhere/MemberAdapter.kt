package com.example.whenandwhere

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MemberAdapter(val context: Context, private val memberList: List<MemberClass>)
    : RecyclerView.Adapter<MemberAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.schedule_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val member = memberList[position]
        holder.bind(member)

        // 아이템 클릭 리스너 설정
        holder.itemView.setOnClickListener{
            val intent = Intent(context, ScheduleSetting::class.java).apply{
                putExtra("memberId", member.userId)
                putExtra("memberNickname", member.nickname)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return memberList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.name)

        fun bind(member: MemberClass) {
            nameTextView.text = member.nickname
        }
    }
}