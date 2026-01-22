package com.example.whenandwhere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.whenandwhere.databinding.ActivityGroupSettingLeaderBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create

class GroupSetting_leader : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_setting_leader)

        val back: ImageView = findViewById(R.id.arrowleft) // 이전 화면 버튼
        val resignButton: Button = findViewById(R.id.resingBtn) //내보내기 버튼
        val addButton: Button = findViewById(R.id.addMemberBtn) //멤버 초대 버튼
        val exitButton: Button = findViewById(R.id.exitGroupBtn) //탈퇴 버튼
        val acceptButton : Button = findViewById(R.id.acceptBtn) //가입 허락 버튼
        val removeButton:Button = findViewById(R.id.removeGroupBtn) //그룹 삭제 버튼

        // http 세팅
        val jwt = HttpUtil().getJWTFromSharedPreference(this) ?: ""
        val client = HttpUtil().createClient(jwt)
        val retrofit = HttpUtil().createRetrofitWithHeader(client)
        val groupId = HttpUtil().getCurrentGroupIdFromSharedPreference(this)

        back.setOnClickListener {
            val intent = Intent(this, Grouphome::class.java)
            startActivity(intent)
        }

        resignButton.setOnClickListener {
            val intent = Intent(this, Resign::class.java)
            startActivity(intent)
        }

        // 클릭 리스너 설정


        addButton.setOnClickListener {
            val intent = Intent(this, memberAdd::class.java)
            startActivity(intent)
        }

        exitButton.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.exit_popup, null)
            val alertDialogBuilder = AlertDialog.Builder(this).setView(dialogView)
            val alertDialog = alertDialogBuilder.create()

            val confirmTextView = dialogView.findViewById<TextView>(R.id.confirmTextView)
            confirmTextView.text = "정말 모임을 나가시겠습니까?"

            // Positive 버튼 클릭 리스너 설정
            dialogView.findViewById<Button>(R.id.yesButton).setOnClickListener {
                alertDialog.dismiss()
                // "예" 버튼을 클릭할 때 수행할 작업 추가
                val apiService = retrofit.create(ApiService::class.java)
                val call = apiService.exitGroup(groupId)

                call.enqueue(object : Callback<ObjectDto> {
                    override fun onResponse(call: Call<ObjectDto>, response: Response<ObjectDto>) {
                        Log.d("http" , "${response.code()}")
                        if(response.code() == 200){
                            val intent = Intent(this@GroupSetting_leader, GrouplistActivity::class.java)
                            startActivity(intent)
                        }
                        else if(response.code() == 400){
                            // 잘못된 요청에 대한 처리
                        }
                        else if(response.code()== 404){
                            // 잘못된 권한에 대한 처리(HOST는 나갈 수 없다)
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

        acceptButton.setOnClickListener {
            val intent = Intent(this, Accept::class.java)
            startActivity(intent)
        }

        removeButton.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.remove_popup, null)
            val alertDialogBuilder = AlertDialog.Builder(this).setView(dialogView)
            val alertDialog = alertDialogBuilder.create()

            val confirmTextView = dialogView.findViewById<TextView>(R.id.confirmTextView)
            confirmTextView.text = "정말 모임을 삭제하시겠습니까?"

            // Positive 버튼 클릭 리스너 설정
            dialogView.findViewById<Button>(R.id.yesButton).setOnClickListener {
                alertDialog.dismiss()
                // "예" 버튼을 클릭할 때 수행할 작업 추가
                val apiService = retrofit.create(ApiService::class.java)
                val call = apiService.deleteGroup(GroupDto(groupId, "", ""))

                call.enqueue(object : Callback<ObjectDto> {
                    override fun onResponse(call: Call<ObjectDto>, response: Response<ObjectDto>) {
                        Log.d("http" , "${response.code()}")
                        if(response.code() == 200){
                            val intent = Intent(this@GroupSetting_leader, GrouplistActivity::class.java)
                            startActivity(intent)
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
    }
}
