package com.example.whenwhere.Service;

import com.example.whenwhere.Dto.ApplyDto;
import com.example.whenwhere.Entity.Apply;
import com.example.whenwhere.Entity.Group;
import com.example.whenwhere.Entity.User;
import com.example.whenwhere.Repository.ApplyRepository;
import com.example.whenwhere.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApplyService {

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private ApplyRepository applyRepository;

    @Autowired
    private UserRepository userRepository;

    public boolean apply(Integer id, ApplyDto applyDto){
        // Validation
        if(
                applyDto.getApplyGroupId() == null){
            System.out.println("[Error] Bad Data Input");
            return false;
        }

        // 유저 가져오기
        Optional<User> userOptional = userService.getUserById(id);
        // 유저가 없으면 예외처리
        if(userOptional.isEmpty()){
            return false;
        }
        User applier = userOptional.get();

        // 그룹 가져오기
        Optional<Group> groupOptional = groupService.getGroupById(applyDto.getApplyGroupId());
        if(groupOptional.isEmpty()){
            System.out.println("[Error] Group is Not Existed");
            return false;
        }
        Group group = groupOptional.get();

        // applier == host이면 예외 처리
        if(group.getHost().getId() == applier.getId()){
            System.out.println("[Error] You are Host");
            return false;
        }

        // Apply 매핑 및 생성
        Apply apply = new Apply();
        apply.setApplier(applier);
        apply.setGroup(group);

        // apply 중복 제거 필요

        try{
            // 비지니스 로직 호출
            applyRepository.save(apply);
        }catch(Exception e){
            // 404 에러로 처리하되, 서버에 로그 저장
            System.out.println(String.format("[Error] %s", e));
            return false;
        }

        return true;
    }

    public List<Object> getAllByGroup(Integer hostId, Integer groupId){
        // Validation
        if(hostId == null || groupId == null){
            // 예외처리 반드시 필요
            System.out.println("[Error] Bad Data Input");
            return null;
        }
        // group 찾기
        try{
            Optional<Group> groupOptional = groupService.getGroupById(groupId);
            if(groupOptional.isEmpty()){
                System.out.println("[Error] Group is Not Existed");
                return null;
            }
            Group group = groupOptional.get();
            // 찾은 group과 host를 확인
            if(group.getHost().getId() != hostId){
                System.out.println("[Error] Forbidden Error");
                return null;
            }

            // 비지니스 로직 호출 (group에 맞는 Applier 조인)
            List<Object> applies = userRepository.findAllUserByGroupId(group.getId());
            // 리턴
            return applies;
        }catch(Exception e){
            // 404 에러로 처리하되, 서버에 로그 저장
            System.out.println(String.format("[Error] %s", e));
            return null;
        }

    }
}
