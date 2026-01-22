package com.example.whenwhere.Entity;

import com.example.whenwhere.Dto.GroupDto;
import com.example.whenwhere.Dto.GroupResultDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
// sql 내의 예약어인 group을 테이블 명으로 사용하고 싶다면 백틱으로 문자열을 감싸준다.
@Table(name = "`group_result`")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupResult {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="rest_title", length=100)
    private String restTitle;

    @Column(name="rest_address", length=100)
    private String restAddress;

    @Column(name="rest_phone", length=15)
    private String restPhone;

    @Column(name="rest_hash", length=100)
    private String restHash;

    @Column(name="cafe_title", length=100)
    private String cafeTitle;

    @Column(name="cafe_address", length=100)
    private String cafeAddress;

    @Column(name="cafe_phone", length=15)
    private String cafePhone;

    @Column(name="cafe_hash", length=100)
    private String cafeHash;

    @Column(name="drink_title", length=100)
    private String drinkTitle;

    @Column(name="drink_address", length=100)
    private String drinkAddress;

    @Column(name="drink_phone", length=15)
    private String drinkPhone;

    // 시작 시간 및 종료 시간 추가
    @Column(name="start_time")
    private String startTime;

    @Column(name="end_time")
    private String endTime;

    @Column(name="result_address")
    private String resultAddress;

    @Column(name="drink_hash", length=100)
    private String drinkHash;
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="group_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Group group;

    public GroupResult toEntity(GroupResultDto dto, Group group){
        return GroupResult.builder()
                .group(group)
                .cafeAddress(dto.getCafeAddress())
                .cafeHash(dto.getCafeHash())
                .cafeTitle(dto.getCafeTitle())
                .cafePhone(dto.getCafePhone())
                .restTitle(dto.getRestTitle())
                .restAddress(dto.getRestAddress())
                .restPhone(dto.getRestPhone())
                .restHash(dto.getRestHash())
                .drinkTitle(dto.getDrinkTitle())
                .drinkAddress(dto.getDrinkAddress())
                .drinkPhone(dto.getDrinkPhone())
                .drinkHash(dto.getDrinkHash())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .resultAddress(dto.getResultAddress())
                .build();
    }

}
