package com.example.whenwhere.Service;

import com.example.whenwhere.Dto.ApplyDto;
import com.example.whenwhere.Entity.*;
import com.example.whenwhere.Repository.ApplyRepository;
import com.example.whenwhere.Repository.GroupMembersRepository;
import com.example.whenwhere.Repository.UserRepository;
import com.example.whenwhere.Util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ApplyService {

    @Autowired
    private GroupService groupService;

    @Autowired
    private ApplyRepository applyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupMembersRepository groupMembersRepository;

    public void apply(ApplyDto applyDto, String userId){
        // Validation
        if(
            applyDto.getApplyGroupId() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");
        }
        // 유저 가져오기
        Optional<User> userOptional = userRepository.findByUserId(userId);
        if(!userOptional.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "USER_NOT_EXISTED");
        }
        // 그룹 가져오기
        Optional<Group> groupOptional = groupService.getGroupById(applyDto.getApplyGroupId());
        if(!groupOptional.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "GROUP_NOT_EXISTED");
        }

        User applier = userOptional.get();
        Group group = groupOptional.get();

        // 그룹 중복 지원 예외 처리
        List<Integer> ids = applyRepository.findApplierInGroup(applier.getId(), group.getId());
        if(ids.size() > 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ALREADY_APPLIED");
        }

        // 호스트면 이용 불가
        if(SecurityUtil.checkRole(SecurityContextHolder.getContext().getAuthentication(), "ROLE_HOST")){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "HOST_NOT_AVAILABLE");
        }
        try{

            // Apply 매핑 및 생성
            Apply apply = new Apply();
            apply.setApplier(applier);
            apply.setGroup(group);
            apply.setState(false);
            apply.setAccepted(false);

            // 비지니스 로직 호출
            applyRepository.save(apply);
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR");
        }
    }

    public List<Object> getAllApplyByGroup(Integer groupId, String hostId){
        // Validation
        if(hostId == null || groupId == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");
        }
        // group 찾기
        Optional<Group> groupOptional = groupService.getGroupById(groupId);
        if(groupOptional.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "GROUP_NOT_EXISTED");
        }

        Optional<User> userOptional = userRepository.findByUserId(hostId);
        if(!userOptional.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "USER NOT EXISTED");
        }

        User host = userOptional.get();
        Group group = groupOptional.get();
        // 찾은 group과 host를 확인
        if(!SecurityUtil.checkRole(SecurityContextHolder.getContext().getAuthentication(), "ROLE_HOST")
            || group.getHost().getId() != host.getId()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "FORBIDDEN_ERROR");
        }
        try{
            // 비지니스 로직 호출 (group에 맞는 Applier 조인)
            List<Object> applies = userRepository.findAllUserByGroupId(group.getId());
            return applies;
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR");
        }

    }

    @Transactional
    public void process(ApplyDto applyDto, String hostId){
        // Validation
        if(hostId == null || applyDto.getId() == null || applyDto.getDecide() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");
        }
        // apply 가져오기
        Optional<Apply> applyOptional = applyRepository.findById(applyDto.getId());
        if(!applyOptional.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "APPLY_NOT_EXISTED");
        }
        // 호스트 가져오기
        Optional<User> userOptional = userRepository.findByUserId(hostId);
        if(!userOptional.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "USER NOT EXISTED");
        }

        Apply apply = applyOptional.get();
        // apply에 있는 그룹의 호스트가 요청자가 아니면 예외처리
        if(!SecurityUtil.checkRole(SecurityContextHolder.getContext().getAuthentication(), "ROLE_HOST")
                || apply.getGroup().getHost().getId() != userOptional.get().getId()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "FORBIDDEN_ERROR");
        }
        // 처리 상태에 대한 예외 처리
        if(apply.getState()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "APPLY_ALREADY_PROCESSED");
        }
        try{
            // 처리 여부에 True
            apply.setState(true);

            // 반려될 경우
            if(!applyDto.getDecide()){
                apply.setAccepted(false);
                applyRepository.save(apply);
            }else{ // 승인될 경우
                apply.setAccepted(true);
                // 멤버 추가 & apply 수정
                GroupMembers membersObj = new GroupMembers();
                membersObj = membersObj.toEntity(apply);
                applyRepository.save(apply);
                groupMembersRepository.save(membersObj);
            }
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR");
        }
    }
}
