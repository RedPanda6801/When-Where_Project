package com.example.whenandwhere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit

class Accept : AppCompatActivity(), acceptAdapter.ButtonClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: acceptAdapter
    private var itemList = mutableListOf<acceptClass>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accept)

        // http 세팅
        val jwt = HttpUtil().getJWTFromSharedPreference(this) ?: ""
        val client = HttpUtil().createClient(jwt)
        val retrofit = HttpUtil().createRetrofitWithHeader(client)
        val groupId = HttpUtil().getCurrentGroupIdFromSharedPreference(this)

        val backButton = findViewById<ImageView>(R.id.arrowleft)
        backButton.setOnClickListener{
            val intent = Intent(this, GroupSetting_leader::class.java)
            startActivity(intent)
        }

        // RecyclerView 초기화
        recyclerView = findViewById(R.id.acceptlist)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 어댑터 초기화
        adapter = acceptAdapter(this, retrofit, itemList, this)
        recyclerView.adapter = adapter

        // 멤버 리스트 매핑
        lifecycleScope.launch {
            val applyList = getApplies(retrofit, groupId)
            for(apply in applyList){
                if(apply.id != null && apply.applierNickname != null){
                    itemList.add(acceptClass(apply.id, apply.applierNickname))
                }
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun onButtonClick(position: Int) {
        // 버튼 클릭 시 진행될 작업
    }

    private suspend fun getApplies(retrofit: Retrofit, groupId: Int): ArrayList<Applies> {
        return withContext(Dispatchers.IO) {

            val apiService = retrofit.create(ApiService::class.java)
            val call = apiService.getApplies(groupId)

            val response = call.execute()

            if(response.code() == 401){
                // 권한 없음 에러로 알림참을 띄우고 뒤로 보내기
            }
            if (response.isSuccessful) {
                val responseData = response.body()
                // 응답 데이터 로그
                responseData?.let {
                    Log.d("ApiTest", "신청 목록: ${it.data}")
                    if (it.data.isNotEmpty()) {
                        val resultList = arrayListOf<Applies>()
                        for (apply in it.data) {
                            resultList.add(Applies(apply.id, apply.applyGroupId, apply.applierId, apply.applierNickname, apply.state, apply.decide))
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