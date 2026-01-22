package com.example.whenandwhere

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class EditPlace : AppCompatActivity() {
    val editPlaceList = ArrayList<editplaceClass>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_place)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val resultButton = findViewById<Button>(R.id.resultbutton)

        val memberList = intent.getStringArrayListExtra("memberNicknameList")
        val memberIds = intent.getIntegerArrayListExtra("MEMBER_IDS")

        // api 실행 및 그룹 리스트 매핑시키기
        lifecycleScope.launch {
            if (!memberList.isNullOrEmpty()) {
                for (member in memberList) {
                    editPlaceList.add(editplaceClass(member,"", true))
                }
            }
            recyclerView.layoutManager = LinearLayoutManager(this@EditPlace)
            val adapter = editplaceAdapter(editPlaceList)
            recyclerView.adapter = adapter
        }

        // EditText의 포커스를 제거
        resultButton.setOnClickListener {
            var inputPlaceList = ArrayList<String>()
            (resultButton.parent as? ViewGroup)?.requestFocus()

            for(place in editPlaceList){
                Log.d("PLACE", place.departurePlace)
                inputPlaceList.add(place.departurePlace)
            }
            val intent = Intent(this, TimeResultActivity::class.java).apply{
                putIntegerArrayListExtra("MEMBER_IDS", memberIds)
                putStringArrayListExtra("PLACE_LIST", inputPlaceList)
            }
            startActivity(intent)
        }
    }
}