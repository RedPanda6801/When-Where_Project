package com.example.whenwhere.Service;

import com.example.whenwhere.Config.SecurityConfig;
import com.example.whenwhere.Dto.UserDto;
import com.example.whenwhere.Entity.Authority;
import com.example.whenwhere.Entity.User;
import com.example.whenwhere.Repository.UserRepository;
import com.example.whenwhere.Util.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    SecurityConfig securityConfig;

    @Autowired
    private UserRepository userRepository;

    // 예외처리 중복을 방지하기 위해 Optional 값만 사용
    // 외부(Controller)에서 사용하지 않고 내부에서 별도로 사용하는 Service
    public Optional<User> getUserById(Integer id){
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional;
    }

    // DB에 항상 ROLE_USER와 ROLE_ADMIN이 존재해야 함
    @Transactional
    public boolean signup(UserDto userDto){
        // userDto의 필수 값 확인
        if(
                userDto.getUserId() == null ||
                userDto.getNickname() == null ||
                userDto.getPassword() == null
        ){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");
        }
        // 기존 유저에 대한 예외 처리
        if(userRepository.findOneWithAuthoritiesByUserId(userDto.getUserId()).orElse(null) != null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "USER_ALREADY_EXISTED");
        }
        Authority authority = Authority.builder()
                .authorityName("ROLE_USER").build();

        User user = User.builder()
                .userId(userDto.getUserId())
                .password(securityConfig.passwordEncoder().encode(userDto.getPassword()))
                .nickname(userDto.getNickname())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();

        // 서버 에러에 대한 예외처리
        try{
            userRepository.save(user);
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR");
        }
        return true;
    }

    @Transactional
    public Optional<User> getUserWithAuthorities(String userId){
        return userRepository.findOneWithAuthoritiesByUserId(userId);
    }

    @Transactional
    public Optional<User> getMyUserWithAuthorities(){
        return SecurityUtil.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByUserId);
    }
}
