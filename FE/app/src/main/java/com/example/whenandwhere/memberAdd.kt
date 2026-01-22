package com.example.whenandwhere

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class memberAdd : AppCompatActivity() {
    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_add)

        val back : ImageView = findViewById(R.id.arrowleft)
        val shareLink : Button = findViewById(R.id.sharelink)

        // 뒤로가기 버튼
        back.setOnClickListener {
            val intent = Intent(this, GroupSetting_leader::class.java)
            startActivity(intent)
        }
        // 링크 복사 또는 공유 버튼
        shareLink.setOnClickListener {
            Log.d("URL", "url")
            // URL 복사
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val groupId = HttpUtil().getCurrentGroupIdFromSharedPreference(this)
            val groupName = HttpUtil().getCurrentGroupNameFromSharedPreference(this)
            val url = "${HttpUtil().getServerURL()}/oauth?groupId=${groupId}"
            Log.d("URL", url)
            val clip = ClipData.newPlainText("URL", url)
            clipboard.setPrimaryClip(clip)

            val invoice = clipboard.getPrimaryClip()
            AlertDialog.Builder(this)
                .setTitle("복사된 URL ${invoice.toString()}")
            Log.d("CLIPBOARD", invoice.toString())
        }
    }
}