package com.example.whenandwhere

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whenandwhere.databinding.ActivityGrouplistBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit

class GrouplistActivity : AppCompatActivity() {
    private lateinit var nickname: TextView
    private lateinit var binding: ActivityGrouplistBinding
    private val ADD_GROUP_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGrouplistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nickname = findViewById(R.id.nickname)
        nickname.text = HttpUtil().getEmailFromSharedPreference(this)

        // Retrofit 객체 생성
        val jwt = HttpUtil().getJWTFromSharedPreference(this) ?: ""
        val client = HttpUtil().createClient(jwt)
        val retrofit = HttpUtil().createRetrofitWithHeader(client)

        // api 실행 및 그룹 리스트 매핑시키기
        lifecycleScope.launch{
            val groupList = getGroups(retrofit)

            val adapter = GroupAdapter(this@GrouplistActivity, groupList)

            // 클릭 이벤트 처리
            adapter.setOnItemClickListener(object : GroupAdapter.OnItemClickListener {
                override fun onItemClick(group: Groups) {
                    Log.d("test", "${group.Groupname}")
                }
            })

            binding.recyclerView.layoutManager = LinearLayoutManager(this@GrouplistActivity)
            binding.recyclerView.adapter = adapter
        }

        val makeBtn = findViewById<Button>(R.id.makemoim)
        makeBtn.setOnClickListener {
            val intent = Intent(this, NewGroupMake::class.java)
            startActivityForResult(intent, ADD_GROUP_REQUEST)
        }
    }

    private suspend fun getGroups(retrofit: Retrofit) : ArrayList<Groups> {
        return withContext(Dispatchers.IO) {
            val apiService = retrofit.create(ApiService::class.java)
            val call = apiService.getMyGroups()

            val response = call.execute()
            if (response.isSuccessful) {
                val responseData = response.body()
                // 응답 데이터 로그
                responseData?.let {
                    Log.d("ApiTest", "유저는: ${it.data}")
                    val resultList = arrayListOf<Groups>()
                    for(group in it.data){
                        resultList.add(Groups(group.id, group.groupName, group.attribute))
                    }

                    return@withContext resultList
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
