package com.example.whenwhere.Dto;

import lombok.*;
import org.springframework.security.core.userdetails.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class UserDto {

    private Long id;

    private String userId;

    private String password;

    private String nickname;

    private String location;

    private boolean activated;

//    public static UserDto toDto(User entity) {
//        return UserDto.builder()
//                .id(entity.getId())
//                .userId(entity.getUserId())
//                .password(entity.getPassword())
//                .nickname(entity.getUsername())
//                .location(entity.getLocation())
//                .activated(entity.getAuthorities())
//                .build();
//    }
}
