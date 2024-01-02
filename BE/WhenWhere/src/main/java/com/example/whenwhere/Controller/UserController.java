package com.example.whenwhere.Controller;

import com.example.whenwhere.Dto.LoginDto;
import com.example.whenwhere.Dto.ResponseDto;
import com.example.whenwhere.Dto.TokenDto;
import com.example.whenwhere.Dto.UserDto;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/api/user")
public class UserController {

    private final TokenProvider tokenProvider;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomExceptionHandler customExceptionHandler;

    public UserController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder){
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @PostMapping("/signup")
    @ResponseBody
    public ResponseEntity<String> signup(@RequestBody UserDto userDto){
        ResponseDto response = new ResponseDto();
        // 존재하지 않으면 회원가입 서비스 호출
        try{
            userService.signup(userDto);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch(Exception e) {
            return new ResponseEntity<String>(customExceptionHandler.getMessage(e), customExceptionHandler.getStatus(e));
        }
    }

    @PostMapping("/auth")
    @ResponseBody
    public ResponseEntity<TokenDto> authorize(@Validated @RequestBody LoginDto loginDto){
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUserId(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.createToken(authentication);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        return new ResponseEntity<>(new TokenDto(jwt), httpHeaders, HttpStatus.OK);
    }

}
