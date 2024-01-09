package com.example.whenwhere.Dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class GroupDto {

    private Integer id;

    private String groupName;

    private String attribute;

}
