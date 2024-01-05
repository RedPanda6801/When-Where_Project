package com.example.whenwhere.Controller;

import com.example.whenwhere.Dto.*;
import com.example.whenwhere.Jwt.JwtFilter;
import com.example.whenwhere.Jwt.TokenProvider;
import com.example.whenwhere.Service.UserService;
import com.example.whenwhere.Util.CustomExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private CustomExceptionHandler customExceptionHandler;

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    public UserController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder){
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    // 회원가입 로직
    @PostMapping("/sign-up")
    @ResponseBody
    public ResponseEntity<String> signup(@RequestBody UserDto userDto){
        try{
            userService.signup(userDto);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch(Exception e) {
            return new ResponseEntity<>(customExceptionHandler.getMessage(e), customExceptionHandler.getStatus(e));
        }
    }

    // 로그인 로직
    @PostMapping("/auth")
    @ResponseBody
    public ResponseEntity<TokenDto> authorize(@Validated @RequestBody LoginDto loginDto){
        // 아이디, 비밀번호에 대한 인증 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUserId(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.createToken(authentication);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        return new ResponseEntity<>(new TokenDto(jwt), httpHeaders, HttpStatus.OK);
    }

    // 유저 개인정보 조회
    @GetMapping("/get-user")
    @ResponseBody
    public ResponseEntity<ObjectDto> getUser(){
        // 로그인 된 유저 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try{
            UserDto user = userService.getUser(authentication.getName());
            return new ResponseEntity<>(new ObjectDto(user, null), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new ObjectDto(null, customExceptionHandler.getMessage(e)), customExceptionHandler.getStatus(e));
        }
    }

    @PostMapping("/modify")
    @ResponseBody
    public ResponseEntity<String> modifyUser(@RequestBody UserDto userDto){
        // 로그인 된 유저 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try{
            userService.modify(userDto, authentication.getName());
            return new ResponseEntity<>(HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(customExceptionHandler.getMessage(e), customExceptionHandler.getStatus(e));
        }
    }

    // Hard Delete
    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<String> deleteUser(){
        // 로그인 된 유저 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try{
            userService.delete(authentication.getName());
            return new ResponseEntity<>(HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(customExceptionHandler.getMessage(e), customExceptionHandler.getStatus(e));
        }
    }
}
