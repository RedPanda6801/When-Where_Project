package com.example.whenandwhere

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NewGroupMake : AppCompatActivity() {

    private lateinit var groupNameEditText: EditText
    private var selectedRadioText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_group_make)

        groupNameEditText = findViewById(R.id.nameinput)
        val back: ImageView = findViewById(R.id.arrowleft)
        val addMemberButton: Button = findViewById(R.id.addMember)
        val radioGroupLine1: RadioGroup = findViewById(R.id.rg_line1)
        val radioGroupLine2: RadioGroup = findViewById(R.id.rg_line2)

        setupRadioGroups(radioGroupLine1, radioGroupLine2)

        addMemberButton.setOnClickListener {
            // input 값 가져오기
            val groupName = groupNameEditText.text.toString()

            // http 세팅
            val jwt = HttpUtil().getJWTFromSharedPreference(this@NewGroupMake) ?: ""
            val client = HttpUtil().createClient(jwt)
            val retrofit = HttpUtil().createRetrofitWithHeader(client)

            // api 호출
            val apiService = retrofit.create(ApiService::class.java)
            val call = apiService.createGroup(GroupDto(id = 0, groupName = groupName, attribute = selectedRadioText))

            call.enqueue(object : Callback<ObjectDto> {
                override fun onResponse(call: Call<ObjectDto>, response: Response<ObjectDto>) {
                    Log.d("http" , "${response.code()}")

                    if(response.code() == 201){
                        val intent = Intent(this@NewGroupMake, GrouplistActivity::class.java)
                        startActivity(intent)
                    }
                    else {
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

        back.setOnClickListener{
            val intent = Intent(this, GrouplistActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRadioGroups(currentGroup: RadioGroup, otherGroup: RadioGroup) {
        setRadioGroupClickListener(currentGroup, otherGroup)
        setRadioGroupClickListener(otherGroup, currentGroup)
    }

    private fun setRadioGroupClickListener(
        currentGroup: RadioGroup,
        otherGroup: RadioGroup
    ) {
        currentGroup.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton = findViewById<RadioButton>(checkedId)
            if (checkedRadioButton != null && checkedRadioButton.isChecked) {
                selectedRadioText = checkedRadioButton.text.toString()
                Log.d("selectedRadiotext의 값", selectedRadioText)
            }
        }

        for (i in 0 until currentGroup.childCount) {
            val radioButton = currentGroup.getChildAt(i) as RadioButton
            radioButton.setOnClickListener {
                if (!radioButton.isChecked) {
                    // 이미 선택된 상태인 라디오 버튼을 클릭한 경우
                    currentGroup.clearCheck()
                } else {
                    // 선택되지 않은 라디오 버튼을 클릭한 경우
                    otherGroup.clearCheck()
                }
            }
        }
    }
}

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            openGalleryForImage()
//        }
//    }
//
//    private fun openGalleryForImage() {
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = "image/*"
//        startActivityForResult(intent, PICK_IMAGE_REQUEST)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
//            val uri = data.data
//            groupImageView.setImageURI(uri)
//        }
//    }

