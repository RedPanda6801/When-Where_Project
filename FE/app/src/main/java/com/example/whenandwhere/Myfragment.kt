package com.example.whenandwhere

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MyFragment : Fragment() {
    private lateinit var WEEK: String
    private lateinit var DATA: String
    private lateinit var SCHEDULES: ArrayList<scheduleClass>

    fun getMonthAndWeek(dateString: String): Pair<String, String> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse(dateString) ?: return Pair("0", "0")

        val calendar = Calendar.getInstance()
        calendar.time = date

        val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH는 0부터 시작하므로 1을 더해줌
        var weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH)
        if(month == 5 && 27 <= calendar.time.date && calendar.time.date <= 31){
            weekOfMonth = 5
        }
        return Pair(month.toString(), weekOfMonth.toString())
    }
    companion object {
        fun newInstance(
            week: String,
            data: String,
            scheduleList: ArrayList<scheduleClass>
        ): MyFragment {
            val args = Bundle()
            args.putString("WEEK", week)
            args.putString("DATA", data)
            args.putSerializable("SCHEDULES", scheduleList)
            val fragment = MyFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            WEEK = it.getString("WEEK")!!
            DATA = it.getString("DATA")!!
            SCHEDULES = it.getSerializable("SCHEDULES") as? ArrayList<scheduleClass> ?: ArrayList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new, container, false)

        highlightSchedule(view)
        return view
    }

    private fun highlightSchedule(view: View) {
        val currentMonth = WEEK.split(" ")[0].dropLast(1).let{if(it.startsWith("0")) it.removePrefix("0") else it}
        val currentWeek = WEEK.split(" ")[1].dropLast(2)
        // 이 메소드에서 선택된 week와 month를 가져와서 비교해야함
        val dayLayouts = listOf(
            view.findViewById<LinearLayout>(R.id.box_mon),
            view.findViewById<LinearLayout>(R.id.box_tue),
            view.findViewById<LinearLayout>(R.id.box_wed),
            view.findViewById<LinearLayout>(R.id.box_thu),
            view.findViewById<LinearLayout>(R.id.box_fri),
            view.findViewById<LinearLayout>(R.id.box_sat),
            view.findViewById<LinearLayout>(R.id.box_sun)
        )

        for (schedule in SCHEDULES) {
            // 스케줄의 년도와 시간을 가져온다.
            val scheduleDate = schedule.startTime.split(" ")[0]
            val (scheduleMonth, scheduleWeek) = getMonthAndWeek(scheduleDate)
            Log.d("SCHEDULE_MONTH", "$scheduleMonth $scheduleWeek $currentMonth $currentWeek")
            val startHour = schedule.startTime.substring(11, 13).toInt()
            val endHour = schedule.endTime.substring(11, 13).toInt()
            val dayIndex = getDayIndex(schedule.startTime.substring(0, 10)) // Assuming date format "yyyy-MM-dd"

            if (dayIndex in dayLayouts.indices) {
                val dayLayout = dayLayouts[dayIndex]
                for (hour in startHour..endHour) {
                    if (hour - 10 in 0 until dayLayout.childCount &&
                        // 선택된 날짜와 스케줄 날짜가 같아야 disabled 되도록 함
                        scheduleMonth == currentMonth && scheduleWeek == currentWeek
                    ) {
                        val hourView = dayLayout.getChildAt(hour - 10)
                        hourView.setBackgroundResource(R.drawable.box_background)
                        hourView.isClickable = false
                    }
                }
            }
        }

        for (dayIndex in dayLayouts.indices) {
            val dayLayout = dayLayouts[dayIndex]
            for (i in 0 until dayLayout.childCount) {
                val hourView = dayLayout.getChildAt(i)
                if (isOrangeBackground(hourView)) {
                    hourView.isClickable = true
                    hourView.setOnClickListener {
                        val tag = hourView.tag as? String
                        val hour = tag?.substringAfter("hour_")?.toIntOrNull()
                        if (hour != null) {
                            showCheckPopup(dayIndex, hour)
                        }
                    }
                }
            }
        }
    }

    private fun isOrangeBackground(view: View): Boolean {
        return view.background.constantState == resources.getDrawable(R.drawable.box_selected_background).constantState
    }

    private fun showCheckPopup(dayIndex: Int, hour: Int) {
        val activity = activity ?: return
        val dialogView =
            LayoutInflater.from(activity).inflate(R.layout.time_result_check_popup, null)
        val alertDialogBuilder = AlertDialog.Builder(activity).setView(dialogView)
        val alertDialog = alertDialogBuilder.create()

        val date = getDateForDayIndex(dayIndex) // 요일 인덱스에 대한 날짜를 구하는 함수 호출
        val timeTextView = dialogView.findViewById<TextView>(R.id.checktext)
        timeTextView.text = "선택된 일정: ${date} ${hour}:00"
        dialogView.findViewById<Button>(R.id.confirm).setOnClickListener {
            HttpUtil().saveSelectedDateFromSharedPreference(requireContext(), "${date} ${hour}:00")
            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.cancel).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun getDateForDayIndex(dayIndex: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.add(Calendar.DAY_OF_WEEK, dayIndex)

        val sdf = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        return sdf.format(calendar.time)
    }

    private fun getDayIndex(dateString: String): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateString)
        val calendar = Calendar.getInstance().apply { time = date }
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> -1
        }
    }
}