package com.example.whenwhere.Controller;

import com.example.whenwhere.Dto.BodyDto;
import com.example.whenwhere.Dto.ObjectDto;
import com.example.whenwhere.Dto.TokenDto;
import com.example.whenwhere.Util.CustomExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@Controller
public class OauthController {

    @Autowired
    private CustomExceptionHandler customExceptionHandler;

    @GetMapping("/oauth")
    public String oauthTest(){return "oauth";}

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
