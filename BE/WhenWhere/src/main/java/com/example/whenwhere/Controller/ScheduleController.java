package com.example.whenwhere.Controller;

import com.example.whenwhere.Dto.BusytimeDto;
import com.example.whenwhere.Dto.ResponseDto;
import com.example.whenwhere.Dto.ScheduleDto;
import com.example.whenwhere.Entity.Schedule;
import com.example.whenwhere.Service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;

    @PostMapping("/api/schedule/add/{id}")
    @ResponseBody
    public ResponseEntity<ResponseDto> addSchedule(@PathVariable Integer id, @RequestBody ScheduleDto scheduleDto){
        ResponseDto response = new ResponseDto();
        System.out.println(scheduleDto);
        boolean result = scheduleService.add(scheduleDto, id);
        // 성공 실패 시 Error 응답
        if(!result){
            response.setResponse("Failed to Create Schedule", HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 빈 시간 계산 API
    @PostMapping("/api/schedule/busytime-group")
    @ResponseBody
    public ResponseEntity<ResponseDto> busyTimeInGroupSchedule(@RequestBody BusytimeDto busytimeDto){
        ResponseDto response = new ResponseDto();
        // members의 schedule을 날짜 별로 정렬하여 가져오는 service
        List<Schedule> sortedTimes =  scheduleService.getSortedTimesByDates(busytimeDto);
        // 해당 service에 대한 예외 처리
        if(sortedTimes == null){
            response.setResponse("Failed to Sort Schedule", HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // 해당 service에 대한 예외 처리
        response.setResponse("Success to Sort", sortedTimes, HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}