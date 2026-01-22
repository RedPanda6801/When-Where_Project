package com.example.whenwhere.Entity;

import com.example.whenwhere.Dto.GroupDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
// sql 내의 예약어인 group을 테이블 명으로 사용하고 싶다면 백틱으로 문자열을 감싸준다.
@Table(name = "`group`")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"host"})
public class Group {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="group_name", length=15, nullable = false)
    private String groupName;

    @Column(name="attribute", length=20)
    private String attribute;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="host_id")
    private User host;

    // 그룹과 유저 사이의 M:N 관계를 GroupMembers 엔터티로 설정
    @OneToMany(mappedBy = "group", cascade = CascadeType.REMOVE)
    List<GroupMembers> groupMembers;

    // 그룹의 지원자에 대한 관계 설정
    @OneToMany(mappedBy = "group", cascade = CascadeType.REMOVE)
    List<Apply> applies;

    @OneToOne(mappedBy = "group", cascade = CascadeType.REMOVE)
    GroupResult groupResult;

    public Group toEntity(GroupDto dto){
        return Group.builder()
                .groupName(dto.getGroupName())
                .attribute(dto.getAttribute())
                .build();
    }

    public void update(String groupName, String attribute){
        this.setGroupName(groupName);
        this.setAttribute(attribute);
    }
}
