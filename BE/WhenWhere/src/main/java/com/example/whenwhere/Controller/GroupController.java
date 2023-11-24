package com.example.whenwhere.Controller;

import com.example.whenwhere.Dto.ApplyDto;
import com.example.whenwhere.Dto.GroupDto;
import com.example.whenwhere.Dto.ResponseDto;
import com.example.whenwhere.Service.ApplyService;
import com.example.whenwhere.Service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class GroupController {

    @Autowired
    private GroupService groupService;

    @PostMapping("/api/group/create-group/{host_id}")
    @ResponseBody
    public ResponseEntity<ResponseDto> createGroup(@PathVariable Integer host_id, @RequestBody GroupDto groupDto){
        ResponseDto response = new ResponseDto();
        // 유저를 호스트로 하는 그룹 생성 서비스 호출
        boolean result = groupService.create(host_id, groupDto);
        // 성공 실패 시 Error 응답
        if(!result){
            response.setResponse("Failed to Create Group", HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        // 성공하면 생성했다는 응답 전송
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
