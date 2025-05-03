package com.api.whitable.dto;

import lombok.Data;

@Data
public class ChangeUsernameEmailDto {
    private Long id;
    private String username;
    private String email;
}
