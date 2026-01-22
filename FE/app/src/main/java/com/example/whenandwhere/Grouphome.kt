package com.example.whenandwhere

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Grouphome : AppCompatActivity() {
    private lateinit var grouptitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grouphome)

        val backButton = findViewById<ImageView>(R.id.arrowleft)
        val setBtn = findViewById<ImageView>(R.id.settings)
        val editScheduleButton: Button = findViewById(R.id.editschedulebutton)
        val resultbutton4: Button = findViewById(R.id.resultbutton4)

        grouptitle = findViewById(R.id.grouptitle)

        grouptitle.text = HttpUtil().getCurrentGroupNameFromSharedPreference(this)

        backButton.setOnClickListener {
            val intent = Intent(this, GrouplistActivity::class.java)
            startActivity(intent)
        }

        setBtn.setOnClickListener {
            val intent = Intent(this, GroupSetting_leader::class.java)
            startActivity(intent)}

        editScheduleButton.setOnClickListener {
            val intent = Intent(this, ScheduleTitle::class.java)
            startActivity(intent)
        }

        resultbutton4.setOnClickListener {
            val intent = Intent(this, moimResult::class.java)
            startActivity(intent)
        }

    }
}