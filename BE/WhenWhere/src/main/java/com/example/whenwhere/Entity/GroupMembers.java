package com.example.whenwhere.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "group_members")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupMembers {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="group_name", length=20)
    private String groupName;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="group_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Group group;

    public GroupMembers toEntity(Apply apply){
        return GroupMembers.builder()
                .group(apply.getGroup())
                .groupName(apply.getGroup().getGroupName())
                .user(apply.getApplier())
                .build();
    }
}
