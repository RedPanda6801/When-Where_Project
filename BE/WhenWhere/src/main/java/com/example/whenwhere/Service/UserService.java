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
    public void signup(UserDto userDto){
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
                // 실제 비밀번호는 카카오 측에 있으므로 Security 세팅을 위한 임시 비밀번호만 설정
                .password(securityConfig.passwordEncoder().encode("1234"))
                .nickname(userDto.getNickname())
                .location(userDto.getLocation())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();

        // 서버 에러에 대한 예외처리
        try{
            userRepository.save(user);
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR");
        }
    }

    @Transactional
    public Optional<User> getUserWithAuthorities(String userId){
        return userRepository.findOneWithAuthoritiesByUserId(userId);
    }

    @Transactional
    public Optional<User> getMyUserWithAuthorities(){
        return SecurityUtil.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByUserId);
    }

    public UserDto getUser(String userId){
        try{
            Optional<User> userOptional = userRepository.findByUserId(userId);
            // 없을 때의 에러 처리
            if(userOptional.isEmpty()){
                return null;
            }
            // 있으면 비밀번호 미공개 이후 넘겨주기
            User user = userOptional.get();
            user.setPassword(null);
            return UserDto.toDto(user);
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR");
        }
    }
    @Transactional
    public void modify(UserDto userDto, String userId){
        // VALIDATION CHECK
        if(userDto.getNickname().equals("") && userDto.getLocation().equals("") && userDto.getPassword().equals("")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");
        }
        try{
            // 이미 시큐리티에서 로그인 여부를 판단했기 때문에 바로 가져옴 -> 예외 발생 시 서버 에러
            User user = userRepository.findByUserId(userId).get();
            // 필터링
            String newPassword = (userDto.getPassword() == null || userDto.getPassword().equals("")) ?
                    user.getPassword() : securityConfig.passwordEncoder().encode(userDto.getPassword());
            String newNickname = (userDto.getNickname() == null || userDto.getNickname().equals("")) ?
                    user.getNickname() : userDto.getNickname();
            String newLocation = (userDto.getLocation() == null || userDto.getLocation().equals("")) ?
                    user.getLocation(): userDto.getLocation();
            // 비지니스 로직 호출
            user.update(newPassword, newNickname, newLocation);
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR");
        }
    }

    @Transactional
    public void delete(String userId){
        try{
            User user = userRepository.findByUserId(userId).get();
            userRepository.delete(user);
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR");
        }
    }
}
