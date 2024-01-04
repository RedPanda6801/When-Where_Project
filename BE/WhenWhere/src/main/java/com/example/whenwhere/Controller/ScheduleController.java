package com.example.whenwhere.Controller;

import com.example.whenwhere.Dto.BusytimeDto;
import com.example.whenwhere.Dto.ObjectDto;
import com.example.whenwhere.Dto.ResponseDto;
import com.example.whenwhere.Dto.ScheduleDto;
import com.example.whenwhere.Entity.Schedule;
import com.example.whenwhere.Service.ScheduleService;
import com.example.whenwhere.Util.CustomExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/schedule")
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private CustomExceptionHandler customExceptionHandler;

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<String> addSchedule(@RequestBody ScheduleDto scheduleDto){
        try{
            // 세션을 유지하고 있는 유저의 아이디를 가져옴
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            // 스케줄 추가 로직 구현
            scheduleService.add(scheduleDto, authentication.getName());
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch(Exception e){
            return new ResponseEntity<String>(customExceptionHandler.getMessage(e), customExceptionHandler.getStatus(e));
        }

    }

    // 빈 시간 계산 API
    @PostMapping("/busytime-group")
    @ResponseBody
    public ResponseEntity<ObjectDto> busyTimeInGroupSchedule(@RequestBody BusytimeDto busytimeDto){
        // members의 schedule을 날짜 별로 정렬하여 가져오는 service
        try{
            List<Schedule> sortedTimes =  scheduleService.getSortedTimesByDates(busytimeDto);
            return new ResponseEntity<>(new ObjectDto(sortedTimes, null), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new ObjectDto(null, customExceptionHandler.getMessage(e)), customExceptionHandler.getStatus(e));
        }
    }
}
