package com.example.whenandwhere

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.whenandwhere.databinding.ActivityTimeResultBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class TimeResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTimeResultBinding
    private lateinit var scheduleList: ArrayList<scheduleClass>
    private lateinit var pagerAdapter: MyPagerAdapter
    private var selectedPosition by Delegates.notNull<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimeResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val memberIds = intent.getIntegerArrayListExtra("MEMBER_IDS") ?: ArrayList<Int>()
        val placeList = intent.getStringArrayListExtra("PLACE_LIST")
        selectedPosition = 22

        // Current date information
        val currentCalendar = Calendar.getInstance()
        val currentYear = currentCalendar.get(Calendar.YEAR)
        val currentMonth = currentCalendar.get(Calendar.MONTH)
        var currentWeek = currentCalendar.get(Calendar.WEEK_OF_MONTH)

        // Generate week data for the current year
        val dataList = generateWeekDataForYear(currentYear)
        val viewPager: ViewPager2 = findViewById(R.id.viewpager)
        val indicatorText: TextView = findViewById(R.id.indicator_text)
        // http 세팅
        val jwt = HttpUtil().getJWTFromSharedPreference(this) ?: ""
        val client = HttpUtil().createClient(jwt)
        val retrofit = HttpUtil().createRetrofitWithHeader(client)

//         scheduleList 초기화
        scheduleList = ArrayList()

        lifecycleScope.launch {
            val requestScheduleList = getScheduleList(retrofit, memberIds)
            for(requestSchedule in requestScheduleList){
                Log.d("CALC_SCHEDULE","${requestSchedule.startTime} ${requestSchedule.endTime}")
                scheduleList.add(scheduleClass(requestSchedule.startTime, requestSchedule.endTime))
            }
            pagerAdapter = MyPagerAdapter(this@TimeResultActivity, dataList, scheduleList)
            viewPager.adapter = pagerAdapter

            // Find the initial position based on the current month and week
            val initialPosition = dataList.indexOfFirst { data ->
                val (monthString, weekString) = data.week.split(" ")
                val month = monthString.substringBefore("월").toInt() // "월" 앞의 문자열을 추출하여 정수로 변환
                val week = weekString.substringBefore("주차").toInt() // "주차" 앞의 문자열을 추출하여 정수로 변환
                month == currentMonth + 1 && week == 4
            }

            viewPager.setCurrentItem(initialPosition, false) // 초기 위치 설정

            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    indicatorText.text = pagerAdapter.getWeek(position)
                    selectedPosition = position
                }
            })
        }

        val backbutton = findViewById<ImageView>(R.id.arrowleft)
        backbutton.setOnClickListener {
            // PutExtra 전달 값 때문에 scheduletitle로 다시 이동해야함
            val intent = Intent(this, ScheduleTitle::class.java)
            startActivity(intent)
        }

        val nextbtn = findViewById<Button>(R.id.resultbutton)
        nextbtn.setOnClickListener {
            val selectedDate = HttpUtil().getSelectedDateFromSharedPreference(this) ?: ""
            Log.d("SELECTED_DATE", selectedDate)
            val intent = Intent(this, middleplace::class.java).apply{
                putExtra("SELECTED_DATE" , selectedDate)
                putStringArrayListExtra("PLACE_LIST", placeList)
            }
            startActivity(intent)
        }

    }

    private fun generateWeekDataForYear(year: Int): List<MyData> {
        val calendar = Calendar.getInstance()
        val weekDataList = mutableListOf<MyData>()

        var weekOfMonth = 0
        var mondayCount = 0 // 월요일 카운트 변수 추가

        for (month in 0 until 12) {
            calendar.set(year, month, 1)

            // Find the first Monday of the month
            var firstMonday = calendar.firstDayOfWeek
            Log.d("MONDAY", "$firstMonday")
            while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                calendar.add(Calendar.DATE, 1)
                firstMonday++
            }

            while (calendar.get(Calendar.MONTH) == month) {
                // Increment week number only if the next week is still within the same month
                // 월요일인 경우 카운트 증가
                var formatMonth = month + 1

                // 한 주의 월요일이 5개를 초과하는 경우
                if(calendar.get(Calendar.DAY_OF_MONTH) == 1){
                    mondayCount = 0
                    weekOfMonth = 0
                }
                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                    mondayCount++
                }
                if (mondayCount > 4) {
                    weekOfMonth = 1 // weekOfMonth를 1로 초기화
                    mondayCount = 0 // 카운트 리셋
                    if((month == 0 || month == 3) && calendar.get(Calendar.DAY_OF_MONTH) == 29)
                        formatMonth = month + 2
                }
                else{
                    weekOfMonth++ // 그렇지 않으면 증가
                }

                val formattedDate = "${year}-${month + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}"
                val weekData = MyData("${formatMonth}월 ${weekOfMonth}주차", "데이터 $formattedDate")

                weekDataList.add(weekData)
                calendar.add(Calendar.DATE, 7)
            }
        }
        return weekDataList
    }
}

private suspend fun getScheduleList(
    retrofit: Retrofit, members: ArrayList<Int>
) : ArrayList<scheduleClass>{
    return withContext(Dispatchers.IO) {
        // api 호출
        val startDate = "2020-01-01"
        val endDate = "2025-12-31"
        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.calcSchedule(BusyTimeDto(members, startDate, endDate))
        val response = call.execute()
        if (response.isSuccessful) {
            val responseData = response.body()
            // 응답 데이터 로그
            responseData?.let {
                Log.d("ApiTest", "SCHEDULE_CALC: $it $members")
                if(it.data is ArrayList<CalcScheduleDto>){
                    var resultCalcSchedule = ArrayList<scheduleClass>()
                    for(schedule in it.data){
                        val newSchedule = scheduleClass(schedule.startTime, schedule.endTime)
                        resultCalcSchedule.add(newSchedule)
                    }
                    return@withContext resultCalcSchedule
                }
            }
            // 예: responseData를 TextView에 설정하거나, 다른 작업을 수행할 수 있습니다.
        } else {
            // 요청 실패 처리
            Log.d("ERRR", "실패")
        }
        return@withContext ArrayList<scheduleClass>()
    }
}