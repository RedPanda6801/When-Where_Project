package com.example.whenandwhere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class nickname : AppCompatActivity() {
    private lateinit var nicnameEditText: EditText

    // Retrofit 객체 생성
    private val retrofit = HttpUtil().createRetrofit()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nickname)

        val finishbtn : Button = findViewById(R.id.finish)
        nicnameEditText = findViewById(R.id.inputname)

        // 이전 페이지에서 받은 email 가져오기
        val loginIntent = intent
        val email = loginIntent.getStringExtra("email").toString()

        finishbtn.setOnClickListener {
            // 입력된 유저 닉네임과 이전에 받은 유저 이메일을 API에 넣어 요청
            val inputNickname = nicnameEditText.text.toString()
            // 회원 등록 API 실행
            Log.d("EDIT_INPUT", "$inputNickname")
            // api 요청
            val apiService = retrofit.create(ApiService::class.java)
            val call = apiService.signUp(UserDto(1, email, inputNickname))

            call.enqueue(object : Callback<ObjectDto> {
                override fun onResponse(call: Call<ObjectDto>, response: Response<ObjectDto>) {
                    if (response.isSuccessful) {
                        val responseData = response.body()
                        // 응답 데이터 로그
                        responseData?.let {
                            Log.d("ApiTest", "유저는: ${it.data}")
                            if(it.data is LoginDto){
                                val loginDto: LoginDto = it.data as LoginDto
                                Log.d("Token", "토큰: ${loginDto.accessToken}")
                                HttpUtil().saveJWTToSharedPreference(this@nickname, loginDto.accessToken, loginDto.data.email)
                                val intent = Intent(this@nickname, GrouplistActivity::class.java)
                                startActivity(intent)
                            }
                            else{
                                Log.d("Token", "Input ERROR")
                            }
                        }
                        // 예: responseData를 TextView에 설정하거나, 다른 작업을 수행할 수 있습니다.
                    } else {
                        // 요청 실패 처리
                        Log.d("ERRR", "실패")
                    }
                }

                override fun onFailure(call: Call<ObjectDto>, t: Throwable) {
                    Log.d("ERRR", "에러 이유 : $t")
                    // 네트워크 오류 처리
                }
            })


            val intent = Intent(this, GrouplistActivity::class.java)
            startActivity(intent)
        }
    }
}