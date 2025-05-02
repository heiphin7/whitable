package com.api.whitable.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String username;
    private Boolean isAdmin;
    private Integer bookingCount;
    private Integer reviewCount;
}
