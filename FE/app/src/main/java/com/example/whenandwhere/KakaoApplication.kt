package com.example.whenandwhere

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class KakaoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // KaKao SDK  초기화
        KakaoSdk.init(this, "3d164f1ef97c8f24a2f77a9b178ca82f")
    }
}