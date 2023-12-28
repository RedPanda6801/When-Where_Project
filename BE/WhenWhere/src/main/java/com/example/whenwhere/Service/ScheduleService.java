package com.example.whenwhere.Service;

import com.example.whenwhere.Dto.BusytimeDto;
import com.example.whenwhere.Dto.ScheduleDto;
import com.example.whenwhere.Entity.Schedule;
import com.example.whenwhere.Entity.User;
import com.example.whenwhere.Repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    public List<Schedule> getSortedTimesByDates(BusytimeDto busytimeDto){
        List<Schedule> result = new ArrayList<>();
        Schedule scheduleTmp = null;

        try{// 지정된 날짜에 대한 유저들의 스케줄을 뽑아오기
            List<Schedule> schedules =
                scheduleRepository.findSortedScheduleByDates(
                busytimeDto.getMembers(),
                busytimeDto.getStartDate(),
                busytimeDto.getEndDate());
            for(int i = 0; i< schedules.size(); i++){
                System.out.println(schedules.get(i).getEndTime());
            }
            // 뽑아온 스케줄을 필터링하는 알고리즘
            for(int i = 0; i < schedules.size(); i++){
                // 초기값 세팅
                if(scheduleTmp == null){
                    scheduleTmp = new Schedule();
                    scheduleTmp.setStartTime(schedules.get(i).getStartTime());
                    scheduleTmp.setEndTime(schedules.get(i).getEndTime());
                    // 마지막 값도 넣어주기
                    if(i == schedules.size()-1){
                        result.add(scheduleTmp);
                    }
                    continue;
                }
                // 날짜가 바뀌면 결과를 넣고 초기화
                if(scheduleTmp.getStartTime().getDayOfMonth() != schedules.get(i).getStartTime().getDayOfMonth()){
                    result.add(scheduleTmp.clone());
                    // 마지막 값도 넣어주기
                    if(i == schedules.size()-1){
                        scheduleTmp.setStartTime(schedules.get(i).getStartTime());
                        scheduleTmp.setEndTime(schedules.get(i).getEndTime());
                        result.add(scheduleTmp);
                    }
                    scheduleTmp = null;
                    continue;
                }

                // 이전 스케줄과 현재 스캐줄의 시간이 곂치면
                if(scheduleTmp.getEndTime().isAfter(schedules.get(i).getStartTime()) ||
                    scheduleTmp.getEndTime().isEqual(schedules.get(i).getStartTime())){
                    // 현재 객체의 끝시간만 늘려줌
                    scheduleTmp.setEndTime(schedules.get(i).getEndTime());
                }
                // 스케줄이 곂치지 않는다면 이전 스케줄 저장 후 다음 스케줄 계산
                else if(scheduleTmp.getEndTime().isBefore(schedules.get(i).getStartTime())){
                    result.add(scheduleTmp.clone());
                    scheduleTmp.setStartTime(schedules.get(i).getStartTime());
                    scheduleTmp.setEndTime(schedules.get(i).getEndTime());
                }
                // 마지막 값도 넣어주기
                if(i == schedules.size()-1){
                    result.add(scheduleTmp);
                }
            }
        }catch(Exception e){
            System.out.println(String.format("[Error] %s", e));
            return null;
        }
        return result;
    }
}
