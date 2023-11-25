package com.example.whenwhere.Dto;

import com.example.whenwhere.Entity.User;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class UserDto {

    private int id;

    private String userId;

    private String password;

    private String nickname;

    private String location;

    private boolean activated;

    public static UserDto toDto(User entity) {
        return UserDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .password(entity.getPassword())
                .nickname(entity.getNickname())
                .location(entity.getLocation())
                .build();
    }
}
