package com.example.whenwhere.Service;

import com.example.whenwhere.Dto.GroupDto;
import com.example.whenwhere.Entity.Authority;
import com.example.whenwhere.Entity.Group;
import com.example.whenwhere.Entity.GroupMembers;
import com.example.whenwhere.Entity.User;
import com.example.whenwhere.Repository.GroupMembersRepository;
import com.example.whenwhere.Repository.GroupRepository;
import com.example.whenwhere.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupMembersRepository groupMembersRepository;

    public Optional<Group> getGroupById(Integer id){
        return groupRepository.findById(id);
    }

    @Transactional
    public void create(GroupDto groupDto, String userId){
        // Validation
        if(groupDto.getGroupName() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");
        }

        // 유저 가져오기
        Optional<User> userOptional = userRepository.findByUserId(userId);
        // 유저가 없으면 예외처리
        if(!userOptional.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "USER NOT EXISTED");
        }
        User host = userOptional.get();

        // Dto -> Entity
        Group group = new Group();
        group = group.toEntity(groupDto);
        group.setHost(host);


        // DB에 저장하는 로직 호출
        try{
            // 권한 부여
            Authority authority = Authority.builder()
                    .authorityName("ROLE_HOST").build();

            //기존 권한에 추가
            Set<Authority> authorities = host.getAuthorities();
            authorities.add(authority);

            host.updateAuthority(authorities);

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

    public List<Object> getMembers(Integer groupId){
        // Validation
        if(groupId == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");
        }
        try{
            // 비지니스 로직(group의 member 가져오기)
            List<Object> members = userRepository.findMembersByGroup(groupId);

            return members;

        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR");
        }
    }

}
