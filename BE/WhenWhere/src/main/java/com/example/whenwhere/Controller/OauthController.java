package com.example.whenwhere.Controller;

import com.example.whenwhere.Dto.BodyDto;
import com.example.whenwhere.Dto.ObjectDto;
import com.example.whenwhere.Dto.TokenDto;
import com.example.whenwhere.Dto.UserDto;
import com.example.whenwhere.Jwt.JwtOauthTokenCheck;
import com.example.whenwhere.Service.UserService;
import com.example.whenwhere.Util.CustomExceptionHandler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Controller
public class OauthController {

    @Autowired
    private CustomExceptionHandler customExceptionHandler;

    @Autowired
    private UserService userService;

    @GetMapping("/oauth")
    public String oauthTest(){return "oauth";}

    // 해당 카카오 로그인한 회원이 우리 회원인지 확인
    // body에 카카오 엑세스 토큰을 넣고 요청
    @PostMapping("/oauth/sign-check")
    @ResponseBody
    public ResponseEntity<ObjectDto> signCheck(@RequestBody TokenDto tokenDto){
        boolean isLogin = false;

        try{
            // 토큰 유효성 검사
            JwtOauthTokenCheck jwtOauthTokenCheck = new JwtOauthTokenCheck();
            isLogin = jwtOauthTokenCheck.oauthTokenChecker(tokenDto.getToken());

            // 카카오 토큰 만료 시 401 에러
            if(!isLogin){
                return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
            }

            // 카카오에서 토큰으로 이메일 가져오기
            String email = jwtOauthTokenCheck.getEmail(tokenDto);
            if(email == null){
                return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
            }
            // 이메일 값으로 유저 정보 찾기
            UserDto userDto = userService.getUser(email);
            // 유저를 찾지 못했을 때 -> 로그인 실패 또는 회원 가입 창으로 redirection
            if(userDto == null){
                return new ResponseEntity<>(new ObjectDto(isLogin, "회원가입 필요 계정"), HttpStatus.OK);
            }
            // 유저를 찾았을 때 -> 로그인 성공
            String userEmail = userDto.getUserId();
            return new ResponseEntity<>(new ObjectDto(userEmail, null), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new ObjectDto(null,  customExceptionHandler.getMessage(e)), customExceptionHandler.getStatus(e));
        }
    }

    @GetMapping("/local/oauth2/code/kakao")
    @ResponseBody
    public ResponseEntity<ObjectDto> getAccessToken(@RequestParam("code") String code){

        System.out.println("AccessToken code = " + code);
        try{
            // 1. header 생성
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

            // 2. body 생성
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code"); //고정값
            params.add("client_id", "4f9a6df343dc0990e161654dbcc833e4");
            params.add("redirect_uri", "http://localhost:8080/local/oauth2/code/kakao"); //등록한 redirect uri
            params.add("code", code);

            // 3. header + body
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, httpHeaders);

            // 4. http 요청하기
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<BodyDto> response = restTemplate.exchange(
                    "https://kauth.kakao.com/oauth/token",
                    HttpMethod.POST,
                    httpEntity,
                    BodyDto.class
            );

            BodyDto tokenObj = response.getBody();
            // 우선은 액세스 토큰만 사용
            return new ResponseEntity<>(new ObjectDto(tokenObj.getAccess_token(), null), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new ObjectDto(null, customExceptionHandler.getMessage(e)), customExceptionHandler.getStatus(e));
        }
    }
}
