package com.example.whenwhere.Controller;

import com.example.whenwhere.Dto.ObjectDto;
import com.example.whenwhere.Dto.TokenDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class TestController {
    @GetMapping("/api/test")
    @ResponseBody
    public ResponseEntity<ObjectDto> Test(){
        return new ResponseEntity<>(new ObjectDto(null, "good Test"), HttpStatus.OK);
    }
}
