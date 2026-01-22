package com.example.whenandwhere

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


class acceptAdapter (private val context: Context, private val retrofit: Retrofit, private val itemList: List<acceptClass>, private val listener: acceptAdapter.ButtonClickListener) :
    RecyclerView.Adapter<acceptAdapter.ViewHolder>() {


        interface ButtonClickListener {
            fun onButtonClick(position: Int)
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView: TextView = itemView.findViewById(R.id.name)
            val button: Button = itemView.findViewById(R.id.accept)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.accept_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val currentItem = itemList[position]
            holder.textView.text = currentItem.name

            holder.button.setOnClickListener {
                listener.onButtonClick(position)

                // 수락 처리
                val apiService = retrofit.create(ApiService::class.java)
                val call = apiService.processApply(ApplyDto(currentItem.id, null, null, null, null, true))

                call.enqueue(object : Callback<ObjectDto> {
                    override fun onResponse(call: Call<ObjectDto>, response: Response<ObjectDto>) {
                        Log.d("http" , "${response.code()}")
                        if(response.code() == 200){
                            // 페이지 리로딩 및 리스트업
                            val intent = (context as Activity).intent
                            context.finish() //현재 액티비티 종료 실시
                            context.startActivity(intent) //현재 액티비티 재실행 실시
                        }
                    }

                    override fun onFailure(call: Call<ObjectDto>, t: Throwable) {
                        Log.d("ERRR", "에러 이유 : $t")
                        // 네트워크 오류 처리
                    }
                })
            }
        }

        override fun getItemCount() = itemList.size
}