package com.api.whitable.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingInfoDto {
    private Long id;
    private String name;
    private String status;
    private Integer guestCount;
    private String restaurantName;
    private LocalDateTime reservationTime;
}
