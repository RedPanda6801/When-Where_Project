package com.example.whenwhere.Controller;

import com.example.whenwhere.Dto.*;
import com.example.whenwhere.Entity.Group;
import com.example.whenwhere.Service.GroupService;
import com.example.whenwhere.Util.CustomExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private CustomExceptionHandler customExceptionHandler;

    @GetMapping("/get-my-groups")
    @ResponseBody
    public ResponseEntity<ObjectDto> getMyGroups(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try{
            List<GroupDto> groups = groupService.getMyGroups(authentication.getName());
            return new ResponseEntity<>(new ObjectDto(groups, null), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new ObjectDto(null, customExceptionHandler.getMessage(e)), customExceptionHandler.getStatus(e));
        }
    }

    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<ObjectDto> createGroup( @RequestBody GroupDto groupDto){
        // 세션을 유지하고 있는 유저의 아이디를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try{
            // 유저를 호스트로 하는 그룹 생성 서비스 호출
            groupService.create(groupDto, authentication.getName());
            return new ResponseEntity<>(new ObjectDto(null, "created"), HttpStatus.CREATED);
        }catch (Exception e){
            System.out.println(e);
            return new ResponseEntity<>(new ObjectDto(null, customExceptionHandler.getMessage(e)), customExceptionHandler.getStatus(e));
        }
    }

    // 속한 그룹이 여러 개일 수 있으므로 파라미터로 그룹 PK를 받아옴
    @GetMapping("/get-members/{group_id}")
    @ResponseBody
    public ResponseEntity<ObjectDto> getmembersInGroup(@PathVariable Integer group_id){
        try{
            // member들을 가져오는 서비스 호출
            List<UserDto> members = groupService.getMembers(group_id);
            return new ResponseEntity<>(new ObjectDto(members, null), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new ObjectDto(null, customExceptionHandler.getMessage(e)), customExceptionHandler.getStatus(e));
        }
    }

    @PostMapping("/modify")
    @ResponseBody
    public ResponseEntity<ObjectDto> modifyGroup(@RequestBody GroupDto groupDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 그룹 서비스에서 수정 로직 호출
        try{
            groupService.modify(groupDto, authentication.getName());
            return new ResponseEntity<>(new ObjectDto("Modify Success!", null), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new ObjectDto(null, customExceptionHandler.getMessage(e)), customExceptionHandler.getStatus(e));
        }
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<ObjectDto> deleteGroup(@RequestBody GroupDto groupDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 그룹 서비스에서 삭제 로직 호출
        try{
            groupService.delete(groupDto, authentication.getName());
            return new ResponseEntity<>(new ObjectDto(null, null), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new ObjectDto(null, customExceptionHandler.getMessage(e)), customExceptionHandler.getStatus(e));
        }
    }

    // 그룹에서 내보내기
    @PostMapping("/emit")
    @ResponseBody
    public ResponseEntity<ObjectDto> emitToGroup(@RequestBody ApplyDto emitDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 그룹에서 해당 그룹원 방출 로직 호출
        try{
            groupService.emit(emitDto, authentication.getName());
            return new ResponseEntity<>(new ObjectDto(null, null), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new ObjectDto(null, customExceptionHandler.getMessage(e)), customExceptionHandler.getStatus(e));
        }
    }

    @PostMapping("/exit")
    @ResponseBody
    public ResponseEntity<ObjectDto> exitToGroup(@PathVariable Integer groupId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 그룹에서 해당 그룹원 방출 로직 호출
        try{
            groupService.exit(groupId, authentication.getName());
            return new ResponseEntity<>(new ObjectDto(null, null), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new ObjectDto(null, customExceptionHandler.getMessage(e)), customExceptionHandler.getStatus(e));
        }
    }
}
