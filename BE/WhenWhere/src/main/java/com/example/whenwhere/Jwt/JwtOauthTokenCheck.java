package com.example.whenwhere.Jwt;

import com.example.whenwhere.Dto.TokenDto;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

// 토큰 유효성 검사 (카카오 토큰 만료 여부 판단)
public class JwtOauthTokenCheck {
    public boolean oauthTokenChecker(String token){
        // 1. header 생성
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        // 4. http 요청하기
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object> response = restTemplate.exchange(
                "https://kapi.kakao.com/v1/user/access_token_info",
                HttpMethod.GET,
                httpEntity,
                Object.class
        );

        if(response.getStatusCode().value() == 200){
            return true;
        }else{
            return false;
        }
    }

    public String getEmail(String accessToken) throws ParseException {
        try{
            // 토큰 정보로 유저 확인하기
            // 1. header 생성
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");
            httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

            HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

            // 4. http 요청하기
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.GET,
                    httpEntity,
                    String.class
            );

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(response.getBody());
            JSONObject accountObj = (JSONObject) jsonObject.get("kakao_account");

            return (String) accountObj.get("email");
        }catch(Exception e){
            System.out.println("이메일 체크 오류 : " + e);
            return null;
        }
    }
}

