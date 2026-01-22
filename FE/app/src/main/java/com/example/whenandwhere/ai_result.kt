package com.example.whenandwhere

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whenandwhere.databinding.ActivityAiResultBinding
import com.example.whenandwhere.databinding.ActivityGrouplistBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class ai_result : AppCompatActivity() {
    private lateinit var binding: ActivityAiResultBinding
    private lateinit var restTitle : TextView
    private lateinit var restAddress : TextView
    private lateinit var restPhone : TextView
    private lateinit var restHash : TextView
    private lateinit var cafeTitle : TextView
    private lateinit var cafeAddress : TextView
    private lateinit var cafePhone : TextView
    private lateinit var cafeHash : TextView
    private lateinit var drinkTitle : TextView
    private lateinit var drinkAddress : TextView
    private lateinit var drinkPhone : TextView
    private lateinit var drinkHash : TextView
    private lateinit var recommendResult: RecommendResult


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAiResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 값 초기화
        restTitle = findViewById(R.id.restaurantname)
        restAddress = findViewById(R.id.restaddress)
        restPhone = findViewById(R.id.restphone)
        restHash = findViewById(R.id.resthash)
        cafeTitle = findViewById(R.id.cafeplace)
        cafeAddress = findViewById(R.id.cafeaddress)
        cafePhone = findViewById(R.id.cafephone)
        cafeHash = findViewById(R.id.cafehash)
        drinkTitle = findViewById(R.id.drinkname)
        drinkAddress = findViewById(R.id.drinkaddress)
        drinkPhone = findViewById(R.id.drinkphone)
        drinkHash = findViewById(R.id.drinkhash)
        recommendResult = RecommendResult()

        // Retrofit 객체 생성
        val jwt = HttpUtil().getJWTFromSharedPreference(this) ?: ""
        val client = HttpUtil().createClient(jwt)
        val aiRetrofit = HttpUtil().createAIRetrofitWithHeader(client)
        val groupId = HttpUtil().getCurrentGroupIdFromSharedPreference(this)
        // 전달된 값을 받아옵니다
        var count = intent.getIntExtra("count", 1)
        val startDate = intent.getStringExtra("SELECTED_DATE")
        val endDate = intent.getStringExtra("endDate")
        val booleanValue = intent.getBooleanExtra("booleanValue", false)//음주여부
        val selectedPlace = intent.getStringExtra("SELECTED_PLACE") ?: "" //선택된 중간 장소

        // 로그로 전달된 값을 확인합니다
        Log.d("ai_result", "Boolean 값: $booleanValue")
        Log.d("ai_result", "선택된 장소: $selectedPlace")


        val back: ImageView = findViewById(R.id.arrowleft)
        lifecycleScope.launch {
            val recommendPlace = recommendFunc(aiRetrofit, count, selectedPlace, booleanValue)
            Log.d("AI_TEST" , "${recommendPlace.cafeObj?.name}")
            restTitle.text = recommendPlace.restaurantObj?.name
            recommendResult.restTitle = recommendPlace.restaurantObj?.name
            restAddress.text = recommendPlace.restaurantObj?.address
            recommendResult.restAddress = recommendPlace.restaurantObj?.address
            restPhone.text = recommendPlace.restaurantObj?.telephone
            recommendResult.restPhone = recommendPlace.restaurantObj?.telephone
            restHash.text = recommendPlace.restaurantObj?.keyword
            recommendResult.restHash = recommendPlace.restaurantObj?.keyword
            cafeTitle.text = recommendPlace.cafeObj?.name
            recommendResult.cafeTitle = recommendPlace.cafeObj?.name
            cafeAddress.text = recommendPlace.cafeObj?.address
            recommendResult.cafeAddress = recommendPlace.cafeObj?.address
            cafePhone.text = recommendPlace.cafeObj?.telephone
            recommendResult.cafePhone = recommendPlace.cafeObj?.telephone
            cafeHash.text = recommendPlace.cafeObj?.keyword
            recommendResult.cafeHash = recommendPlace.cafeObj?.keyword
            drinkTitle.text = recommendPlace.drinkObj?.name
            recommendResult.drinkTitle = recommendPlace.drinkObj?.name
            drinkAddress.text = recommendPlace.drinkObj?.address
            recommendResult.drinkAddress = recommendPlace.drinkObj?.address
            drinkPhone.text = recommendPlace.drinkObj?.telephone
            recommendResult.drinkPhone = recommendPlace.drinkObj?.telephone
            drinkHash.text = recommendPlace.drinkObj?.keyword
            recommendResult.drinkHash = recommendPlace.drinkObj?.keyword
            recommendResult.groupId = groupId
            recommendResult.startTime = startDate
            recommendResult.endTime = endDate
            recommendResult.resultAddress = selectedPlace
        }

        back.setOnClickListener{
            val intent = Intent(this, middleplace::class.java)
            startActivity(intent)
        }

        val selectBtn : Button = findViewById(R.id.select)
        selectBtn.setOnClickListener{
            val retrofit = HttpUtil().createRetrofitWithHeader(client)
            // 수락 처리
            val apiService = retrofit.create(ApiService::class.java)
            val call = apiService.addRecommend(recommendResult)

            call.enqueue(object : Callback<ObjectDto> {
                override fun onResponse(call: Call<ObjectDto>, response: Response<ObjectDto>) {
                    Log.d("http" , "${response.code()}")
                    if(response.code() == 200){
                        val intent = Intent(this@ai_result,moimResult::class.java)
                        startActivity(intent)
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

        val retryBtn : Button = findViewById(R.id.retry)
        retryBtn.setOnClickListener {
            count++
            Log.d("ai_result","횟수: $count")
            val intent = (this as Activity).intent
            intent.putExtra("count", count)
            this.finish() //현재 액티비티 종료 실시
            this.startActivity(intent) //현재 액티비티 재실행 실시
        }
    }

    private suspend fun recommendFunc(retrofit: Retrofit, count:Int, address : String, isDrink : Boolean) : AIRecommend{
        return withContext(Dispatchers.IO) {
            val apiService = retrofit.create(ApiService::class.java)
            val call = apiService.aiRecommend(count,address, isDrink)

            val response = call.execute()
            if (response.isSuccessful) {
                val responseData = response.body()
                // 응답 데이터 로그
                responseData?.let {
                    Log.d("ApiTest", "AI_RESULT: ${it}")
                    if(it is AIResultDto){
                        return@withContext it.data
                    }
                    /*if (place in it) {
                        val resultList = arrayListOf<MemberClass>()
                        for (member in it.data) {
                            resultList.add(MemberClass(member.id, member.userId, member.nickname))
                        }
                        return@withContext resultList
                    }*/
                }
                // 예: responseData를 TextView에 설정하거나, 다른 작업을 수행할 수 있습니다.
            } else {
                // 요청 실패 처리
                Log.d("ERRR", "실패")
            }
            return@withContext AIRecommend(null, null, null)
        }
    }
}