package com.example.whenwhere.Dto;

import com.example.whenwhere.Entity.User;
import lombok.*;

@Data
@AllArgsConstructor
@ToString
@Builder
public class UserDto {

    private Integer id;

    private String userId;

    private String password;

    private String nickname;

    private boolean activated;

    public UserDto(){}

    public UserDto(Integer id, String userId, String nickname){
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
    }
    public static UserDto toDto(User entity) {
        return UserDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .password(entity.getPassword())
                .nickname(entity.getNickname())
                .activated(entity.isActivated())
                .build();
    }
}
