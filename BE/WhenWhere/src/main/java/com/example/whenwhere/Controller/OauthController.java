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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

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
    public String oauthTest(@RequestParam String groupId){return "oauth";}

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
            else{
                // 회원이 아니면 jwt를 null로 하고 넘긴다.
                return new ResponseEntity<>(new ObjectDto(new OauthDto(jwt, email), null), HttpStatus.OK);
            }
            return new ResponseEntity<>(new ObjectDto(new OauthDto(jwt, userOptional.get().getNickname()), null), HttpStatus.OK);
        }catch(Exception e){
            System.out.println("로그인 에러 : " + e);
            return new ResponseEntity<>(new ObjectDto(null,  customExceptionHandler.getMessage(e)), customExceptionHandler.getStatus(e));
        }
    }

    @GetMapping("/local/oauth2/code/kakao")
    public ModelAndView getAccessToken(@RequestParam("code") String code){
        String redirectUrl = "http://3.36.131.153:8080";
        System.out.println("AccessToken code = " + code);
        try{
            // 1. header 생성
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

            // 2. body 생성
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code"); //고정값
            params.add("client_id", "4f9a6df343dc0990e161654dbcc833e4");
            params.add("redirect_uri", redirectUrl + "/local/oauth2/code/kakao"); //등록한 redirect uri
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
            // ModelAndView에 리다이렉트 뷰 설정
            ModelAndView modelAndView = new ModelAndView();

            // 토큰 값이 잘 담겼으면 회원인지 확인
            if(tokenObj != null && tokenObj.getAccess_token() != null){
                System.out.println("token : " + tokenObj.getAccess_token());
                // Email 값 가져오기
                JwtOauthTokenCheck jwtOauthTokenCheck = new JwtOauthTokenCheck();
                String email = jwtOauthTokenCheck.getEmail(tokenObj.getAccess_token());

                // 이메일 값으로 유저 정보 찾기
                Optional<User> userOptional = userRepository.findByUserId(email);
                // 유저를 찾았을 때 -> 로그인 성공
                if(userOptional.isPresent()){
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userOptional.get().getUserId(), "1234");

                    Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    String jwt = tokenProvider.createToken(authentication);
                    redirectUrl = redirectUrl + "/api/apply/page?token=" + jwt;
                    // 리다이렉트 뷰 생성
                    RedirectView redirectView = new RedirectView(redirectUrl);
                    // ModelAndView에 리다이렉트 뷰 설정
                    modelAndView.setView(redirectView);
                    return modelAndView;
                }else{
                    // 회원이 아니면 회원가입 페이지로 이동
                    redirectUrl = redirectUrl + "/api/user/sign/page?email=" + email;
                    // 리다이렉트 뷰 생성
                    RedirectView redirectView = new RedirectView(redirectUrl);
                    // ModelAndView에 리다이렉트 뷰 설정
                    modelAndView.setView(redirectView);
                    return modelAndView;
                }
            }
            // 회원이 아니면 회원가입 페이지로 이동
            redirectUrl = redirectUrl + "/error";
            // 리다이렉트 뷰 생성
            RedirectView redirectView = new RedirectView(redirectUrl);
            // ModelAndView에 리다이렉트 뷰 설정
            modelAndView.setView(redirectView);
            return modelAndView;
        }catch(Exception e){
            System.out.println(e);
            redirectUrl = redirectUrl + "/error";
            ModelAndView modelAndView = new ModelAndView();
            // 리다이렉트 뷰 생성
            RedirectView redirectView = new RedirectView(redirectUrl);
            // ModelAndView에 리다이렉트 뷰 설정
            modelAndView.setView(redirectView);
            return modelAndView;
        }
    }
}
