package com.example.whenandwhere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView

class NewGroupMake : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_group_make)

        val btn = findViewById<androidx.appcompat.widget.AppCompatButton>(androidx.appcompat.R.id.add)

        btn.setOnClickListener {
            val intent = Intent(this,memberAdd::class.java)
            startActivity(intent)
        }

        val exitbtn = findViewById<ImageView>(R.id.arrowleft)

        exitbtn.setOnClickListener {
            val intent = Intent(this,GrouplistActivity::class.java)
            startActivity(intent)
        }

    }
}