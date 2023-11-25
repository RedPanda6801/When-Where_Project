package com.example.whenwhere.Dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class GroupDto {

    private int id;

    private String groupName;

    private String attribute;

}
