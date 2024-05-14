package com.example.whenwhere.Dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ApplyDto {

    private Integer id;

    // group의 pk값
    private Integer applyGroupId;

    private String applierId;

    private String applierNickname;
    // 처리 상태
    private Boolean state;

    // host의 결정
    private Boolean decide;
}
