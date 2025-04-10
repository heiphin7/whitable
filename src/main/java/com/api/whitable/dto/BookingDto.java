package com.api.whitable.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingDto {
    private LocalDate reservationDate;
    private String reservationTime;
    private Integer guestCount;
    private String note;
}
