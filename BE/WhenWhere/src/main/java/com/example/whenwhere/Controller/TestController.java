package com.example.whenwhere.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {
    @GetMapping("/api/test")
    @ResponseBody
    public ResponseEntity<String> Test(){
        return ResponseEntity.ok("good Test!");
    }
}
