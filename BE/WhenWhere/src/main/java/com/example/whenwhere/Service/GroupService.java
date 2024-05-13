package com.example.whenwhere.Service;

import com.example.whenwhere.Dto.ApplyDto;
import com.example.whenwhere.Dto.GroupDto;
import com.example.whenwhere.Dto.UserDto;
import com.example.whenwhere.Entity.*;
import com.example.whenwhere.Repository.ApplyRepository;
import com.example.whenwhere.Repository.GroupMembersRepository;
import com.example.whenwhere.Repository.GroupRepository;
import com.example.whenwhere.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupMembersRepository groupMembersRepository;

    @Autowired
    private ApplyRepository applyRepository;

    public List<GroupDto> getMyGroups(String userId){
        // 유저 가져오기
        Optional<User> userOptional = userRepository.findByUserId(userId);
        if(userOptional.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "USER_NOT_EXISTED");
        }

        try{
            // 유저의 PK로 유저의 그룹을 모두 가져오기
            List<GroupDto> myGroups = groupMembersRepository.findAllByUserPk(userOptional.get().getId());
            return myGroups;
        }catch(Exception e){
            System.out.println(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR");
        }
    }

    @Transactional
    public void create(GroupDto groupDto, String userId){
        // Validation
        if(groupDto == null || groupDto.getGroupName() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");
        }

        // 유저 가져오기
        User host = userRepository.findByUserId(userId).get();

        // 그룹 이름 중복 체크
        Optional<Integer> groupPkOptional = groupRepository.findByGroupName(groupDto.getGroupName());
        if(groupPkOptional.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "GROUPNAME_DUPLICATION_ERROR");
        }


        // Dto -> Entity
        Group group = new Group();
        group = group.toEntity(groupDto);
        group.setHost(host);


        // DB에 저장하는 로직 호출
        try{
            boolean isHost = false;
            //기존 권한에 추가
            Set<Authority> authorities = host.getAuthorities();
            for(Authority authority : authorities){
                if(authority.getAuthorityName().equals("ROLE_HOST")){
                    isHost = true;
                    break;
                }
            }
            if(!isHost){
                // 권한 부여
                Authority authority = Authority.builder()
                        .authorityName("ROLE_HOST").build();

                authorities.add(authority);

                host.updateAuthority(authorities);
            }

            Group created = groupRepository.save(group);

            // 그룹장도 그룹에 가입되어 있어야 함
            GroupMembers membersObj = new GroupMembers();
            membersObj.setGroup(created);
            membersObj.setUser(host);
            membersObj.setGroupName(groupDto.getGroupName());

            groupMembersRepository.save(membersObj);
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR");
        }
    }

    public List<UserDto> getMembers(Integer groupId){
        // Validation
        if(groupId == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");
        }
        try{
            // 비지니스 로직(group의 member 가져오기)
            List<UserDto> members = userRepository.findMembersByGroup(groupId);
            return members;

        }catch(Exception e){
            System.out.println(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR");
        }
    }

    @Transactional
    public void modify(GroupDto groupDto, String userId){
        // VALIDATION
        if(groupDto == null || groupDto.getId() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");
        }
        // 유저와 그룹 가져오기
        User user = userRepository.findByUserId(userId).get();
        Optional<Group> groupOptional = groupRepository.findById(groupDto.getId());
        if(!groupOptional.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "GROUP_NOT_EXISTED");
        }
        Group group = groupOptional.get();

        // 유저가 그룹의 호스트인지 확인
        if(group.getHost().getId() != user.getId()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "FORBIDDEN_ERROR");
        }

        // 그룹 이름 중복 체크
        Optional<Integer> groupPkOptional = groupRepository.findByGroupName(groupDto.getGroupName());
        if(groupPkOptional.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "GROUPNAME_DUPLICATION_ERROR");
        }

        // 수정 시작
        try{
            // Dto에서 필터링
            String newGroupName = (groupDto.getGroupName() == null || groupDto.getGroupName().equals("")) ?
                    group.getGroupName() : groupDto.getGroupName();
            String newAttribute = (groupDto.getAttribute() == null || groupDto.getAttribute().equals("")) ?
                    group.getAttribute() : groupDto.getAttribute();
            // 비지니스 로직 호출
            group.update(newGroupName, newAttribute);
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR");
        }
    }

    @Transactional
    public void delete(GroupDto groupDto, String userId){
        // VALIDATION
        if(groupDto == null || groupDto.getId() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");
        }

        // 유저와 그룹 가져오기
        User user = userRepository.findByUserId(userId).get();
        Optional<Group> groupOptional = groupRepository.findById(groupDto.getId());
        if(groupOptional.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "GROUP_NOT_EXISTED");
        }
        Group group = groupOptional.get();

        // 유저가 그룹의 호스트여야 삭제가 가능
        if(!group.getHost().getId().equals(user.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "FORBIDDEN_ERROR");
        }

        try{
            // 그룹의 지원서 모두 삭제
            List<Apply> applies = applyRepository.findAllByGroupId(group.getId());
            for(Apply apply : applies){
                if(apply == null) continue;
                apply.setGroup(null);
                apply.setApplier(null);
                apply.setGroup(null);
                applyRepository.delete(apply);
            }

            // 그룹 멤버 삭제
            List<GroupMembers> groupMembers = groupMembersRepository.findAllByGroupId(group.getId());
            for(GroupMembers member : groupMembers){
                if(member == null) continue;
                member.setGroup(null);
                member.setUser(null);
                groupMembersRepository.delete(member);
            }

            // 그룹 삭제
            group.setGroupMembers(null);
            group.setHost(null);
            group.setApplies(null);
            groupRepository.delete(group);
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR");
        }
    }

    @Transactional
    public void emit(ApplyDto emitDto, String userId){
        // VALIDATION
        if(emitDto == null || emitDto.getApplyGroupId() == null || emitDto.getId() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");
        }

        // 방출할 사람과 그에 맞는 그룹 가져오기
        Optional<User> userOptional = userRepository.findById(emitDto.getId());
        Optional<Group> groupOptional = groupRepository.findById(emitDto.getApplyGroupId());
        if(userOptional.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "USER_NOT_EXISTED");
        }
        if(groupOptional.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "GROUP_NOT_EXISTED");
        }
        User user = userOptional.get();
        Group group = groupOptional.get();

        // 호스트 가져와서 그룹장인지 확인하기
        User host = userRepository.findByUserId(userId).get();
        if(!group.getHost().getId().equals(host.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "FORBIDDEN_ERROR");
        }
        // 호스트는 내보내기 불가
        if(host.getId().equals(user.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "HOST_CANNOT_EMIT");
        }

        try{
            groupMembersRepository.deleteMember(group.getId(), user.getId());
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR");
        }
    }

    @Transactional
    public void exit(Integer groupId, String userId){
        // VALIDATION
        if(groupId == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");
        }
        // Group 찾기 및 유저 찾기
        Optional<User> userOptional = userRepository.findByUserId(userId);
        Optional<Group> groupOptional = groupRepository.findById(groupId);
        if(userOptional.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "USER_NOT_EXISTED");
        }
        if(groupOptional.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "GROUP_NOT_EXISTED");
        }
        User user = userOptional.get();
        Group group = groupOptional.get();

        if(Objects.equals(user.getId(), group.getHost().getId())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "HOST_CANNOT_EXIT");
        }

        try{
            groupMembersRepository.deleteMember(group.getId(), user.getId());
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR");
        }
    }
}
