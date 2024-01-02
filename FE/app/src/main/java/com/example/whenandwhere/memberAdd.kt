package com.example.whenandwhere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView

class memberAdd : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_add)

        val rectangle3Button = findViewById<Button>(R.id.sharelink)
        rectangle3Button.isEnabled = false

        val exitbtn = findViewById<ImageView>(R.id.arrowleft)

        exitbtn.setOnClickListener {
            val intent = Intent(this, NewGroupMake::class.java)
            startActivity(intent)

        }

    }
}