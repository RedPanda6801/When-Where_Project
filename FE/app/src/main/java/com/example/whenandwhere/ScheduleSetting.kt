package com.example.whenandwhere

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.*

class ScheduleSetting : AppCompatActivity() {
    private lateinit var scheduleTitleText: TextView
    private lateinit var scheduleList: MutableList<ScheduleItem>
    private lateinit var selectedSchedule: ScheduleItem // 선택된 스케줄을 저장하는 변수
    private lateinit var startYearPicker: NumberPicker
    private lateinit var startMonthPicker: NumberPicker
    private lateinit var startDayPicker: NumberPicker
    private lateinit var startHourPicker: NumberPicker
    private lateinit var startMinPicker: NumberPicker
    private lateinit var endYearPicker: NumberPicker
    private lateinit var endMonthPicker: NumberPicker
    private lateinit var endDayPicker: NumberPicker
    private lateinit var endHourPicker: NumberPicker
    private lateinit var endMinPicker: NumberPicker

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_setting)

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val intent = intent
        val userId = intent.getStringExtra("memberId") ?: ""
        val userNick = intent.getStringExtra("memberNickname")
        // Retrofit 객체 생성
        val jwt = HttpUtil().getJWTFromSharedPreference(this) ?: ""
        val client = HttpUtil().createClient(jwt)
        val retrofit = HttpUtil().createRetrofitWithHeader(client)


        Log.d("DATA", "$userId $userNick")
        scheduleTitleText = findViewById(R.id.schedule_title)
        scheduleTitleText.text = "$userNick 의 일정"

        startYearPicker = findViewById(R.id.yearPicker_start)
        startMonthPicker = findViewById(R.id.monthPicker_start)
        startDayPicker = findViewById(R.id.daypicker_start)
        startHourPicker = findViewById(R.id.hourPicker_start)
        startMinPicker = findViewById(R.id.minPicker_start)
        endYearPicker = findViewById(R.id.yearPicker_end)
        endMonthPicker = findViewById(R.id.monthPicker_end)
        endDayPicker = findViewById(R.id.daypicker_end)
        endHourPicker = findViewById(R.id.hourPicker_end)
        endMinPicker = findViewById(R.id.minPicker_end)

        startYearPicker.minValue = 2000
        startYearPicker.maxValue = currentYear + 10
        startYearPicker.value = currentYear
        startYearPicker.setFormatter { value ->
            value.toString() // 또는 String.format("%d", value)로 포맷
        }

        startMonthPicker.minValue = 1
        startMonthPicker.maxValue = 12
        startMonthPicker.value = currentMonth
        startMonthPicker.setFormatter { value -> String.format("%02d", value) }

        startDayPicker.minValue = 1
        startDayPicker.maxValue = 31
        startDayPicker.setFormatter { value -> String.format("%02d", value) }

        startHourPicker.minValue = 0
        startHourPicker.maxValue = 23
        startHourPicker.setFormatter { value -> String.format("%02d", value) }

        startMinPicker.minValue = 0
        startMinPicker.maxValue = 59
        startMinPicker.setFormatter { value -> String.format("%02d", value) }

        endYearPicker.minValue = 2000
        endYearPicker.maxValue = currentYear + 10
        endYearPicker.value = currentYear
        endYearPicker.setFormatter { value ->
            value.toString()
        }

        endMonthPicker.minValue = 1
        endMonthPicker.maxValue = 12
        endMonthPicker.setFormatter { value -> String.format("%02d", value) }

        endDayPicker.minValue = 1
        endDayPicker.maxValue = 31
        endDayPicker.setFormatter { value -> String.format("%02d", value) }

        endHourPicker.minValue = 0
        endHourPicker.maxValue = 23
        endHourPicker.setFormatter { value -> String.format("%02d", value) }

        endMinPicker.minValue = 0
        endMinPicker.maxValue = 59
        endMinPicker.setFormatter { value -> String.format("%02d", value) }

        // api 실행 및 그룹 리스트 매핑시키기
        lifecycleScope.launch {
            scheduleList = getSchedules(intent, retrofit)
            Log.d("ScheduleList", "${scheduleList}")
            val recyclerView = findViewById<RecyclerView>(R.id.schedule_recycler_view)
            recyclerView.layoutManager = LinearLayoutManager(this@ScheduleSetting)
            val adapter = ScheduleSettingAdapter(scheduleList) { scheduleItem ->
                showDeletePopup(retrofit, scheduleItem)
            }
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged() // 데이터 변경 알림
        }

        // SlidingUpPanelLayout에 대한 참조를 가져옵니다.
        val slidingLayout = findViewById<SlidingUpPanelLayout>(R.id.main_frame)

        // 각 뷰에 대한 참조를 가져옵니다.
        val backButton = findViewById<ImageView>(R.id.arrowleft)
        val addScheduleButton = findViewById<AppCompatButton>(R.id.add_schedule)

        // 일정 추가에 대한 뷰들의 참조를 가져옵니다
        val addScheduleButtonInSlide = findViewById<TextView>(R.id.add)
        val titleInput = findViewById<EditText>(R.id.scheduletitle)
        val detailInput = findViewById<EditText>(R.id.scheduledetail)

        // 각 뷰에 대한 클릭 리스너를 설정합니다.
        backButton.setOnClickListener {
            val intent = Intent(this, ScheduleTitle::class.java)
            startActivity(intent)
        }

        //customCalendarview(2줄)
        val materialCalendarView: MaterialCalendarView = findViewById(R.id.calendarView)
        materialCalendarView.setSelectedDate(CalendarDay.today())

        // '일정 추가하기' 버튼을 클릭하면 슬라이딩 패널이 확장됩니다.
        addScheduleButton.setOnClickListener {
            slidingLayout.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
        }

        // 슬라이드 안의 실제 데이터 추가하기 버튼
        addScheduleButtonInSlide.setOnClickListener {
            val titleText = titleInput.text.toString()
            val detailText = detailInput.text.toString()

            // 두자리 포맷팅을 추가합니다.
            val startMonthFormatted = String.format("%02d", startMonthPicker.value)
            val startDayFormatted = String.format("%02d", startDayPicker.value)
            val startHourFormatted = String.format("%02d", startHourPicker.value)
            val startMinFormatted = String.format("%02d", startMinPicker.value)

            val endMonthFormatted = String.format("%02d", endMonthPicker.value)
            val endDayFormatted = String.format("%02d", endDayPicker.value)
            val endHourFormatted = String.format("%02d", endHourPicker.value)
            val endMinFormatted = String.format("%02d", endMinPicker.value)

            val startTimeText =
                "${startYearPicker.value}-$startMonthFormatted-$startDayFormatted $startHourFormatted:$startMinFormatted:00"
            val endTimeText =
                "${endYearPicker.value}-$endMonthFormatted-$endDayFormatted $endHourFormatted:$endMinFormatted:00"
            Log.d("Input Text", "$titleText $detailText $startTimeText $endTimeText")
            val newSchedule = ScheduleDto(0, titleText, detailText, startTimeText, endTimeText)
            addScheduleFunc(retrofit, newSchedule, userId)
        }

//        scheduleList = generateScheduleList().toMutableList()

        materialCalendarView.setOnDateChangedListener { widget, date, selected ->
            selectWeek(widget, date)
        }
    }

    private fun showDeletePopup(retrofit: Retrofit, scheduleItem: ScheduleItem) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.schedule_popup, null)
        val alertDialogBuilder = AlertDialog.Builder(this).setView(dialogView)
        val alertDialog = alertDialogBuilder.create()
        selectedSchedule = scheduleItem

        // Populating the dialog with schedule data if needed
        val Title = dialogView.findViewById<TextView>(R.id.scheduleTitle)
        Title.text = scheduleItem.title // 필요한 텍스트로 설정

        val Start = dialogView.findViewById<TextView>(R.id.scheduleStart)
        Start.text = "시작 날짜 : ${scheduleItem.startTime.split(" ")[0]}\n 시작 시간: ${scheduleItem.startTime.split(" ")[1]}" // 필요한 텍스트로 설정

        val End = dialogView.findViewById<TextView>(R.id.scheduleEnd)
        End.text = "종료 날짜 : ${scheduleItem.endTime.split(" ")[0]}\n 종료 시간: ${scheduleItem.endTime.split(" ")[1]}" // 필요한 텍스트로 설정 // 필요한 텍스트로 설정

        // Set up the confirm and cancel buttons
        dialogView.findViewById<Button>(R.id.deleteSchedule).setOnClickListener {
            // Handle the confirm button click
            showSecondPopup(retrofit)
            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.cancel).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun showSecondPopup(retrofit:Retrofit) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.schedule_second_popup, null)
        val alertDialogBuilder = AlertDialog.Builder(this).setView(dialogView)
        val alertDialog = alertDialogBuilder.create()

        // Populating the dialog with schedule data if needed
        val titleTextView = dialogView.findViewById<TextView>(R.id.titletext)
        titleTextView.text = "정말 일정을 삭제하시겠습니까?" // 필요한 텍스트로 설정

        // 두 번째 팝업창의 버튼을 설정합니다.
        dialogView.findViewById<Button>(R.id.deleteCheck).setOnClickListener {

            // 두 번째 팝업창의 확인 버튼 클릭 시의 동작을 정의합니다.
            deleteSchedule(retrofit, selectedSchedule.id)
            val intent = (this as Activity).intent
            this.finish() //현재 액티비티 종료 실시
            this.startActivity(intent) //현재 액티비티 재실행 실시

            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.cancelCheck).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun selectWeek(widget: MaterialCalendarView, date: CalendarDay) {
        val calendar = Calendar.getInstance()
        calendar.set(date.year, date.month, date.day)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 일요일이 1이므로 0으로 만듦
        calendar.add(Calendar.DAY_OF_MONTH, -dayOfWeek)
        val startDate = calendar.time
        val endDate = Calendar.getInstance()
        endDate.time = startDate
        endDate.add(Calendar.DAY_OF_MONTH, 6) // 선택된 날짜로부터 6일 후까지의 날짜를 종료일로 설정

        widget.clearSelection()
        calendar.time = startDate
        while (calendar.time <= endDate.time) {
            widget.setDateSelected(CalendarDay.from(calendar), true)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    private suspend fun getSchedules(intent : Intent, retrofit: Retrofit) : ArrayList<ScheduleItem> {
        val userId = intent.getStringExtra("memberId") ?: ""
        return withContext(Dispatchers.IO) {

            val apiService = retrofit.create(ApiService::class.java)
            val call = apiService.getSchedules(userId)

            val response = call.execute()
            if (response.isSuccessful) {
                val responseData = response.body()
                // 응답 데이터 로그
                responseData?.let {
                    Log.d("ApiTest", "유저 스케줄: ${it.data}")
                    val resultList = arrayListOf<ScheduleItem>()
                    for(schedule in it.data){
                        resultList.add(ScheduleItem(schedule.id, schedule.title, schedule.detail, schedule.startTime, schedule.endTime))
                    }

                    return@withContext resultList
                }
                // 예: responseData를 TextView에 설정하거나, 다른 작업을 수행할 수 있습니다.
            } else {
                // 요청 실패 처리
                Log.d("ERRR", "실패")
            }
            return@withContext ArrayList()
        }
    }

    private fun deleteSchedule( retrofit: Retrofit, scheduleId : Int) {
        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.deleteSchedule(ScheduleDto(scheduleId, "", "" ,"" , ""))

        call.enqueue(object : Callback<ObjectDto> {
            override fun onResponse(call: Call<ObjectDto>, response: Response<ObjectDto>) {
                if (response.code() == 200) {
                    Log.d("ApiTest", "스케줄 삭제 여부: ${response.code()}")
                    // 예: responseData를 TextView에 설정하거나, 다른 작업을 수행할 수 있습니다.
                } else {
                    // 요청 실패 처리
                    Log.d("ERRR", "실패")
                }
            }
            override fun onFailure(call: Call<ObjectDto>, t: Throwable) {
                Log.d("ERRR", "에러 이유 : $t")
                // 네트워크 오류 처리
            }
        })
    }


    private fun addScheduleFunc(retrofit: Retrofit, scheduleDto: ScheduleDto, email: String) {
        // api 요청
        Log.d("TESTET", email)
        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.addSchedule(scheduleDto, email)

        // Validation
        if (scheduleDto.title.equals("") || scheduleDto.title == null) {
            Log.d("Validation", "title NULL")
            return
        } else if (scheduleDto.detail.equals("") || scheduleDto.detail == null) {
            Log.d("Validation", "detail NULL")
            return
        } else if (scheduleDto.startTime.equals("") || scheduleDto.startTime == null) {
            Log.d("Validation", "startTime NULL")
            return
        } else if (scheduleDto.endTime.equals("") || scheduleDto.endTime == null) {
            Log.d("Validation", "endTime NULL")
            return
        }
        call.enqueue(object : Callback<ObjectDto> {
            override fun onResponse(call: Call<ObjectDto>, response: Response<ObjectDto>) {
                if (response.code() == 201) {
                    val activity = this@ScheduleSetting
                    val intent = activity.intent
                    activity.finish() // 현재 액티비티 종료
                    activity.startActivity(intent) // 현재 액티비티 재실행
                    Log.d("ApiTest", "스케줄 처리 여부: ${response.code()}")
                    // 예: responseData를 TextView에 설정하거나, 다른 작업을 수행할 수 있습니다.
                } else {
                    // 요청 실패 처리
                    Log.d("ERRR", "실패")
                }
            }

            override fun onFailure(call: Call<ObjectDto>, t: Throwable) {
                Log.d("ERRR", "에러 이유 : $t")
                // 네트워크 오류 처리
            }
        })
    }
}