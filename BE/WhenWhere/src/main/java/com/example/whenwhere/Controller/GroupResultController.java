package com.example.whenwhere.Controller;


import com.example.whenwhere.Dto.ApplyDto;
import com.example.whenwhere.Dto.GroupResultDto;
import com.example.whenwhere.Dto.ObjectDto;
import com.example.whenwhere.Service.GroupResultService;
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
@RequestMapping("/api/result")
public class GroupResultController {

    @Autowired
    private GroupResultService groupResultService;

    @Autowired
    private CustomExceptionHandler customExceptionHandler;

    @PostMapping("/add-result")
    public ResponseEntity<ObjectDto> apply(@RequestBody GroupResultDto groupResultDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try{
            // 유저를 호스트로 하는 그룹 생성 서비스 호출
            groupResultService.addResult(groupResultDto, authentication.getName());
            return new ResponseEntity<>(new ObjectDto(null, null), HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e);
            return new ResponseEntity<>(new ObjectDto(null, customExceptionHandler.getMessage(e)), customExceptionHandler.getStatus(e));
        }
    }

    @GetMapping("/get-result/{groupId}")
    @ResponseBody
    public ResponseEntity<ObjectDto> getResult(@PathVariable Integer groupId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 유저를 호스트로 하는 그룹 생성 서비스 호출
        try{
            GroupResultDto result = groupResultService.getResult(groupId, authentication.getName());
            return new ResponseEntity<>(new ObjectDto(result, null), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new ObjectDto(null, customExceptionHandler.getMessage(e)), customExceptionHandler.getStatus(e));
        }
    }
}
