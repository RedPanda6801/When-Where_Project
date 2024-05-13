package com.example.whenwhere.Controller;

import com.example.whenwhere.Dto.*;
import com.example.whenwhere.Entity.User;
import com.example.whenwhere.Jwt.JwtOauthTokenCheck;
import com.example.whenwhere.Jwt.TokenProvider;
import com.example.whenwhere.Repository.UserRepository;
import com.example.whenwhere.Service.UserService;
import com.example.whenwhere.Util.CustomExceptionHandler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Controller
public class OauthController {

    @Autowired
    private CustomExceptionHandler customExceptionHandler;

    @Autowired
    private UserRepository userRepository;

    private final TokenProvider tokenProvider;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public OauthController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder){
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @GetMapping("/oauth")
    public String oauthTest(){return "oauth";}

    // 해당 카카오 로그인한 회원이 우리 회원인지 확인
    // body에 카카오 엑세스 토큰을 넣고 요청
    @PostMapping("/api/oauth/sign-check")
    @ResponseBody
    public ResponseEntity<ObjectDto> signCheck(@RequestBody String accessToken){
        boolean isLogin = false;
        String jwt = null;
        try{
            // 토큰 유효성 검사
            JwtOauthTokenCheck jwtOauthTokenCheck = new JwtOauthTokenCheck();
            isLogin = jwtOauthTokenCheck.oauthTokenChecker(accessToken);
            // 카카오 토큰 만료 시 401 에러
            if(!isLogin){
                return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
            }

            // 카카오에서 토큰으로 이메일 가져오기
            String email = jwtOauthTokenCheck.getEmail(accessToken);
            if(email == null){
                return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
            }
            // 이메일 값으로 유저 정보 찾기
            Optional<User> userOptional = userRepository.findByUserId(email);
            // 유저를 찾았을 때 -> 로그인 성공
            // 아이디, 비밀번호에 대한 인증 토큰 생성
            // 유저가 있으면 회원이라는 표기를 true로 해주고 넘겨준다.
            if(userOptional.isPresent()){
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userOptional.get().getUserId(), "1234");

                Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                jwt = tokenProvider.createToken(authentication);
            }
            // 회원이 아니면 jwt를 null로 하고 넘긴다.
            return new ResponseEntity<>(new ObjectDto(new OauthDto(jwt, userOptional.get().getNickname()), null), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new ObjectDto(null,  customExceptionHandler.getMessage(e)), customExceptionHandler.getStatus(e));
        }
    }
}
