package com.example.whenwhere.Service;


import com.example.whenwhere.Dto.ScheduleDto;
import com.example.whenwhere.Entity.Schedule;
import com.example.whenwhere.Entity.User;
import com.example.whenwhere.Repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private UserService userService;
    public boolean add(ScheduleDto scheduleDto, Integer id){
        // Validation
        if(
            scheduleDto.getTitle() == null ||
            scheduleDto.getStartTime() == null ||
            scheduleDto.getEndTime() == null
        ){
            System.out.println("[Error] Bad Data Input");
            return false;
        }
        // time 값의 예외 처리
        LocalDateTime startTime = scheduleDto.getStartTime();
        LocalDateTime endTime = scheduleDto.getEndTime();
        // 1. time 의 Date 값은 일정해야 함(날짜 일정) -> End Time이 다음날로 넘어가는 일이 없도록 함
        if(startTime.getDayOfMonth() != endTime.getDayOfMonth()){
            System.out.println("[Error] Input Time Error 1");
            return false;
        }
        // 2. Start Time > End Time 에 대한 예외 처리
        if(startTime.getHour() == endTime.getHour()){
            // 분 까지만 예외처리 (최소 30분 스케줄을 잡아야 함)
            if(startTime.getMinute() >= endTime.getMinute() - 30){
                System.out.println("[Error] Input Time Error 2");
                return false;
            }
        }
        else if(startTime.getHour() > endTime.getHour()){
            System.out.println("[Error] Input Time Error 3");
            return false;
        }
        else if( // 최소 30분의 스케줄은 잡아야 함
                endTime.getHour() - startTime.getHour() == 1 &&
                Math.abs(startTime.getMinute() - endTime.getMinute()) > 30
        ){
            System.out.println("[Error] Input Time Error 4");
            return false;
        }

        // 매핑할 User 가져오기
        Optional<User> userOptional = userService.getUserById(id);
        if(!userOptional.isPresent()){
            System.out.println("[Error] User is Not Existed");
            return false;
        }
        User user = userOptional.get();

        // Dto -> Entity
        Schedule scheduleObj = new Schedule();
        scheduleObj = scheduleObj.toEntity(scheduleDto);
        // User도 추가
        scheduleObj.setUser(user);

        // Create 로직 수행
        try{
            scheduleRepository.save(scheduleObj);
        }catch(Exception e){
            System.out.println(String.format("[Error] %s", e));
            return false;
        }

        return true;
    }
}
