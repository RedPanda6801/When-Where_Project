package com.example.whenandwhere

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.RadioButton
import android.widget.RadioGroup


class SelectionOption : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection_option)

        val radioGroupLine1: RadioGroup = findViewById(R.id.rg_line1)
        val radioGroupLine2: RadioGroup = findViewById(R.id.rg_line2)

        setupRadioGroups(radioGroupLine1, radioGroupLine2)
    }

    private fun setupRadioGroups(currentGroup: RadioGroup, otherGroup: RadioGroup) {
        setRadioGroupClickListener(currentGroup, otherGroup)
        setRadioGroupClickListener(otherGroup, currentGroup)
    }

    private fun setRadioGroupClickListener(
        currentGroup: RadioGroup,
        otherGroup: RadioGroup
    ) {
        currentGroup.setOnCheckedChangeListener(null)
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