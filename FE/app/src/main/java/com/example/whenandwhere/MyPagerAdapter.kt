package com.example.whenandwhere

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

    class MyPagerAdapter(fragmentActivity: FragmentActivity, private val dataList: List<MyData>, private val scheduleList: ArrayList<scheduleClass>) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return Int.MAX_VALUE  // 무한 스크롤을 위해 최대값 반환
    }

    override fun createFragment(position: Int): Fragment {
        val index = position % dataList.size
        val data = dataList[index]
        return MyFragment.newInstance(data.week, data.data, scheduleList)
    }

    fun getWeek(position: Int): String {
        val index = position % dataList.size
        return dataList[index].week
    }
}