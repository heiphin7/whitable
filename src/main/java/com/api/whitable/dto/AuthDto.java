package com.api.whitable.dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class AuthDto {
    private String email;
    private String password;
}
