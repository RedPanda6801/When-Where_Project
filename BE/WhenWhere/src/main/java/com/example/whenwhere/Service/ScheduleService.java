package com.example.whenwhere.Service;


import com.example.whenwhere.Dto.ScheduleDto;
import com.example.whenwhere.Entity.Schedule;
import com.example.whenwhere.Entity.User;
import com.example.whenwhere.Repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
