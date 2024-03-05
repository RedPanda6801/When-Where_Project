package com.example.whenwhere.Dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class BodyDto {

    String access_token;

    String refresh_token;

    String token_type;

    String expires_in;

    String refresh_token_in;
}
