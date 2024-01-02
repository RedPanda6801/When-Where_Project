package com.example.whenwhere.Entity;

import com.example.whenwhere.Dto.UserDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "user")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @JsonIgnore
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="user_id", length=20, unique = true)
    private String userId;

    @Column(name="password", length = 100)
    private String password;

    @Column(name="nickname", length=50)
    private String nickname;

    @Column(name="location", length = 100)
    private String location;

    @JsonIgnore
    @Column(name="activated")
    private boolean activated;

    // 한 그룹의 호스트에 대한 연관관계 설정
    @JsonIgnore
    @OneToMany(mappedBy = "host", cascade = CascadeType.REMOVE)
    Set<Group> groups;

    // 한 유저가 여러 개의 스케줄을 등록할 수 있도록 연관관계 설정
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    List<Schedule> schedules;

    // 그룹과 유저 사이의 M:N 관계를 GroupMembers 엔터티로 설정
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    List<GroupMembers> groupMembers;

    // 그룹의 지원자에 대한 관계 설정
    @JsonIgnore
    @OneToMany(mappedBy = "applier", cascade = CascadeType.REMOVE)
    List<Apply> applies;

    @ManyToMany
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name="authority_name", referencedColumnName = "authority_name")}
    )
    private Set<Authority> authorities;

    public static User toEntity(UserDto dto){
        return User.builder()
                .userId(dto.getUserId())
                .nickname(dto.getNickname())
                .location(dto.getLocation())
                .password(dto.getPassword())
                .activated(true)
                .build();
    }

    public void update(@Nullable String password, @Nullable String nickname, @Nullable String location){
        this.setPassword(password);
        this.setNickname(nickname);
        this.setLocation(location);
    }

    public void updateAuthority(Set<Authority> authorities){
        this.setAuthorities(authorities);
    }
}
