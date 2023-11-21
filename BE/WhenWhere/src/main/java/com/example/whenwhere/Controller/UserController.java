package com.example.whenwhere.Controller;

import com.example.whenwhere.Dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {

    @PostMapping("/api/user/join")
    @ResponseBody
    public ResponseEntity<String> Join(@RequestBody UserDto userDto){
        return ResponseEntity.ok("Good");
    }
}
