package com.example.whenandwhere

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class GroupAdapter(private val context: Context, private val groupList: ArrayList<Groups>) :
    RecyclerView.Adapter<GroupAdapter.CustomViewHolder>() {
    // 클릭 이벤트 리스너 정의
    interface OnItemClickListener {
        fun onItemClick(group: Groups)
    }

    // 리스너 변수 선언
    private var listener: OnItemClickListener? = null

    // 리스너 설정 메서드
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val group = groupList[position]
        holder.bind(group)

        // 아이템 클릭 리스너 설정
        holder.itemView.setOnClickListener {
            group.Groupname?.let { groupName ->
                group.Grouptheme?.let { groupTheme ->
                    group.groupId?.let{groupId ->
                        HttpUtil().saveCurrentGroupToSharedPreference(context, groupName, groupTheme, groupId)
                    }
                }
            }
            val intent = Intent(context, Grouphome::class.java).apply {
                putExtra("groupName", group.Groupname)
                putExtra("groupTheme", group.Grouptheme)
                putExtra("groupId", group.groupId)
            }
            Log.d("GroupAdapter", "Selected groupName: ${group.Groupname}, groupTheme: ${group.Grouptheme}")
            context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int {
        return groupList.size
    }

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val groupNameTextView: TextView = itemView.findViewById(R.id.groupname)
        private val groupThemeTextView: TextView = itemView.findViewById(R.id.grouptheme)

        fun bind(group: Groups) {
            groupNameTextView.text = group.Groupname
            groupThemeTextView.text = group.Grouptheme
        }
    }


    fun addGroup(group: Groups) {
        groupList.add(group)
        notifyItemInserted(groupList.size - 1)
    }
}
