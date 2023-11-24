package com.example.whenwhere.Service;

import com.example.whenwhere.Dto.UserDto;
import com.example.whenwhere.Entity.User;
import com.example.whenwhere.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    // 예외처리 중복을 방지하기 위해 Optional 값만 사용
    // 외부(Controller)에서 사용하지 않고 내부에서 별도로 사용하는 Service
    public Optional<User> getUserById(Integer id){
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional;
    }

    public boolean existedUser(String userId){
        try{
            Optional<User> userOptional = userRepository.findByUserId(userId);
            if(userOptional.isPresent()){
                return true;
            }else{
                return false;
            }
        }catch(Exception e){
            // 404 에러로 처리하되, 서버에 로그 저장
            System.out.println(String.format("[Error] %s", e));
            return true;
        }
    }

    public boolean join(UserDto userDto){
        // userDto의 필수 값 확인
        if(
                userDto.getUserId() == null ||
                userDto.getNickname() == null ||
                userDto.getPassword() == null
        ){
            return false;
        }
        // 저장할 객체 세팅
        User userObj = new User();
        userObj = userObj.toEntity(userDto);

        try{
            userRepository.save(userObj);
        }catch(Exception e){
            System.out.println(String.format("[Error] %s", e));
            return false;
        }
        return true;
    }
}
