package com.example.whenwhere.Controller;

import com.example.whenwhere.Dto.ResponseDto;
import com.example.whenwhere.Dto.UserDto;
import com.example.whenwhere.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/api/user/join")
    @ResponseBody
    public ResponseEntity<ResponseDto> Join(@RequestBody UserDto userDto){
        ResponseDto response = new ResponseDto();
        // 유저가 존재하는지부터 확인
        boolean isUser = userService.existedUser(userDto.getUserId());
        // 존재하면 회원가입 거부
        if(isUser){
            response.setResponse("User is Already Existed", HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        // 존재하지 않으면 회원가입 서비스 호출
        boolean result = userService.join(userDto);
        // 성공 실패 시 Error 응답
        if(!result){
            response.setResponse("Failed to Create User", HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        // 성공하면 생성했다는 응답 전송
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
