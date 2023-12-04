package com.example.whenwhere.Dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class BusytimeDto {
    // member들의 PK값만 받아오기
    private List<Integer> members;

    private Date startDate;

    private Date endDate;
}
