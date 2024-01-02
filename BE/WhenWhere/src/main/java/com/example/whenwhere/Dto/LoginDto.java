package com.example.whenwhere.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
    @NotNull
    private String userId;

    @NotNull
    private String password;
}
