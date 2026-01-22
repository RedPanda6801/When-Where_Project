package com.example.whenandwhere

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Nickname
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.whenandwhere.R
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class login : AppCompatActivity() {
//    private lateinit var usernameEditText: EditText
//    private lateinit var passwordEditText: EditText
//    private lateinit var loginButton: Button

    // Retrofit 객체 생성
    val retrofit = HttpUtil().createRetrofit()

    private fun kakaoTokenFunc(token: String) {
        // API 서비스 인스턴스 생성
        val apiService = retrofit.create(ApiService::class.java)

        val call = apiService.oauthCheck(token)
        call.enqueue(object : Callback<LoginDto> {
            override fun onResponse(call: Call<LoginDto>, response: Response<LoginDto>) {
                Log.d("http" , "${response.code()}")

                if (response.isSuccessful) {
                    val responseData = response.body()
                    // 응답 데이터 로그
                    responseData?.let {
                        Log.d("ApiTest", "응답 데이터: ${it.data}")
                        // 회원이 아니면 true, 맞으면 email 값 반환
                        if(it.data.token == null){
                            // 회원가입 페이지로 넘어가기
                            val intent = Intent(this@login, nickname::class.java)
                            intent.putExtra("email", it.data.email)
                            startActivity(intent)
                        }
                        else {
                            // 다음 페이지로 email 값을 들고 넘어가기
                            val intent = Intent(this@login, GrouplistActivity::class.java)
                            // JWT 토큰을 쿠키에 저장
                            HttpUtil().saveJWTToSharedPreference(this@login, it.data.token, it.data.email)
                            startActivity(intent)
                        }
                    }
                    // 예: responseData를 TextView에 설정하거나, 다른 작업을 수행할 수 있습니다.
                } else {
                    // 요청 실패 처리
                    Log.d("ERRR", "실패")
                }
            }

            override fun onFailure(call: Call<LoginDto>, t: Throwable) {
                Log.d("ERRR", "에러 이유 : $t")
                // 네트워크 오류 처리
            }
        })
    }
    private fun testFunc() {
        // API 서비스 인스턴스 생성
        val apiService = retrofit.create(ApiService::class.java)

        val call = apiService.testData()
        call.enqueue(object : Callback<ObjectDto> {
            override fun onResponse(call: Call<ObjectDto>, response: Response<ObjectDto>) {
                Log.d("http", "${response.code()}")

                if (response.isSuccessful) {
                    val responseData = response.body()
                    // 응답 데이터 로그
                    responseData?.let {
                        Log.d("ApiTest", "테스트 데이터: ${it.message}")
                        // 회원이 아니면 true, 맞으면 email 값 반환
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
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        testFunc()

        val loginbtn: Button = findViewById(R.id.login)
        loginbtn.setOnClickListener {

            // 카카오계정으로 로그인 공통 callback 구성
            // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e("DEG", "카카오계정으로 로그인 실패", error)
                } else if (token != null) {
                    Log.i("DEG", "카카오계정으로 로그인 성공 ${token.accessToken}")
                    kakaoTokenFunc(token.accessToken)
                }
            }

            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                    if (error != null) {
                        Log.e("DEG", "카카오톡으로 로그인 실패", error)

                        // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                        // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }

                        // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                        UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                    } else if (token != null) {
                        Log.i("DEG", "카카오톡으로 로그인 성공 ${token.accessToken}")
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }
    }
}
