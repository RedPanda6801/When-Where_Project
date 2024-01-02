package com.example.whenandwhere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whenandwhere.databinding.ActivityGrouplistBinding
import com.example.whenandwhere.databinding.ActivityLoginBinding



class GrouplistActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGrouplistBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGrouplistBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val grouplist = arrayListOf(
            Groups("8반 찐따 모임", R.drawable.alone),
            Groups("훈련소 동기들", R.drawable.army),
            Groups("셉친자들", R.drawable.juyeon),
            Groups("똥영 카르텔", R.drawable.tongyeong)
        )

        binding.recyclerView.layoutManager = GridLayoutManager(applicationContext,2)
        binding.recyclerView.adapter = GroupAdapter(grouplist)

        val makeBtn = findViewById<Button>(R.id.makemoim)

        makeBtn.setOnClickListener {
            val intent = Intent(this,NewGroupMake::class.java)
            startActivity(intent)
        }



    }




}

