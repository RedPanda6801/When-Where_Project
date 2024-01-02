package com.example.whenwhere.Controller;

import com.example.whenwhere.Dto.ApplyDto;
import com.example.whenwhere.Dto.ResponseDto;
import com.example.whenwhere.Service.ApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/apply")
public class ApplyController {

    @Autowired
    private ApplyService applyService;
    //  지원 전에 로그인 상태여야 한다.
    @PostMapping("/apply-group/{user_id}")
    @ResponseBody
    public ResponseEntity<ResponseDto> applyGroup(@PathVariable Integer user_id, @RequestBody ApplyDto applyDto){
        ResponseDto response = new ResponseDto();
        // 유저를 호스트로 하는 그룹 생성 서비스 호출
        boolean result = applyService.apply(user_id, applyDto);
        // 성공 실패 시 Error 응답
        if(!result){
            response.setResponse("Failed to Apply Group", HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        // 성공하면 생성했다는 응답 전송
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/get-apply/{host_id}/{group_id}")
    @ResponseBody
    public ResponseEntity<ResponseDto> getApplies(@PathVariable Integer host_id, @PathVariable Integer group_id){
        ResponseDto response = new ResponseDto();
        // 유저를 호스트로 하는 그룹 생성 서비스 호출
        List<Object> applies = applyService.getAllApplyByGroup(host_id, group_id);
        // 성공 실패 시 Error 응답
        if(applies == null){
            response.setResponse("Failed to Get Group Applies", HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        // 가져온 apply를 응답값에 넣기
        response.setResponse("Get Applies Success", applies, HttpStatus.OK);
        // 성공하면 생성했다는 응답 전송
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/process-apply/{host_id}")
    @ResponseBody
    public ResponseEntity<ResponseDto> processApply(@PathVariable Integer host_id, @RequestBody ApplyDto applyDto){
        ResponseDto response = new ResponseDto();
        // 해당 apply에 대한 처리 서비스 호출
        boolean result =  applyService.process(host_id, applyDto);
        // 성공 실패 시 Error 응답
        if(!result){
            response.setResponse("Failed to Apply Group", HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        // 성공하면 생성했다는 응답 전송
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
