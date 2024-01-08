package com.example.whenwhere.Controller;

import com.example.whenwhere.Dto.ApplyDto;
import com.example.whenwhere.Dto.ObjectDto;
import com.example.whenwhere.Dto.ResponseDto;
import com.example.whenwhere.Service.ApplyService;
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
@RequestMapping("/api/apply")
public class ApplyController {

    @Autowired
    private ApplyService applyService;

    @Autowired
    private CustomExceptionHandler customExceptionHandler;

    //  지원 전에 로그인 상태여야 한다.
    @PostMapping("/apply-group")
    @ResponseBody
    public ResponseEntity<String> apply(@RequestBody ApplyDto applyDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try{
            // 유저를 호스트로 하는 그룹 생성 서비스 호출
            applyService.apply(applyDto, authentication.getName());
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(customExceptionHandler.getMessage(e), customExceptionHandler.getStatus(e));
        }
    }

    @GetMapping("/get-apply/{groupId}")
    @ResponseBody
    public ResponseEntity<ObjectDto> getApplies(@PathVariable Integer groupId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 유저를 호스트로 하는 그룹 생성 서비스 호출
        try{
            List<Object> applies = applyService.getAllApplyByGroup(groupId, authentication.getName());
            return new ResponseEntity<>(new ObjectDto(applies, null), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new ObjectDto(null, customExceptionHandler.getMessage(e)), customExceptionHandler.getStatus(e));
        }
    }

    @PostMapping("/process-apply")
    @ResponseBody
    public ResponseEntity<String> processApply(@RequestBody ApplyDto applyDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 해당 apply에 대한 처리 서비스 호출
        try{
            applyService.process(applyDto, authentication.getName());
            return new ResponseEntity<>(HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(customExceptionHandler.getMessage(e), customExceptionHandler.getStatus(e));
        }
    }
}
