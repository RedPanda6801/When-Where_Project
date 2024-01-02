package com.example.whenandwhere

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



//        supportFragmentManager.beginTransaction().replace(R.id.main_bottom_navigation, place_edit()).commit()
//
//        textView3.setOnItemSelectedListener {
//            when(it.itemId) {
//                R.id.bottom_place -> {
//                    supportFragmentManager.beginTransaction().replace(R.id.layout_nav_bottom, place_edit()).commit()
//                }
//                R.id.bottom_schedule -> {
//                    supportFragmentManager.beginTransaction().replace(R.id.layout_nav_bottom, schedule_add()).commit()
//                }
//                R.id.bottom_member -> {
//                    supportFragmentManager.beginTransaction().replace(R.id.layout_nav_bottom, NavBottomProfile()).commit()
//                }
//                R.id.bottom_setting -> {
//                    supportFragmentManager.beginTransaction().replace(R.id.layout_nav_bottom, GroupSetting_leader()).commit()
//                }
//            }
//
//            true
//        }

        val exitbtn = findViewById<ImageView>(R.id.arrowleft)

        exitbtn.setOnClickListener {
            val intent = Intent(this, GrouplistActivity::class.java)
            startActivity(intent)
        }
  }
}