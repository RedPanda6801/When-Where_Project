package com.example.whenwhere.Service;

import com.example.whenwhere.Dto.ApplyDto;
import com.example.whenwhere.Dto.GroupDto;
import com.example.whenwhere.Entity.Apply;
import com.example.whenwhere.Entity.Group;
import com.example.whenwhere.Entity.User;
import com.example.whenwhere.Repository.ApplyRepository;
import com.example.whenwhere.Repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GroupService {
    @Autowired
    private UserService userService;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private ApplyRepository applyRepository;


    public Optional<Group> getGroupById(Integer id){
        return groupRepository.findById(id);
    }

    public boolean create(Integer id, GroupDto groupDto){
        // Validation
        if(groupDto.getGroupName() == null){
            System.out.println("[Error] Bad Data Input");
            return false;
        }

        // 유저 가져오기
        Optional<User> userOptional = userService.getUserById(id);
        // 유저가 없으면 예외처리
        if(userOptional.isEmpty()){
            return false;
        }
        User host = userOptional.get();

        // Dto -> Entity
        Group group = new Group();
        group = group.toEntity(groupDto);
        group.setHost(host);

        // DB에 저장하는 로직 호출
        try{
            groupRepository.save(group);
        }catch(Exception e){
            // 404 에러로 처리하되, 서버에 로그 저장
            System.out.println(String.format("[Error] %s", e));
            return false;
        }

        return true;

    }
}
