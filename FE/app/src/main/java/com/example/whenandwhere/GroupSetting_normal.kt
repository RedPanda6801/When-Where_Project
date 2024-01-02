package com.example.whenandwhere

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton

class GroupSetting_normal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_setting_normal)

        val button = findViewById<TextView>(R.id.exit)
        val textView = button.findViewById<TextView>(android.R.id.text1)
        textView.setPadding(16, 0, 0, 0)
    }
}