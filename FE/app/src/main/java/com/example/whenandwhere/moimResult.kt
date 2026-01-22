package com.example.whenandwhere

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit

class moimResult : AppCompatActivity() {
    private lateinit var moimTitle : TextView
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
    private lateinit var whenText : TextView
    private lateinit var whereText : TextView


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_moim_result)

        moimTitle = findViewById(R.id.moim)
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
        whenText = findViewById(R.id.`when`)
        whereText = findViewById(R.id.where)

        val jwt = HttpUtil().getJWTFromSharedPreference(this) ?: ""
        val client = HttpUtil().createClient(jwt)
        val retrofit = HttpUtil().createRetrofitWithHeader(client)
        val groupId = HttpUtil().getCurrentGroupIdFromSharedPreference(this)
        moimTitle.text = HttpUtil().getCurrentGroupNameFromSharedPreference(this)

        lifecycleScope.launch {
            val recommendPlace = getResult(retrofit, groupId)
            Log.d("AI_TEST" , "${recommendPlace.drinkTitle}")
            restTitle.text = recommendPlace.restTitle
            restAddress.text = recommendPlace.restAddress
            restPhone.text = recommendPlace.restPhone
            restHash.text = recommendPlace.restHash
            cafeTitle.text = recommendPlace.cafeTitle
            cafeAddress.text = recommendPlace.cafeAddress
            cafePhone.text = recommendPlace.cafePhone
            cafeHash.text = recommendPlace.cafeHash
            drinkTitle.text = recommendPlace.drinkTitle
            drinkAddress.text = recommendPlace.drinkAddress
            drinkPhone.text = recommendPlace.drinkPhone
            drinkHash.text = recommendPlace.drinkHash
            whenText.text = recommendPlace.startTime
            whereText.text = recommendPlace.resultAddress
        }

        val backbtn = findViewById<ImageView>(R.id.arrowleft)
        backbtn.setOnClickListener {
            val intent = Intent(this, Grouphome::class.java)
            startActivity(intent)
        }
    }
    private suspend fun getResult(retrofit: Retrofit, groupId :Int) : RecommendResult{
        return withContext(Dispatchers.IO) {
            val apiService = retrofit.create(ApiService::class.java)
            val call = apiService.getRecommend(groupId)

            val response = call.execute()
            if (response.isSuccessful) {
                val responseData = response.body()
                // 응답 데이터 로그
                responseData?.let {
                    Log.d("ApiTest", "AI_RESULT: ${it}")
                    if(it.data is RecommendResult){
                        return@withContext it.data
                    }
                }
                // 예: responseData를 TextView에 설정하거나, 다른 작업을 수행할 수 있습니다.
            } else {
                // 요청 실패 처리
                Log.d("ERRR", "실패")
            }
            return@withContext RecommendResult()
        }
    }
}