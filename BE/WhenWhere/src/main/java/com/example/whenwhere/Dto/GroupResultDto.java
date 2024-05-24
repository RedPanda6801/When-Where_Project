package com.example.whenwhere.Dto;

import com.example.whenwhere.Entity.GroupResult;
import com.example.whenwhere.Entity.Schedule;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class GroupResultDto {
    private Integer id;

    private String restTitle;

    private String restAddress;

    private String restPhone;

    private String restHash;

    private String cafeTitle;

    private String cafeAddress;

    private String cafePhone;

    private String cafeHash;

    private String drinkTitle;

    private String drinkAddress;

    private String drinkPhone;

    private String drinkHash;

    private Integer groupId;

    // 시작 시간 및 종료 시간 추가
    private String startTime;

    private String endTime;

    private String resultAddress;

    public static GroupResultDto toDto(GroupResult entity){
        return GroupResultDto.builder()
                .id(entity.getId())
                .restTitle(entity.getRestTitle())
                .restAddress(entity.getRestAddress())
                .restHash(entity.getRestHash())
                .restPhone(entity.getRestPhone())
                .cafeTitle(entity.getCafeTitle())
                .cafeAddress(entity.getCafeAddress())
                .cafeHash(entity.getCafeHash())
                .cafePhone(entity.getCafePhone())
                .drinkTitle(entity.getDrinkTitle())
                .drinkAddress(entity.getDrinkAddress())
                .drinkHash(entity.getDrinkHash())
                .drinkPhone(entity.getDrinkPhone())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .resultAddress(entity.getResultAddress())
                .build();
    }

}
