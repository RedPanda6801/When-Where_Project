package com.example.whenandwhere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.whenandwhere.databinding.ActivityMiddleplaceBinding

class middleplace : AppCompatActivity() {
    private lateinit var binding: ActivityMiddleplaceBinding
    private var alertDialog: AlertDialog? = null // AlertDialog 인스턴스를 저장할 변수 추가
    private var place1Text = "인천광역시 연수구 경원대로 480"
    private var place2Text = "인천광역시 연수구 경원대로 180"
    private var place3Text = "인천 연수구 학나래로118번길 45"
    private lateinit var select1Button: RadioButton
    private lateinit var select2Button: RadioButton
    private lateinit var select3Button: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMiddleplaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 초기화
        select1Button = findViewById(R.id.place1)
        select2Button = findViewById(R.id.place2)
        select3Button = findViewById(R.id.place3)

        select1Button.text = place1Text
        select2Button.text = place2Text
        select3Button.text = place3Text

        val back: ImageView = findViewById(R.id.arrowleft)
        back.setOnClickListener {
            val intent = Intent(this, EditPlace::class.java)
            startActivity(intent)
        }
        R.id.place3

        binding.finish.setOnClickListener {
            val placesRadioGroup: RadioGroup = findViewById(R.id.places)
            val selectedId = placesRadioGroup.checkedRadioButtonId
            if (selectedId != -1) {
                val selectedRadioButton: RadioButton = findViewById(selectedId)
                val selectedText = selectedRadioButton.text.toString()
                showAlcoholDialog(selectedText)
            } else {
                // 라디오 버튼이 선택되지 않은 경우 처리
                Toast.makeText(this, "장소를 선택해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAlcoholDialog(selectedPlace: String) {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.alcohol, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)

        alertDialog = mBuilder.show()

        val yesBtn = mDialogView.findViewById<RadioButton>(R.id.radioButton5)
        yesBtn.setOnClickListener {
            alertDialog?.dismiss() // 다이얼로그 닫기
            moveToNextActivityWithPlace(selectedPlace, true) // 다음 액티비티로 이동하면서 선택된 장소와 true 값을 전달
        }

        val noBtn = mDialogView.findViewById<RadioButton>(R.id.radioButton6)
        noBtn.setOnClickListener {
            alertDialog?.dismiss() // 다이얼로그 닫기
            moveToNextActivityWithPlace(selectedPlace, false) // 다음 액티비티로 이동하면서 선택된 장소와 false 값을 전달
        }
    }

    // 선택된 장소 텍스트와 boolean 값을 다음 액티비티로 전달하는 함수
    private fun moveToNextActivityWithPlace(selectedPlace: String, value: Boolean) {
        val startDate = intent.getStringExtra("SELECTED_DATE")
        val endDate = intent.getStringExtra("endDate")

        val nextIntent = Intent(this, ai_result::class.java).apply {
            putExtra("SELECTED_DATE", startDate)
            putExtra("endDate", endDate)
            putExtra("SELECTED_PLACE", selectedPlace) // 선택된 장소 텍스트를 인텐트에 추가
            putExtra("booleanValue", value) // boolean 값을 인텐트에 추가
        }
        startActivity(nextIntent) // 다음 액티비티로 이동
    }
}
