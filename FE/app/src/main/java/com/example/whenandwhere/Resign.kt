package com.example.whenandwhere

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class Resign : AppCompatActivity(), resignAdapter.ButtonClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: resignAdapter
    private var itemList = mutableListOf<resignClass>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resign)

        // Retrofit 객체 생성
        val jwt = HttpUtil().getJWTFromSharedPreference(this) ?: ""
        val client = HttpUtil().createClient(jwt)
        val retrofit = HttpUtil().createRetrofitWithHeader(client)
        val groupId = HttpUtil().getCurrentGroupIdFromSharedPreference(this)

        val backButton = findViewById<ImageView>(R.id.arrowleft)
        backButton.setOnClickListener {
            val intent = Intent(this, GroupSetting_leader::class.java)
            startActivity(intent)
        }

        // RecyclerView 초기화
        recyclerView = findViewById(R.id.memberlist)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 어댑터 초기화
        adapter = resignAdapter(itemList, this)
        recyclerView.adapter = adapter

        // api 실행 및 그룹 리스트 매핑시키기
        lifecycleScope.launch {
            val memberList = getMembers(retrofit, groupId)
            for(member in memberList){
                if(member.id != null && member.nickname != null){
                    itemList.add(resignClass(member.id, "${member.nickname} #${member.userId}"))
                }
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun onButtonClick(position: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.resign_popup, null)
        val alertDialogBuilder = AlertDialog.Builder(this).setView(dialogView)
        val alertDialog = alertDialogBuilder.create()

        val confirmTextView = dialogView.findViewById<TextView>(R.id.confirmTextView)
        confirmTextView.text = "정말 내보내시겠습니까?"

        // Positive 버튼 클릭 리스너 설정
        dialogView.findViewById<Button>(R.id.yesButton).setOnClickListener {
            alertDialog.dismiss()

            // Retrofit 객체 생성
            val jwt = HttpUtil().getJWTFromSharedPreference(this) ?: ""
            val client = HttpUtil().createClient(jwt)
            val retrofit = HttpUtil().createRetrofitWithHeader(client)
            val applyGroupId = HttpUtil().getCurrentGroupIdFromSharedPreference(this)
            // 수락 처리
            val apiService = retrofit.create(ApiService::class.java)
            val call = apiService.emitMember(ApplyDto(itemList[position].id, applyGroupId, null, null, null, null))

            call.enqueue(object : Callback<ObjectDto> {
                override fun onResponse(call: Call<ObjectDto>, response: Response<ObjectDto>) {
                    Log.d("http" , "${response.code()}")
                    if(response.code() == 200){
                        // 페이지 리로딩 및 리스트업
                        val intent = (this as Activity).intent
                        this.finish() //현재 액티비티 종료 실시
                        this.startActivity(intent) //현재 액티비티 재실행 실시
                    }
                    else if(response.code() == 400){
                        // 잘못된 요청에 대한 처리
                    }
                    else{
                        // 다른 예외 처리
                    }
                }

                override fun onFailure(call: Call<ObjectDto>, t: Throwable) {
                    Log.d("ERRR", "에러 이유 : $t")
                    // 네트워크 오류 처리
                }
            })
        }

        // Negative 버튼 클릭 리스너 설정
        dialogView.findViewById<Button>(R.id.noButton).setOnClickListener {
            alertDialog.dismiss()
            // "아니요" 버튼을 클릭할 때 수행할 작업 추가
        }

        // AlertDialog를 화면에 표시
        alertDialog.show()
    }

    private suspend fun getMembers(retrofit: Retrofit, groupId : Int): ArrayList<Members> {
        return withContext(Dispatchers.IO) {
            val apiService = retrofit.create(ApiService::class.java)
            val call = apiService.getGroupMembers(groupId)

            val response = call.execute()
            if (response.isSuccessful) {
                val responseData = response.body()
                // 응답 데이터 로그
                responseData?.let {
                    Log.d("ApiTest", "그룹 멤버: ${it.data}")
                    if (it.data.isNotEmpty()) {
                        val resultList = arrayListOf<Members>()
                        for (member in it.data) {
                            resultList.add(Members(member.id, member.userId, member.nickname))
                        }
                        return@withContext resultList
                    }
                }
                // 예: responseData를 TextView에 설정하거나, 다른 작업을 수행할 수 있습니다.
            } else {
                // 요청 실패 처리
                Log.d("ERRR", "실패")
            }
            return@withContext ArrayList()
        }
    }
}